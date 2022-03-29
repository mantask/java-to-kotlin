@Service
@AllArgsConstructor
public class AuthController {
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private final AuthContextFactory authContextFactory;
    private final RequestValidator requestValidator;
    private final GetTokenKeyUsecase getTokenKeyUsecase;
    private final GrantPasswordTokenUsecase grantPasswordTokenUsecase;
    private final GrantRefreshTokenUsecase grantRefreshTokenUsecase;
    private final SignupUserUsecase signupUserUsecase;
    private final RequestUserEmailVerificationUsecase requestUserEmailVerificationUsecase;
    private final LogoutUserUsecase logoutUserUsecase;
    private final ConfirmUserEmailVerificationUsecase confirmUserEmailVerificationUsecase;
    private final GetUserProfileUsecase getUserProfileUsecase;

    public Mono<ServerResponse> tokenKey(ServerRequest request) {
        return getTokenKeyUsecase.execute()
            .flatMap(it -> ServerResponse.ok()
                .headers(headers -> headers.setContentType(new MediaType("application", "x-pem-file")))
                .bodyValue(it));
    }

    public Mono<ServerResponse> login(ServerRequest req) {
        return req.bodyToMono(GrantPasswordTokenUsecase.Command.class)
            .flatMap(requestValidator::validate)
            .doOnNext(cmd -> cmd.setIp(req.remoteAddress()
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .orElse(null)))
            .flatMap(grantPasswordTokenUsecase::execute)
            .flatMap(it -> ServerResponse.ok()
                .cookie(ResponseCookie.from(REFRESH_TOKEN_COOKIE, it.getRefreshToken())
                    .httpOnly(true)
                    .path("/api/auth/refresh")
                    .build())
                .bodyValue(it));
    }

    public Mono<ServerResponse> logout(ServerRequest req) {
        return Mono.just(Optional.ofNullable(req.cookies().getFirst(REFRESH_TOKEN_COOKIE))
                .map(HttpCookie::getValue)
                .orElseThrow(IllegalArgumentException::new))
            .flatMap(logoutUserUsecase::execute)
            .then(ServerResponse.ok()
                .cookie(ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                    .httpOnly(true)
                    .path("/api/auth/refresh")
                    .maxAge(0)
                    .build())
                .build());
    }

    public Mono<ServerResponse> refresh(ServerRequest req) {
        return Mono.just(Optional.ofNullable(req.cookies().getFirst(REFRESH_TOKEN_COOKIE))
                .map(HttpCookie::getValue)
                .orElseThrow(IllegalArgumentException::new))
            .flatMap(grantRefreshTokenUsecase::execute)
            .flatMap(it -> ServerResponse.ok().bodyValue(it));
    }

    public Mono<ServerResponse> signup(ServerRequest req) {
        return req.bodyToMono(SignupUserUsecase.Command.class)
            .flatMap(requestValidator::validate)
            .flatMap(signupUserUsecase::execute)
            .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> requestEmailVerification(ServerRequest req) {
        return authContextFactory.build(req)
            .flatMap(auth -> requestUserEmailVerificationUsecase.execute(auth.getUserId()))
            .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> confirmEmailVerification(ServerRequest req) {
        return req.bodyToMono(ConfirmUserEmailVerificationUsecase.Command.class)
            .flatMap(requestValidator::validate)
            .flatMap(confirmUserEmailVerificationUsecase::execute)
            .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> profile(ServerRequest req) {
        return authContextFactory.build(req)
            .flatMap(getUserProfileUsecase::execute)
            .flatMap(it -> ServerResponse.ok().bodyValue(it));
    }
}

