@Validated
@ConstructorBinding
@ConfigurationProperties("app.jwt")
data class JwtProps(
    @field:NotNull val accessTokenExpiresMin: Int,
)

@Validated
@ConstructorBinding
@ConfigurationProperties("app.keystore")
data class KeystoreProps(
    @field:NotBlank val alias: String,
    @field:NotBlank val path: String,
    @field:NotBlank val password: String,
)
