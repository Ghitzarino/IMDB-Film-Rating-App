package classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.AccountType;
import enums.RequestTypes;
import interfaces.ExperienceStrategy;
import interfaces.Observer;
import interfaces.SortableElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Random;
import java.time.LocalDateTime;

// Concrete strategy implementation for adding a review
class AddReviewStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logic to calculate experience when adding a review
        return 2;
    }
}

// Concrete strategy implementation for resolving a request
class ResolveRequestStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logic to calculate experience when resolving a request
        return 5;
    }
}

// Concrete strategy implementation for adding a production or actor
class AddProductionOrActorStrategy implements ExperienceStrategy {
    @Override
    public int calculateExperience() {
        // Logic to calculate experience when adding a production or actor
        return 3;
    }
}

// User class implementing the strategy pattern
public abstract class User implements Observer {
    private Information information;
    private AccountType userType;
    private String username;
    private int experience;
    private List<String> notifications;
    private SortedSet<SortableElement> favorites;
    @JsonIgnore
    private ExperienceStrategy experienceStrategy;

    // Constructors
    public User() {
    }

    public User(AccountType accountType, Information information, String username)
            throws Information.InformationIncompleteException {
        if (information == null || information.getName() == null || information.getCredentials() == null) {
            throw new Information.InformationIncompleteException("Name and credentials cannot be null");
        }

        this.information = information;
        this.username = username;
        this.experience = 0;
        this.notifications = new ArrayList<>();
        this.favorites = new TreeSet<>(new ElementNameComparator());
        this.userType = accountType;
        this.experienceStrategy = new AddReviewStrategy(); // Default strategy
    }

    // Getters and setters

    public Information getInformation() {
        return information;
    }

    public void setInformation(Information information) {
        this.information = information;
    }

    public AccountType getUserType() {
        return userType;
    }

    public void setUserType(AccountType userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public SortedSet<SortableElement> getFavorites() {
        return favorites;
    }

    public void setFavorites(SortedSet<SortableElement> favorites) {
        this.favorites = favorites;
    }

    public ExperienceStrategy getExperienceStrategy() {
        return experienceStrategy;
    }

    public void setExperienceStrategy(ExperienceStrategy experienceStrategy) {
        this.experienceStrategy = experienceStrategy;
    }

    public List<String> getFavoriteActors() {
        List<String> favoriteActors = new ArrayList<>();

        for (SortableElement elem : this.favorites) {
            if (elem instanceof Actor) {
                favoriteActors.add(elem.getName());
            }
        }

        return favoriteActors;
    }

    public List<String> getFavoriteProductions() {
        List<String> favoriteProductions = new ArrayList<>();

        for (SortableElement elem : this.favorites) {
            if (elem instanceof Production) {
                favoriteProductions.add(elem.getName());
            }
        }

        return favoriteProductions;
    }

    public List<String> getActorsContribution() {
        List<String> actorsContribution = new ArrayList<>();

        if (this instanceof Staff) {
            for (SortableElement elem : ((Staff) this).getAddedElements()) {
                if (elem instanceof Actor) {
                    actorsContribution.add(elem.getName());
                }
            }
        }

        return actorsContribution;
    }

    public List<String> getProductionsContribution() {
        List<String> productionsContribution = new ArrayList<>();

        if (this instanceof Staff) {
            for (SortableElement elem : ((Staff) this).getAddedElements()) {
                if (elem instanceof Production) {
                    productionsContribution.add(elem.getName());
                }
            }
        }

        return productionsContribution;
    }

    public void update (String notification) {
        this.notifications.add(notification);
    }

    // Method to generate a unique username based on the name
    public static String generateUsername(String name) {
        Random random = new Random();

        // Generate a random number between 0 and 999
        int randomNumber = random.nextInt(1000);
        return name.toLowerCase().replace(" ", "_") + "_" + randomNumber;
    }

    // Method to add a notification
    public void addNotification(String notification) {
        notifications.add(notification);
    }

    // Method to remove a notification
    public void removeNotification(String notification) {
        notifications.remove(notification);
    }

    // Method to add a favorite production or actor
    public void addFavorite(SortableElement favorite) {
        favorites.add(favorite);
    }

    // Method to remove a favorite production or actor
    public void removeFavorite(SortableElement favorite) {
        favorites.remove(favorite);
    }

    public static class Information {
        private final Credentials credentials;
        private final String name;
        private final String country;
        private final int age;
        private final char gender;
        private final LocalDateTime birthDate;

        public String toString() {
            return "Information{" +
                    "email='" + credentials.getEmail() + '\'' +
                    "password='" + credentials.getPassword() + '\'' +
                    ", name='" + name + '\'' +
                    ", country='" + country + '\'' +
                    ", age=" + age +
                    ", gender=" + gender +
                    ", birthDate=" + birthDate +
                    '}';
        }

        // Private constructor to be used by the builder
        private Information(Builder builder) {
            this.credentials = builder.credentials;
            this.name = builder.name;
            this.country = builder.country;
            this.age = builder.age;
            this.gender = builder.gender;
            this.birthDate = builder.birthDate;
        }

        // Getter methods

        public Credentials getCredentials() {
            return credentials;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public int getAge() {
            return age;
        }

        public char getGender() {
            return gender;
        }

        public String getBirthDate() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return birthDate.format(formatter);
        }

        // Builder class for Information
        public static class Builder {
            private final Credentials credentials;
            private final String name;
            private String country;
            private int age;
            private char gender;
            private LocalDateTime birthDate;

            // Constructor with required parameters
            public Builder(Credentials credentials, String name) {
                if (credentials == null || name == null || name.isEmpty()) {
                    throw new InformationIncompleteException("Credentials and name are required.");
                }
                this.credentials = credentials;
                this.name = name;
            }

            // Optional methods for additional parameters
            public Builder country(String country) {
                this.country = country;
                return this;
            }

            public Builder age(int age) {
                this.age = age;
                return this;
            }

            public Builder gender(char gender) {
                this.gender = gender;
                return this;
            }

            public Builder birthDate(LocalDateTime birthDate) {
                this.birthDate = birthDate;
                return this;
            }

            public Builder birthDate(String birthDate) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                this.birthDate = LocalDate.parse(birthDate, formatter).atStartOfDay();
                return this;
            }

            // Build method to create the Information instance
            public Information build() {
                return new Information(this);
            }
        }

        // Exception class for incomplete Information
        public static class InformationIncompleteException extends RuntimeException {
            public InformationIncompleteException(String message) {
                super(message);
            }
        }
    }

    public static class ElementNameComparator implements Comparator<SortableElement> {
        @Override
        public int compare(SortableElement e1, SortableElement e2) {
            return e1.getName().compareToIgnoreCase(e2.getName());
        }
    }
}

