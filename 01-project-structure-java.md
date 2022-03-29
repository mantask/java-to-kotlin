* .auth
  * AuthApplication.java

* .auth.config
  * KafkaConfig.java
  * R2dbcConfig.java
  * SecurityConfig.java
  * WebConfig.java

* .auth.config.props
  * JwtProps.java
  * KeystoreProps.java

* .auth.domain
  * Account.java
  * AccountUser.java
  * ExternalIdentity.java
  * IdentityProvider.java
  * User.java
  * UserToken.java

* .auth.process
  * UserSignupProcessManager.java

* .auth.projection
  * AccountUserDetailsDto.java
  * SqlQueryBuilders.java
  * UserDetailsDto.java
  * UserProfileDto.java

* .auth.repository
  * AccountRepository.java
  * AccountUserRepository.java
  * UserRepository.java
  * UserTokenRepository.java

* .auth.service
  * JwtFactory.java

* .auth.usecase.account
  * CreateAccountUsecase.java
  * GetCurrentAccountUsecase.java
  * ListMyAccountsUsecase.java
  * SelectAccountUsecase.java

* .auth.usecase.token
  * GetTokenKeyUsecase.java
  * GrantPasswordTokenUsecase.java
  * GrantRefreshTokenUsecase.java
  * LogoutUserUsecase.java

* .auth.usecase.user
  * ConfirmUserEmailVerificationUsecase.java
  * GetUserProfileUsecase.java
  * RequestUserEmailVerificationUsecase.java
  * SignupUserUsecase.java

* .auth.util.constraint
  * CustomSpringMessageResolver.java
  * ValidPassword.java
  * ValidPasswordValidator.java

* .auth.web
  * AccountController.java
  * AuthController.java
  * TaskController.java

