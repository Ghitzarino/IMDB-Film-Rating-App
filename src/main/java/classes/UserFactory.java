package classes;

import enums.AccountType;

public class UserFactory {
    public static User createUser(AccountType accountType, User.Information information, String username) {
        return switch (accountType) {
            case REGULAR -> new Regular(accountType, information, username);
            case CONTRIBUTOR -> new Contributor(accountType, information, username);
            case ADMIN -> new Admin(accountType, information, username);
            default -> throw new IllegalArgumentException("Invalid account type");
        };
    }
}
