@Table("auth_user_tokens")
@Getter
public class UserToken extends BaseEntity {
    private static final StringKeyGenerator TOKEN_GENERATOR = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    @Column("user_id")
    private UUID userId;

    @Column("type")
    private UserTokenType type;

    @Column("value")
    private String value;

    @Column("expires_on")
    private OffsetDateTime expiresOn;

    @PersistenceConstructor
    public UserToken(@NonNull UUID userId, @NonNull UserTokenType type) {
        this.userId = userId;
        this.type = type;
        this.value = TOKEN_GENERATOR.generateKey();
        this.expiresOn = OffsetDateTime.now().plus(type.getExpiresIn());
    }

    public boolean isValid() {
        return expiresOn.isAfter(OffsetDateTime.now());
    }
}

