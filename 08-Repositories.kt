@Repository
interface AccountRepository : CoroutineCrudRepository<Account, UUID>

@Repository
interface AccountUserRepository : CoroutineCrudRepository<AccountUser, UUID>

@Repository
interface UserRepository : CoroutineCrudRepository<User, UUID> {
    suspend fun existsByEmail(email: String): Boolean

    @Modifying
    @Query("UPDATE auth_users SET last_logged_on = NOW() WHERE id = :id")
    suspend fun updateLastLoggedOn(id: UUID)

    @Modifying
    @Query("UPDATE auth_users SET is_email_verified = true WHERE id = :id")
    suspend fun updateEmailVerified(id: UUID?)
}

@Repository
interface UserTokenRepository : CoroutineCrudRepository<UserToken, UUID> {
    suspend fun findByValueAndExpiresOnIsAfterAndType(value: String, now: LocalDateTime, type: UserTokenType): UserToken?

    suspend fun deleteByUserIdAndType(userId: UUID, type: UserTokenType)

    suspend fun deleteByValue(value: String)
}
