public interface UserEvent extends DomainEvent {
}

@Data
@Accessors(chain = true)
public class UserEmailChangedEvent implements UserEvent {
    private String newEmail;
    private String oldEmail;
    private String token;
}

@Data
@Accessors(chain = true)
public class UserEmailConfirmedEvent implements UserEvent {
    private String email;
}

@Data
@Accessors(chain = true)
public class UserLoggedEvent implements UserEvent {
    private String email;
    private String ip;
}

@Data
@Accessors(chain = true)
public class UserSignedEvent implements UserEvent {
    private String name;
    private String email;
}

