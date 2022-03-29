@Configuration(proxyBeanMethods = false)
class Routes {
    @Bean
    fun route(
        authController: AuthController,
        userController: UserController,
        accountController: AccountController,
    ) = docCoRouter {
        // @formatter:off
        GET("/", { permanentRedirect(URI.create("/swagger-ui.html")).buildAndAwait() }, { op -> op
            .operationId("swagger")
            .summary("Redirect to Swagger UI.")
            .hidden(true)
        })
        "/auth".nest({
            GET("/public-key", authController::publicKey) { op -> op
                .tag("Auth")
                .operationId("publicKey")
                .summary("Fetch a public key for JWT token verification.")
                .response(jsonResponse(HttpStatus.OK, String::class))
            }
            POST("/token", authController::login) { op -> op
                .tag("Auth")
                .operationId("token")
                .summary("Issue an access and refresh token pair (JWT).")
                .requestBody(jsonRequestBody(GrantPasswordTokenUsecase.Command::class))
                .response(jsonResponse(HttpStatus.OK, GrantPasswordTokenUsecase.TokensDto::class)
                .header(headerBuilder().name("Set-Cookie").description(AuthController.REFRESH_TOKEN_COOKIE)))
            }
            POST("/refresh", authController::refresh) { op -> op
                .tag("Auth")
                .operationId("refresh")
                .summary("Refresh an access token (JWT).")
                .parameter(cookieParameter(AuthController.REFRESH_TOKEN_COOKIE))
                .response(jsonResponse(HttpStatus.OK, GrantRefreshTokenUsecase.TokensDto::class))
            }
            POST("/logout", authController::logout) { op -> op
                .tag("Auth")
                .operationId("logout")
                .summary("Destroy refresh token and cookie.")
                .parameter(cookieParameter(AuthController.REFRESH_TOKEN_COOKIE))
                .response(emptyResponse(HttpStatus.OK))
            }
        })
        "/users".nest({
            POST("/signup", userController::signup) { op -> op
                .tag("Users")
                .operationId("signup")
                .summary("Signup a new user.")
                .requestBody(jsonRequestBody(SignupUserUsecase.Command::class))
                .response(emptyResponse(HttpStatus.OK))
            }
            POST("/verify", userController::requestEmailVerification) { op -> op
                .tag("Users")
                .operationId("requestEmailVerification")
                .summary("Request a current user's email address verification. The system sends an email w/ a unique link that user needs to open.")
                .response(emptyResponse(HttpStatus.OK))
            }
            POST("/verify/{token}", userController::confirmEmailVerification) { op -> op
                .tag("Users")
                .operationId("confirmEmailVerification")
                .summary("Confirm user email address by activating user sent unique link.")
                .parameter(pathParameter("token"))
                .response(emptyResponse(HttpStatus.OK))
            }
            GET("/me", userController::me) { op -> op
                .tag("Users")
                .operationId("profile")
                .summary("Get details of current user.")
                .response(jsonResponse(HttpStatus.OK, UserProfileDto::class))
            }
        })
        "/accounts".nest({
            GET("", accountController::index) { op -> op
                .tag("Account")
                .operationId("index")
                .summary("List my accounts.")
                .response(jsonArrayResponse(HttpStatus.OK, AccountUserDetailsDto::class))
            }
            POST("", accountController::create) { op -> op
                .tag("Account")
                .operationId("create")
                .summary("Create a new account.")
                .requestBody(jsonRequestBody(CreateAccountUsecase.Command::class))
                .response(jsonResponse(HttpStatus.OK, AccountUserDetailsDto::class))
            }
            POST("/{id}/select", accountController::select) { op -> op
                .tag("Account")
                .operationId("select")
                .summary("Select an existing account.")
                .parameter(pathParameter("id"))
                .response(jsonResponse(HttpStatus.OK, AccountUserDetailsDto::class))
            }
            GET("/current", accountController::current) { op -> op
                .tag("Account")
                .operationId("current")
                .summary("View current account details.")
                .response(jsonResponse(HttpStatus.OK, AccountUserDetailsDto::class))
            }
        })
        // @formatter:on
    }

    @Bean
    fun authorizeExchangeConfiguration(): AuthorizeExchangeDsl.() -> Unit =
        {
            authorize(pathMatchers("/v3/api-docs/**", "/swagger-ui.html", "/webjars/**", "/"), permitAll)
            authorize(pathMatchers("/actuator/**", "/tasks/**"), permitAll)
            authorize(pathMatchers("/auth/public-key", "/auth/token", "/auth/refresh", "/users/signup", "/users/verify/**"), permitAll)
            authorize()
        }
}
