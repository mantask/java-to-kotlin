@Table("auth_user_tokens")
class UserToken(
    @field:Column("user_id") val userId: UUID,
    @field:Column("type") val type: UserTokenType,
) : AuditableEntity<UUID>() {

    @field:Column("value")
    val value: String = tokenGenerator.generateKey()

    @field:Column("expires_on")
    val expiresOn: LocalDateTime = LocalDateTime.now().plus(type.expiresIn)

    val valid: Boolean
        get() = expiresOn.isAfter(LocalDateTime.now())

    companion object {
        private val tokenGenerator: StringKeyGenerator = Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96)
    }
}

