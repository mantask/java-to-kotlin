@Repository
public interface AccountRepository extends R2dbcRepository<Account, UUID> {
}

@Repository
public interface AccountUserRepository extends R2dbcRepository<AccountUser, UUID> {
}

@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {
    Mono<Boolean> existsByEmail(String email);

    @Modifying
    @Query("UPDATE auth_users SET last_logged_on = NOW() WHERE id = :id")
    Mono<Void> updateLastLoggedOn(UUID id);

    @Modifying
    @Query("UPDATE auth_users SET is_email_verified = true WHERE id = :id")
    Mono<Void> updateEmailVerified(UUID id);
}

@Repository
public interface UserTokenRepository extends R2dbcRepository<UserToken, UUID> {
    Mono<UserToken> findByValueAndExpiresOnIsAfterAndType(String value, ZonedDateTime now, UserTokenType type);

    Mono<Void> deleteByUserIdAndType(UUID userId, UserTokenType type);

    Mono<Void> deleteByValue(String value);
}

