interface UserEvent : DomainEvent

data class UserEmailChangedEvent(
    val newEmail: String,
    val oldEmail: String,
    val token: String,
) : UserEvent

data class UserEmailConfirmedEvent(
    val email: String,
) : UserEvent

data class UserLoggedEvent(
    val email: String,
    val ip: String?,
) : UserEvent

data class UserSignedEvent(
    val name: String,
    val email: String,
) : UserEvent

