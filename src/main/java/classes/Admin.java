package classes;

import enums.AccountType;
import enums.RequestTypes;
import interfaces.SortableElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admin extends Staff {
    // Constructors
    public Admin() {
    }
    public Admin(AccountType accountType, Information information, String username) {
        super( accountType, information, username);
    }

    // Create a Request for adding a new actor
    public static void createActorRequest(Actor actor, Production production) {
        Request request = new Request();
        request.setType(RequestTypes.ACTOR_ISSUE);
        request.setCreatedDate(LocalDateTime.now());  // Set the current date and time
        request.setActorName(actor.getName());
        request.setDescription("New actor found in production: " + production.getName());
        request.setUsername("ADMIN");
        request.setTo("ADMIN");
        RequestsHolder.addRequest(request);

        IMDB imdb = IMDB.getInstance();
        imdb.requests.add(request);
    }

    public static class RequestsHolder {
        public static List<Request> requestsList = new ArrayList<>();

        // Method to add a request to the list
        public static void addRequest(Request request) {
            IMDB imdb = IMDB.getInstance();
            // Update and notify observers for
            for (User user : imdb.users) {
                if (user instanceof Admin) {
                    user.addNotification("New request " + request.getType() +
                            " added from user \"" + request.getUsername() + "\" for the admin team.");
                }
            }
            requestsList.add(request);
        }

        // Method to remove a request from the list
        public static void removeRequest(Request request) {
            requestsList.remove(request);
        }
    }
}
