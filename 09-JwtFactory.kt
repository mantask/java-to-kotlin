@Service
class JwtFactory(
    private val tokenKeyRsa: RSAKey,
    private val props: JwtProps,
) {
    fun buildAccessToken(user: UserDetailsDto): String {
        val claims = JWTClaimsSet.Builder()
            .audience("app")
            .issuer("Auth-Service")
            .issueTime(Date())
            .expirationTime(Date().addMinutes(props.accessTokenExpiresMin))
            .subject(user.id.toString())
            .claim("name", user.name)
            .claim("perm", user.permissions)
        if (user.lastAccountId != null) {
            claims.claim(
                "acc", mapOf(
                    "id" to user.lastAccountId?.toString(),
                    "name" to user.lastAccountName,
                    "role" to user.lastAccountRole?.name,
                )
            )
        }
        val jwt = SignedJWT(buildHeader(), claims.build())
        jwt.sign(RSASSASigner(tokenKeyRsa))
        return jwt.serialize()
    }

    fun buildRefreshToken(value: String?): String {
        val jwt = JWSObject(buildHeader(), Payload(value))
        jwt.sign(RSASSASigner(tokenKeyRsa))
        return jwt.serialize()
    }

    fun parseRefreshToken(value: String?): String? {
        val jwt = JWSObject.parse(value)
        if (!jwt.verify(RSASSAVerifier(tokenKeyRsa))) return null
        return jwt.payload.toString()
    }

    private fun buildHeader() =
        JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(tokenKeyRsa.keyID)
            .build()

    private fun Date.addMinutes(minutes: Int) =
        DateUtils.addMinutes(this, minutes)
}
