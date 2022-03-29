@Service
@AllArgsConstructor
public class GrantPasswordTokenUsecase {
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtFactory jwtFactory;
    private final SqlQueryRepository sqlQueryRepository;
    private final DomainEventPublisher publisher;

    @Transactional
    public Mono<TokensDto> execute(@NonNull Command cmd) {
        return Mono.just(new Context(cmd))
            .flatMap(this::findUserWithPassword)
            .doOnNext(ctx -> ctx.accessToken = jwtFactory.buildAccessToken(ctx.userDetails))
            .flatMap(this::buildRefreshToken)
            .flatMap(this::publishEvent)
            .flatMap(this::setLastLoggedOn)
            .map(Context::toTokens);
    }

    private Mono<Context> findUserWithPassword(Context ctx) {
        return sqlQueryRepository.findUserDetailsByEmail(ctx.cmd.getUsername())
            .filter(user -> passwordEncoder.matches(ctx.cmd.getPassword(), user.getPasswordHash()))
            .doOnNext(user -> ctx.userDetails = user)
            .thenReturn(ctx);
    }

    public Mono<Context> buildRefreshToken(Context ctx) {
        return userTokenRepository.deleteByUserIdAndType(ctx.userDetails.getId(), UserTokenType.REFRESH)
            .thenReturn(new UserToken(ctx.userDetails.getId(), UserTokenType.REFRESH))
            .flatMap(userTokenRepository::save)
            .doOnNext(userToken -> ctx.refreshToken = jwtFactory.buildRefreshToken(userToken.getValue()))
            .thenReturn(ctx);
    }

    public Mono<Context> publishEvent(Context ctx) {
        return publisher.publish(ctx.userDetails,
                new UserLoggedEvent()
                    .setEmail(ctx.userDetails.getEmail())
                    .setIp(ctx.cmd.ip),
                ctx.userDetails.getId())
            .thenReturn(ctx);
    }

    public Mono<Context> setLastLoggedOn(Context ctx) {
        return userRepository.updateLastLoggedOn(ctx.userDetails.getId())
            .thenReturn(ctx);
    }

    @RequiredArgsConstructor
    private static class Context {
        final Command cmd;
        UserDetailsDto userDetails;
        String accessToken;
        String refreshToken;

        TokensDto toTokens() {
            return new TokensDto()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
        }
    }

    @Schema(name = "LoginUserCommand")
    @Data
    @Accessors(chain = true)
    public static class Command {
        @NotBlank
        @Email
        private String username;

        @NotBlank
        private String password;

        @JsonIgnore
        private String ip;
    }

    @Schema(name = "TokenResponse")
    @Data
    @Accessors(chain = true)
    public static class TokensDto {
        private String accessToken;
        @JsonIgnore
        private String refreshToken;
    }
}

