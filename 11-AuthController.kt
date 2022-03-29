@Service
class AuthController(
    private val beanValidator: BeanValidator,
    private val tokenKeyPublic: PublicKey,
    private val grantPasswordTokenUsecase: GrantPasswordTokenUsecase,
    private val grantRefreshTokenUsecase: GrantRefreshTokenUsecase,
    private val logoutUserUsecase: LogoutUserUsecase,
) {
    suspend fun publicKey(req: ServerRequest): ServerResponse {
        return ServerResponse.ok()
            .headers { it.contentType = MediaType("application", "x-pem-file") }
            .bodyValueAndAwait(tokenKeyPublic.toPemString())
    }

    suspend fun login(req: ServerRequest): ServerResponse {
        val cmd = req.awaitBody<GrantPasswordTokenUsecase.Command>().apply {
            ip = req.remoteAddressOrNull()?.address?.hostAddress
        }
        beanValidator.valid(cmd)
        val tokens = grantPasswordTokenUsecase.execute(cmd)
        return ServerResponse.ok()
            .cookie(
                ResponseCookie.from(REFRESH_TOKEN_COOKIE, tokens.refreshToken)
                    .httpOnly(true)
                    .path("/api/auth/refresh")
                    .build()
            )
            .bodyValueAndAwait(tokens)
    }

    suspend fun refresh(req: ServerRequest): ServerResponse {
        val refreshToken = req.cookies().getFirst(REFRESH_TOKEN_COOKIE)!!.value
        val tokens = grantRefreshTokenUsecase.execute(refreshToken)
        return ServerResponse.ok().bodyValueAndAwait(tokens)
    }

    suspend fun logout(req: ServerRequest): ServerResponse {
        val refreshToken = req.cookies().getFirst(REFRESH_TOKEN_COOKIE)!!.value
        logoutUserUsecase.execute(refreshToken)
        return ServerResponse.ok()
            .cookie(
                ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                    .httpOnly(true)
                    .path("/api/auth/refresh")
                    .maxAge(0)
                    .build()
            )
            .buildAndAwait()
    }

    companion object {
        const val REFRESH_TOKEN_COOKIE = "refreshToken"
    }
}
