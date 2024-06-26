package classes;

import enums.AccountType;
import enums.RequestTypes;
import interfaces.RequestsManager;
import interfaces.SortableElement;

import java.util.ArrayList;
import java.util.List;

public class Regular extends User implements RequestsManager {
    // Constructors
    public Regular() {
    }
    public Regular(AccountType accountType, Information information, String username) {
        super(accountType, information, username);
    }

    public List<Rating> getUserReviews(List<Production> productions) {
        List<Rating> userRatings = new ArrayList<>();

        for (Production production : productions) {
            for (Rating rating : production.getRatings()) {
                if (rating.getUsername().equals(this.getUsername())) {
                    userRatings.add(rating);
                }
            }
        }

        return userRatings;
    }

    // Implementation of the createRequest method
    @Override
    public void createRequest(Request r) {
        IMDB imdb = IMDB.getInstance();

        if (r.getType().equals(RequestTypes.ACTOR_ISSUE) || r.getType().equals(RequestTypes.MOVIE_ISSUE)) {
            // Search for the user that created the production/actor
            for (User user : imdb.users) {
                if (user instanceof Staff) {
                    for (SortableElement elem : ((Staff) user).getAddedElements()) {
                        if (elem.getName().equals(r.getTitle())) {
                            r.setTo(user.getUsername());

                            // Notify user
                            user.addNotification("New request " + r.getType() +
                                    " added from user \"" + r.getUsername() + "\" for you.");

                            ((Staff) user).addUserRequest(r);
                            imdb.requests.add(r);
                            return;
                        }
                    }
                }
            }
        }
        Admin.RequestsHolder.addRequest(r);
        imdb.requests.add(r);
    }

    // Implementation of the removeRequest method
    @Override
    public void removeRequest(Request r) {
        IMDB imdb = IMDB.getInstance();

        if (!r.getType().equals(RequestTypes.DELETE_ACCOUNT) && !r.getType().equals(RequestTypes.OTHERS)) {
            // Search for the user that created the production/actor
            for (User user : imdb.users) {
                if (user instanceof Staff) {
                    for (SortableElement elem : ((Staff) user).getAddedElements()) {
                        if (elem.getName().equals(r.getTitle())) {
                            ((Staff) user).removeUserRequest(r);
                            imdb.requests.remove(r);
                            return;
                        }
                    }
                }
            }
        }
        Admin.RequestsHolder.removeRequest(r);
        imdb.requests.remove(r);
    }
}