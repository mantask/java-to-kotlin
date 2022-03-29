@Service
class GrantPasswordTokenUsecase(
    private val userRepository: UserRepository,
    private val userTokenRepository: UserTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtFactory: JwtFactory,
    private val sqlQueryRepository: SqlQueryRepository,
    private val publisher: DomainEventPublisher,
) {
    @Transactional
    suspend fun execute(cmd: Command): TokensDto {
        val userDetails = sqlQueryRepository.findUserDetailsByEmail(cmd.username!!) ?: throw PasswordDontMatchException()
        if (!passwordEncoder.matches(cmd.password, userDetails.passwordHash)) throw PasswordDontMatchException()
        userRepository.updateLastLoggedOn(userDetails.id)
        publisher.publish(userDetails, UserLoggedEvent(userDetails.email, cmd.ip))
        return TokensDto(
            accessToken = jwtFactory.buildAccessToken(userDetails),
            refreshToken = buildRefreshToken(userDetails.id),
        )
    }

    suspend fun buildRefreshToken(userId: UUID): String {
        userTokenRepository.deleteByUserIdAndType(userId, UserTokenType.REFRESH)
        val token = userTokenRepository.save(UserToken(userId, UserTokenType.REFRESH))
        return jwtFactory.buildRefreshToken(token.value)
    }

    @Schema(name = "LoginUserCommand")
    data class Command(
        @field:NotBlank @field:Email var username: String?,
        @field:NotBlank var password: String?,
        @field:JsonIgnore var ip: String?,
    )

    @Schema(name = "TokenResponse")
    data class TokensDto(
        private var accessToken: String,
        @JsonIgnore var refreshToken: String,
    )

    @ResponseStatus(HttpStatus.FORBIDDEN)
    class PasswordDontMatchException : Throwable("Password do not match")
}

