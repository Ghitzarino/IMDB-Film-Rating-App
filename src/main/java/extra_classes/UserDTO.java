package extra_classes;

import classes.User;
import enums.AccountType;
import org.w3c.dom.ls.LSException;

import java.util.List;

public class UserDTO {
    public String username;
    public int experience;
    public User.Information information;
    public AccountType userType;
    public List<String> productionsContribution;
    public List<String> actorsContribution;
    public List<String> favoriteProductions;
    public List<String> favoriteActors;
    public List<String> notifications;

    public UserDTO(String username, int experience, User.Information information,
                   AccountType userType, List<String> productionsContribution,
                   List<String> actorsContribution, List<String> favoriteProductions,
                   List<String> favoriteActors, List<String> notifications) {
        this.username = username;
        this.experience = experience;
        this.information = information;
        this.userType = userType;
        this.productionsContribution = productionsContribution;
        this.actorsContribution = actorsContribution;
        this.favoriteProductions = favoriteProductions;
        this.favoriteActors = favoriteActors;
        this.notifications = notifications;
    }
}
