* .auth
  * App.kt

* .auth.config
  * Config.kt
  * Constraints.kt
  * KTopics.kt
  * Props.kt
  * Routes.kt

* .auth.domain
  * Account.kt
  * AccountUser.kt
  * ExternalIdentity.kt
  * IdentityProvider.kt
  * User.kt
  * UserToken.kt

* .auth.process
  * UserSignupProcessManager.kt

* .auth.repository
  * Projections.kt
  * Repositories.kt
  * SqlQueryBuilders.kt
  * SqlQueryRepository.kt

* .auth.service
  * JwtFactory.kt

* .auth.usecase.account
  * CreateAccountUsecase.kt
  * GetCurrentAccountUsecase.kt
  * ListMyAccountsUsecase.kt
  * SelectAccountUsecase.kt

* .auth.usecase.token
  * GrantPasswordTokenUsecase.kt
  * GrantRefreshTokenUsecase.kt
  * LogoutUserUsecase.kt

* .auth.usecase.user
  * ConfirmUserEmailVerificationUsecase.kt
  * GetUserProfileUsecase.kt
  * RequestUserEmailVerificationUsecase.kt
  * SignupUserUsecase.kt

* .auth.web
  * AccountController.kt
  * AuthController.kt
  * TaskController.kt
