@Configuration
public class UserSignupProcessManager {

    @Bean
    public DomainEventListener<UserSignedEvent> onUserSigned(RequestUserEmailVerificationUsecase requestEmailConfirmationUsecase) {
        return DomainEventListener.create(UserSignedEvent.class, ee ->
            requestEmailConfirmationUsecase.execute(ee.getUserId()));
    }
}

