@EnableWebFluxDefaults
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
public class WebConfig {
    private final AuthController authController;
    private final AccountController accountController;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route()
            .GET("/", req -> ServerResponse.permanentRedirect(URI.create("/swagger-ui.html")).build())
            .path("/auth", () -> SpringdocRouteBuilder.route()
                .GET("/token-key", authController::tokenKey, op -> op
                    .tag("Authentication")
                    .operationId("tokenKey")
                    .summary("Fetch a public key for JWT token verification.")
                    .response(jsonResponse(HttpStatus.OK, String.class)))
                .POST("/login", authController::login, op -> op
                    .tag("Authentication")
                    .operationId("login")
                    .summary("Issue an access and refresh token pair (JWT).")
                    .requestBody(jsonRequestBody(GrantPasswordTokenUsecase.Command.class))
                    .response(jsonResponse(HttpStatus.OK, GrantPasswordTokenUsecase.TokensDto.class)
                        .header(headerBuilder().name("Set-Cookie").description(AuthController.REFRESH_TOKEN_COOKIE))))
                .POST("/refresh", authController::refresh, op -> op
                    .tag("Authentication")
                    .operationId("refresh")
                    .summary("Refresh an access token (JWT).")
                    .parameter(cookieParameter(AuthController.REFRESH_TOKEN_COOKIE))
                    .response(jsonResponse(HttpStatus.OK, GrantRefreshTokenUsecase.TokensDto.class)))
                .DELETE("/refresh", authController::logout, op -> op
                    .tag("Authentication")
                    .operationId("logout")
                    .summary("Destroy refresh token and cookie.")
                    .parameter(cookieParameter(AuthController.REFRESH_TOKEN_COOKIE))
                    .response(emptyResponse(HttpStatus.OK)))
                .POST("/signup", authController::signup, op -> op
                    .tag("Authentication")
                    .operationId("signup")
                    .summary("Signup a new user.")
                    .requestBody(jsonRequestBody(SignupUserUsecase.Command.class))
                    .response(emptyResponse(HttpStatus.OK)))
                .POST("/verify", authController::requestEmailVerification, op -> op
                    .tag("Authentication")
                    .operationId("requestEmailVerification")
                    .summary("Request a current user's email address verification. The system sends an email w/ a unique link that user needs to open.")
                    .response(emptyResponse(HttpStatus.OK)))
                .PUT("/verify", authController::confirmEmailVerification, op -> op
                    .tag("Authentication")
                    .operationId("confirmEmailVerification")
                    .summary("Confirm user email address by activating user sent unique link.")
                    .requestBody(jsonRequestBody(ConfirmUserEmailVerificationUsecase.Command.class))
                    .response(emptyResponse(HttpStatus.OK)))
                .GET("/profile", authController::profile, op -> op
                    .tag("Authentication")
                    .operationId("profile")
                    .summary("Get details of current user.")
                    .response(jsonResponse(HttpStatus.OK, UserProfileDto.class)))
                .build())
            .path("/accounts", () -> SpringdocRouteBuilder.route()
                .GET("", accountController::index, op -> op
                    .tag("Account")
                    .operationId("index")
                    .summary("List my accounts.")
                    .response(jsonArrayResponse(HttpStatus.OK, AccountUserDetailsDto.class)))
                .POST("", accountController::create, op -> op
                    .tag("Account")
                    .operationId("create")
                    .summary("Create a new account.")
                    .requestBody(jsonRequestBody(CreateAccountUsecase.Command.class))
                    .response(jsonResponse(HttpStatus.OK, AccountUserDetailsDto.class)))
                .POST("/{id}/select", accountController::select, op -> op
                    .tag("Account")
                    .operationId("select")
                    .summary("Select an existing account.")
                    .parameter(pathParameter("id"))
                    .response(jsonResponse(HttpStatus.OK, AccountUserDetailsDto.class)))
                .GET("/current", accountController::current, op -> op
                    .tag("Account")
                    .operationId("current")
                    .summary("View current account details.")
                    .response(jsonResponse(HttpStatus.OK, AccountUserDetailsDto.class)))
                .build())
            .build();
    }
}

@EnableSecurityDefaults
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    @Bean
    public Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> authorizeExchangeCustomizer() {
        return exchanges -> exchanges
            .pathMatchers("/v3/api-docs/**", "/swagger-ui.html", "/webjars/**", "/").permitAll()
            .pathMatchers("/actuator/**", "/tasks/**").permitAll()
            .pathMatchers("/auth/token-key", "/auth/login", "/auth/refresh", "/auth/signup", "/auth/verify").permitAll()
            .anyExchange().authenticated();
    }
}

