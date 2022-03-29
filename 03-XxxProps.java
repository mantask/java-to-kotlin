@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("app.jwt")
@Validated
public class JwtProps {
    @NotNull
    private Integer accessTokenExpiresMin;
}

@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("app.keystore")
@Validated
public class KeystoreProps {
    @NotBlank
    private String alias;
    @NotBlank
    private String path;
    @NotBlank
    private String password;
}

