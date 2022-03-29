@Service
@AllArgsConstructor
public class JwtFactory {
    private final RSAKey tokenKeyRsa;
    private final JwtProps jwts;

    public String buildAccessToken(@NonNull UserDetailsDto user) {
        try {
            var claims = new JWTClaimsSet.Builder()
                .audience("app")
                .issuer("Auth-Service")
                .issueTime(new Date())
                .expirationTime(DateUtils.addMinutes(new Date(), jwts.getAccessTokenExpiresMin()))
                .subject(user.getId().toString())
                .claim("name", user.getName())
                .claim("perm", user.getPermissions());
            if (user.getLastAccountId() != null) claims.claim("acc", Map.of(
                "id", user.getLastAccountId().toString(),
                "name", user.getLastAccountName(),
                "role", user.getLastAccountRole()));
            var jwt = new SignedJWT(buildHeader(), claims.build());
            jwt.sign(new RSASSASigner(tokenKeyRsa));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildRefreshToken(String value) {
        try {
            var jwt = new JWSObject(buildHeader(), new Payload(value));
            jwt.sign(new RSASSASigner(tokenKeyRsa));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String parseRefreshToken(String value) {
        try {
            var jwt = JWSObject.parse(value);
            var verified = jwt.verify(new RSASSAVerifier(tokenKeyRsa));
            if (!verified) return null;
            return jwt.getPayload().toString();
        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private JWSHeader buildHeader() {
        return new JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(tokenKeyRsa.getKeyID())
            .build();
    }
}

