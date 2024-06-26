package classes;

import enums.AccountType;
import interfaces.StaffInterface;
import interfaces.SortableElement;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class Staff extends User implements StaffInterface {
    // List to store requests that only this user needs to resolve
    private List<Request> userRequests;

    // SortedSet to store productions and actors added by this user
    private SortedSet<SortableElement> addedElements;

    // Constructors
    public Staff() {

    }
    public Staff(AccountType accountType, Information information, String username) {
        super(accountType, information, username);
        this.userRequests = new ArrayList<>();
        this.addedElements = new TreeSet<>(new ElementNameComparator());
    }
    public Staff(AccountType accountType, Information information, String username, List<Request> userRequests,
                 SortedSet<SortableElement> addedElements) {
        super(accountType, information, username);
        this.userRequests = userRequests;
        this.addedElements = addedElements;
    }

    // Getter and setter
    public List<Request> getUserRequests() {
        return userRequests;
    }
    public void setUserRequests(List<Request> userRequests) {
        this.userRequests = userRequests;
    }
    public SortedSet<SortableElement> getAddedElements() {
        return addedElements;
    }
    public void setAddedElements(SortedSet<SortableElement> addedElements) {
        this.addedElements = addedElements;
    }

    public void addElement(SortableElement element){
        this.addedElements.add(element);
    }

    public void removeElement(SortableElement element){
        this.addedElements.remove(element);
    }

    public void addUserRequest(Request request) {
        this.userRequests.add(request);
    }

    public void removeUserRequest(Request request) {
        this.userRequests.remove(request);
    }


    // Implementation of the addProductionSystem method from StaffInterface interface
    @Override
    public void addProductionSystem(Production p) {
        IMDB imdb = IMDB.getInstance();
        for (Production production : imdb.productions) {
            if (production.getName().equals(p.getName())) {
                System.out.println("Production already exists.");
                return;
            }
        }
        // Add the creator as an observer
        p.addObserver(this);
        imdb.productions.add(p);
        // Update the addedElements collection
        addedElements.add(p);
        System.out.println("Production added successfully!");

        // Update the experience gained
        this.setExperienceStrategy(new AddProductionOrActorStrategy());
        this.setExperience(this.getExperience() + this.getExperienceStrategy().calculateExperience());
    }

    // Implementation of the addActorSystem method from StaffInterface interface
    @Override
    public void addActorSystem(Actor a) {
        IMDB imdb = IMDB.getInstance();
        for (Actor actor : imdb.actors) {
            if (actor.getName().equals(a.getName())){
                System.out.println("Actor already exists.");
                return;
            }
        }

        imdb.actors.add(a);
        // Update the addedElements collection
        addedElements.add(a);
        System.out.println("Actor added successfully!");

        // Update the experience gained
        this.setExperienceStrategy(new AddProductionOrActorStrategy());
        this.setExperience(this.getExperience() + this.getExperienceStrategy().calculateExperience());
    }

    // Implementation of the removeProductionSystem method from StaffInterface interface
    @Override
    public void removeProductionSystem(String name) {
        IMDB imdb = IMDB.getInstance();
        for (Production p : imdb.productions) {
            if (p.getName().equals(name)) {
                // Remove from all users' favorites lists
                for (User user : imdb.users) {
                    user.getFavorites().removeIf(element -> element.getName().equals(name));
                }

                imdb.productions.remove(p);
                // Update the addedElements collection
                addedElements.removeIf(element -> element.getName().equals(name));
                System.out.println("Production removed successfully!");

                // Update the experience gained
                this.setExperienceStrategy(new AddProductionOrActorStrategy());
                this.setExperience(this.getExperience() - this.getExperienceStrategy().calculateExperience());
                return;
            }
        }
        System.out.println("No production to remove.");
    }

    // Implementation of the removeActorSystem method from StaffInterface interface
    @Override
    public void removeActorSystem(String name) {
        IMDB imdb = IMDB.getInstance();
        for (Actor a : imdb.actors) {
            if (a.getName().equals(name)) {
                // Remove from all users' favorites lists
                for (User user : imdb.users) {
                    user.getFavorites().removeIf(element -> element.getName().equals(name));
                }

                imdb.actors.remove(a);
                // Update the addedElements collection
                addedElements.removeIf(element -> element.getName().equals(name));
                System.out.println("Actor removed successfully!");

                // Update the experience gained
                this.setExperienceStrategy(new AddProductionOrActorStrategy());
                this.setExperience(this.getExperience() - this.getExperienceStrategy().calculateExperience());
                return;
            }
        }
        System.out.println("No actor to remove.");
    }

    // Implementation of the updateProduction method from StaffInterface interface
    @Override
    public void updateProduction(Production p) {
        IMDB imdb = IMDB.getInstance();
        // Update productions list
        // Update from all users' favorites lists
        for (User user : imdb.users) {
            if (user.getFavorites().contains(p)){
                user.getFavorites().removeIf(element -> element.getName().equals(p.getName()));
                user.getFavorites().add(p);
            }
        }
        imdb.productions.removeIf(production -> production.getName().equals(p.getName()));
        imdb.productions.add(p);
        // Update the addedElements collection
        addedElements.removeIf(element -> element.getName().equals(p.getName()));
        addedElements.add(p);
        System.out.println("Production updated successfully!");
    }

    // Implementation of the updateActor method from StaffInterface interface
    @Override
    public void updateActor(Actor a) {
        IMDB imdb = IMDB.getInstance();
        // Update actors list
        // Update from all users' favorites lists
        for (User user : imdb.users) {
            if (user.getFavorites().contains(a)){
                user.getFavorites().removeIf(element -> element.getName().equals(a.getName()));
                user.getFavorites().add(a);
            }
        }
        imdb.actors.removeIf(actor -> actor.getName().equals(a.getName()));
        imdb.actors.add(a);
        // Update the addedElements collection
        addedElements.removeIf(element -> element.getName().equals(a.getName()));
        addedElements.add(a);
        System.out.println("Actor updated successfully!");
    }
}
