@Configuration(proxyBeanMethods = false)
class UserSignupProcessManager {
    @Bean
    fun onUserSigned(requestEmailConfirmationUsecase: RequestUserEmailVerificationUsecase) =
        domainEventListener<UserSignedEvent> {
            requestEmailConfirmationUsecase.execute(it.userId!!)
        }
}
