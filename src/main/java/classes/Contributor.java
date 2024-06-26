package classes;

import enums.AccountType;
import enums.RequestTypes;
import interfaces.RequestsManager;
import interfaces.SortableElement;

import java.util.List;
import java.util.SortedSet;

public class Contributor extends Staff implements RequestsManager {
    // Constructors
    public Contributor() {
    }
    public Contributor(AccountType accountType, Information information, String username) {
        super(accountType, information, username);
    }

    public Contributor(AccountType accountType, Information information, String username, List<Request> userRequests,
                 SortedSet<SortableElement> addedElements) {
        super(accountType, information, username, userRequests, addedElements);
    }

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
                                    " added from user \"" + r.getUsername() + "\"");

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
