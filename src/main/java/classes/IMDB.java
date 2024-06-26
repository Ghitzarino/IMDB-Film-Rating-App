package classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import enums.*;
import extra_classes.MovieDTO;
import extra_classes.ProductionDTO;
import extra_classes.SeriesDTO;
import extra_classes.UserDTO;
import interfaces.Observer;
import interfaces.SortableElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;

public class IMDB {
    private static IMDB instance;
    public List<User> users;
    public List<Actor> actors;
    public List<Production> productions;
    public List<Request> requests;

    // Private constructor to prevent instantiation
    private IMDB() {
    }

    // Method to get the instance of IMDB
    public static IMDB getInstance() {
        if (instance == null) {
            instance = new IMDB();
        }
        return instance;
    }

    // Invalid user input exception
    private static class InvalidCommandException extends RuntimeException {
        public InvalidCommandException(String message) {
            super(message);
        }
    }

    // Save users changes to json
    public static void saveUsersToJson(List<User> users, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO(
                    user.getUsername(),
                    user.getExperience(),
                    user.getInformation(),
                    user.getUserType(),
                    user.getProductionsContribution(),
                    user.getActorsContribution(),
                    user.getFavoriteProductions(),
                    user.getFavoriteActors(),
                    user.getNotifications()
            );
            userDTOs.add(userDTO);
        }

        try {
            objectMapper.writeValue(new File(filePath), userDTOs);
            System.out.println("Users saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    // Save actors changes to json
    public static void saveActorsToJson(List<Actor> actors, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(new File(filePath), actors);
            System.out.println("Actors saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save requests changes to json
    public static void saveRequestsToJson(List<Request> requests, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(new File(filePath), requests);
            System.out.println("Requests saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save productions changes to json
    public static void saveProductionToJson(List<Production> productions, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<ProductionDTO> productionDTOs = new ArrayList<>();
        for (Production production : productions) {
            ProductionDTO productionDTO = convertToProductionDTO(production);
            productionDTOs.add(productionDTO);
        }

        try {
            objectMapper.writeValue(new File(filePath), productionDTOs);
            System.out.println("Productions saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ProductionDTO convertToProductionDTO(Production production) {
        ProductionDTO newProductionDTO = null;
        if (production instanceof Movie) {
            String type = "Movie";
            newProductionDTO = new MovieDTO(
                    production.getTitle(),
                    type,
                    production.getDirectors(),
                    production.getActors(),
                    production.getGenres(),
                    production.getRatings(),
                    production.getPlot(),
                    production.getAverageRating(),
                    production.getReleaseYear(),
                    ((Movie) production).getDuration()
            );
        } else if (production instanceof Series) {
            String type = "Series";
            newProductionDTO = new SeriesDTO(
                    production.getTitle(),
                    type,
                    production.getDirectors(),
                    production.getActors(),
                    production.getGenres(),
                    production.getRatings(),
                    production.getPlot(),
                    production.getAverageRating(),
                    production.getReleaseYear(),
                    ((Series) production).getNumSeasons(),
                    ((Series) production).getSeasons()
            );
        }
        return newProductionDTO;
    }

    // Parse accounts.json method
    private static List<User> parseAccountsJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = new ArrayList<>();
        IMDB imdb = IMDB.getInstance();

        try {
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // Iterate over JSON array elements
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                JsonNode userNode = elements.next();

                // Extract fields from JSON
                String username = userNode.get("username").asText();
                // Build the Information field
                User.Information.Builder informationBuilder = new User.Information.Builder(
                        objectMapper.treeToValue(userNode.get("information").get("credentials"), Credentials.class),
                        userNode.get("information").get("name").asText())
                        .country(userNode.get("information").get("country").asText())
                        .age(userNode.get("information").get("age").asInt())
                        .gender(userNode.get("information").get("gender").asText().charAt(0))
                        .birthDate(userNode.get("information").get("birthDate").asText());

                User.Information information = informationBuilder.build();

                AccountType accountType = AccountType.valueOf(userNode.get("userType").asText().toUpperCase());

                int experience = userNode.get("experience").asInt();

                // Use the factory to create the appropriate User instance
                User user = UserFactory.createUser(accountType, information, username);
                user.setExperience(experience);

                // Add the notifications to the notifications list
                List<String> notificationsList = objectMapper.treeToValue(userNode.get("notifications"), List.class);
                if (notificationsList != null) {
                    for (String notification : notificationsList) {
                        user.addNotification(notification);
                    }
                }

                // Add the favorite actors to the favorites SortedSet
                List<String> favoriteActorsList = objectMapper.treeToValue(userNode.get("favoriteActors"), List.class);
                if (favoriteActorsList != null) {
                    for (String favoriteActor : favoriteActorsList) {
                        Actor actor = getActor(imdb.actors, favoriteActor);
                        user.addFavorite(actor);
                    }
                }

                // Add the favorite productions to the favorites SortedSet
                List<String> favoriteProductionsList =
                        objectMapper.treeToValue(userNode.get("favoriteProductions"), List.class);
                if (favoriteProductionsList != null) {
                    for (String favoriteProduction : favoriteProductionsList) {
                        Production production = getProduction(imdb.productions, favoriteProduction);
                        user.addFavorite(production);
                    }
                }

                // Parse addedElements if the User belongs to Staff members
                if (user instanceof Staff staffUser){

                    List<String> actorsContributionList =
                            objectMapper.treeToValue(userNode.get("actorsContribution"), List.class);
                    if (actorsContributionList != null) {
                        for (String actorContribution : actorsContributionList) {
                            Actor actor = getActor(imdb.actors, actorContribution);
                            staffUser.addElement(actor);
                        }
                    }

                    List<String> productionsContributionList =
                            objectMapper.treeToValue(userNode.get("productionsContribution"), List.class);
                    if (productionsContributionList != null) {
                        for (String productionContribution : productionsContributionList) {
                            Production production = getProduction(imdb.productions, productionContribution);
                            staffUser.addElement(production);
                        }
                    }
                }

                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }

        return users;
    }

    // Parse production.json method
    private static List<Production> parseProductionJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Production> productions = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // Iterate over JSON array elements
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                JsonNode productionNode = elements.next();

                // Check the "type" field to determine the production type
                String type = productionNode.get("type").asText();
                if ("Movie".equals(type)) {
                    Movie movie = objectMapper.treeToValue(productionNode, Movie.class);
                    productions.add(movie);
                } else if ("Series".equals(type)) {
                    Series series = objectMapper.treeToValue(productionNode, Series.class);
                    productions.add(series);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return productions;
    }

    // Parse actors.json and requests.json method
    private static <T> List<T> parseNormalJsonFile(String filePath, Class<T[]> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            T[] objects = objectMapper.readValue(new File(filePath), clazz);
            return new ArrayList<>(Arrays.asList(objects));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to add the parsed staff users as correct observers for notifications
    private static void addCorrectProductionObservers() {
        IMDB imdb = IMDB.getInstance();
        for (User user : imdb.users) {
            if (user instanceof Staff) {
                for (SortableElement elem : ((Staff) user).getAddedElements()) {
                    if (elem instanceof Production) {
                        ((Production) elem).addObserver(user);
                        int index = imdb.productions.indexOf(((Production) elem));
                        if (index != -1) {
                            imdb.productions.get(index).addObserver(user);
                        }
                    }
                }
            }
        }
    }

    // Check if an actor with a given name is in the list of actors
    private static boolean containsActor(List<Actor> actors, String actorName) {
        for (Actor actor : actors) {
            if (actor.getName().equals(actorName)) {
                return true;
            }
        }
        return false;
    }

    // Get a certain actor by its name
    private static Actor getActor(List<Actor> actors, String actorName) {
        for (Actor actor : actors) {
            if (actor.getName().equals(actorName)) {
                return actor;
            }
        }
        return null;
    }

    // Get a certain production by its name
    private static Production getProduction(List<Production> productions, String productionName) {
        for (Production production : productions) {
            if (production.getName().equals(productionName)) {
                return production;
            }
        }
        return null;
    }

    // Get only movies out of production
    private static List<Movie> splitMoviesFromProductions() {
        IMDB imdb = IMDB.getInstance();
        List<Movie> moviesList = new ArrayList<>();

        for (Production movie : imdb.productions) {
            if (movie instanceof Movie movieReal){
                moviesList.add(movieReal);
            }
        }

        return moviesList;
    }

    // Get only series out of production
    private static List<Series> splitSeriesFromProductions() {
        IMDB imdb = IMDB.getInstance();
        List<Series> seriesList = new ArrayList<>();

        for (Production series : imdb.productions) {
            if (series instanceof Series seriesReal){
                seriesList.add(seriesReal);
            }
        }

        return seriesList;
    }

    // Get experience based on username for sorting reviews
    public static int getUserExperience(Rating r) {
        IMDB imdb = IMDB.getInstance();
        for (User user : imdb.users) {
            if (user.getUsername().equals(r.getUsername())) {
                return user.getExperience();
            }
        }
        return 0; // default value
    }

    // View production menu
    private static void viewProductionDetailsMenu(Scanner scanner) {
        IMDB imdb = IMDB.getInstance();

        System.out.println("\nChoose an option:");
        System.out.println("1) View all productions");
        System.out.println("2) View by genre");
        System.out.println("3) View by minimum number of reviews");
        System.out.println("4) View by minimum average rating");
        System.out.println("5) Sort by average rating (best to worst)");
        System.out.println("6) Sort by average rating (worst to best)");
        System.out.println("7) View by minimum release year");
        System.out.println("8) Sort by release year (newest to oldest)");
        System.out.println("9) Sort by release year (oldest to newest)");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                displayProductions(imdb.productions);
                break;
            case 2:
                System.out.print("Enter genre to filter by: ");
                String genre = scanner.nextLine();
                displayProductions(filterProductionsByGenre(imdb.productions, genre));
                break;
            case 3:
                System.out.print("Enter minimum number of reviews: ");
                int minReviews;
                try {
                    minReviews = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid number.");
                }
                displayProductions(filterProductionsByReviews(imdb.productions, minReviews));
                break;
            case 4:
                System.out.print("Enter minimum average rating (1 to 10): ");
                double minRating;
                try {
                    minRating = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid number.");
                }
                if (minRating < 1 || minRating > 10) {
                    System.out.println("Please enter a number between 1 and 10!");
                } else {
                    displayProductions(filterProductionsByAverageRating(imdb.productions, minRating));
                }
                break;
            case 5:
                displayProductions(sortProductionsByAverageRating(imdb.productions));
                break;
            case 6:
                displayProductions(sortProductionsByAverageRating(imdb.productions).reversed());
                break;
            case 7:
                System.out.print("Enter minimum year: ");
                int minYear;
                try {
                    minYear = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid number.");
                }
                displayProductions(filterProductionsByReleaseYear(imdb.productions, minYear));
                break;
            case 8:
                displayProductions(sortProductionsByReleaseYear(imdb.productions));
                break;
            case 9:
                displayProductions(sortProductionsByReleaseYear(imdb.productions).reversed());
                break;
            case 0:
                // Back to main menu
                break;
            default:
                throw new InvalidCommandException("Invalid command. Please enter a valid option.");
        }
    }

    // Print productions
    private static void displayProductions(List<Production> productions) {
        for (Production production : productions) {
            System.out.println(production);
        }
    }

    // Filter productions by genre
    private static List<Production> filterProductionsByGenre(List<Production> productions, String genre) {
        String lowercaseGenre = genre.toLowerCase();

        return productions.stream()
                .filter(production -> production.getGenres()
                        .stream()
                        .anyMatch(g -> g.toString().toLowerCase().equals(lowercaseGenre)))
                .collect(Collectors.toList());
    }

    // Filter productions by reviews
    private static List<Production> filterProductionsByReviews(List<Production> productions, int minReviews) {
        return productions.stream()
                .filter(production -> production.getRatings().size() >= minReviews)
                .collect(Collectors.toList());
    }

    // Filter production by average rating
    private static List<Production> filterProductionsByAverageRating(List<Production> productions, double minRating) {
        return productions.stream()
                .filter(production -> production.getAverageRating() >= minRating)
                .collect(Collectors.toList());
    }

    // Sort production by average rating
    private static List<Production> sortProductionsByAverageRating(List<Production> productions) {
        return productions.stream()
                .sorted(Comparator.comparingDouble(Production::getAverageRating).reversed())
                .collect(Collectors.toList());
    }

    // Filter production by release year
    public static List<Production> filterProductionsByReleaseYear(List<Production> productions, int minReleaseYear) {
        return productions.stream()
                .filter(production -> production.getReleaseYear() >= minReleaseYear)
                .collect(Collectors.toList());
    }

    // Sort production by release year
    public static List<Production> sortProductionsByReleaseYear(List<Production> productions) {
        return productions.stream()
                .sorted(Comparator.comparingInt(Production::getReleaseYear).reversed())
                .collect(Collectors.toList());
    }

    // View actors sorted or unsorted
    private static void viewActorDetails(Scanner scanner) throws InvalidCommandException {
        IMDB imdb = IMDB.getInstance();
        List<Actor> actorsCopy = new ArrayList<>(imdb.actors);
        System.out.println("\nViewing actor details...");

        // Option to sort actors by name
        System.out.println("Do you want to sort actors by name? (yes/no)");
        String sortChoice = scanner.nextLine().toLowerCase();

        if (!sortChoice.equals("yes") && !sortChoice.equals("no")) {
            throw new InvalidCommandException("Invalid choice for sorting. Please enter 'yes' or 'no'.");
        }

        if (sortChoice.equals("yes")) {
            // Sort the actors by name
            actorsCopy.sort(Comparator.comparing(Actor::getName));
        }

        // Display the sorted (or unsorted) actors
        System.out.println("List of actors:");
        for (Actor actor : actorsCopy) {
            System.out.println(actor);
        }
    }

    // View notifications
    private static void viewNotifications(User user) {
        System.out.println();
        for (String notification : user.getNotifications()){
            System.out.println(notification);
        }
    }

    // View search by actor/movie/series menu
    private static void searchMenu(List<Actor> actors, List<Production> productions, Scanner scanner) {
        System.out.println("\nSearch Menu:");
        System.out.println("1) Search for Actor");
        System.out.println("2) Search for Movie");
        System.out.println("3) Search for Series");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                System.out.print("Enter actor name to search: ");
                String actorQuery = scanner.nextLine();
                searchActorsByName(actors, actorQuery);
                break;
            case 2:
                System.out.print("Enter movie title to search: ");
                String movieQuery = scanner.nextLine();
                searchMoviesByTitle(productions, movieQuery);
                break;
            case 3:
                System.out.print("Enter series title to search: ");
                String seriesQuery = scanner.nextLine();
                searchSeriesByTitle(productions, seriesQuery);
                break;
            case 0:
                return;  // Back to main menu
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Search name in actors
    private static void searchActorsByName(List<Actor> actors, String query) {
        List<Actor> matchingActors = searchByName(actors, query);

        if (matchingActors.isEmpty()) {
            System.out.println("No matching actors found.");
        } else {
            System.out.println("Matching actors:");
            for (Actor matchingActor : matchingActors) {
                System.out.println(matchingActor);
            }
        }
    }

    // Search title in movies
    private static void searchMoviesByTitle(List<Production> movies, String query) {
        List<Production> matchingMovies = searchByName(movies, query);

        if (matchingMovies.isEmpty()) {
            System.out.println("No matching movies found.");
        } else {
            System.out.println("Matching movies:");
            for (Production matchingMovie : matchingMovies) {
                if (matchingMovie instanceof Movie matchingMovieReal){
                    System.out.println(matchingMovieReal);
                }
            }
        }
    }

    // Search title in series
    private static void searchSeriesByTitle(List<Production> series, String query) {
        List<Production> matchingSeries = searchByName(series, query);

        if (matchingSeries.isEmpty()) {
            System.out.println("No matching series found.");
        } else {
            System.out.println("Matching series:");
            for (Production matchingSerie : matchingSeries) {
                if (matchingSerie instanceof Series matchingSeriesReal){
                    System.out.println(matchingSeriesReal);
                }
            }
        }
    }

    // General search method
    private static <T extends SortableElement> List<T> searchByName(List<T> productions, String query) {
        List<T> matchingProductions = new ArrayList<>();

        for (T production : productions) {
            if (production.getName().toLowerCase().contains(query.toLowerCase())) {
                matchingProductions.add(production);
            }
        }

        return matchingProductions;
    }

    // Manage favorites menu
    public static void manageFavorites(User currentUser, List<Actor> actors,
                                       List<Movie> movies, List<Series> series, Scanner scanner) {
        System.out.println("\nManage Favorites Menu:");
        System.out.println("1) Add to Favorites");
        System.out.println("2) Remove from Favorites");
        System.out.println("3) View your Favorites");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                addToFavoritesMenu(currentUser, actors, movies, series, scanner);
                break;
            case 2:
                displayFavoritesWithIndexes(currentUser.getFavorites().stream().toList());
                System.out.print("Enter the index of the favorite entry to remove (0 to cancel): ");

                int favoriteIndex;
                try {
                    favoriteIndex = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                }

                // Check if the user chose to cancel
                if (favoriteIndex == 0) {
                    System.out.println("Favorite entry removal canceled.");
                    return;
                }

                removeFavoritesEntry(currentUser, currentUser.getFavorites().stream().toList(), favoriteIndex);
                break;
            case 3:
                System.out.println("Favorites:");
                for (SortableElement favorite : currentUser.getFavorites()) {
                    System.out.println(favorite);
                }
                break;
            case 0:
                return;  // Back to main menu
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Add favorite actor/movie/series menu
    public static void addToFavoritesMenu(User currentUser, List<Actor> actors, List<Movie> movies,
                                          List<Series> series, Scanner scanner) {
        System.out.println("\nChoose category to add to favorites:");
        System.out.println("1) Add Actor to Favorites");
        System.out.println("2) Add Movie to Favorites");
        System.out.println("3) Add Series to Favorites\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                displayActorsWithIndexes(actors);
                System.out.print("Enter the index of the actor to add to favorites: ");

                int actorIndex;
                try {
                    actorIndex = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                }

                addActorToFavorites(currentUser, actors, actorIndex);
                break;
            case 2:
                displayMoviesWithIndexes(movies);
                System.out.print("Enter the index of the movie to add to favorites: ");

                int movieIndex;
                try {
                    movieIndex = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                }

                addProductionToFavorites(currentUser, movies, movieIndex);
                break;
            case 3:
                displaySeriesWithIndexes(series);
                System.out.print("Enter the index of the series to add to favorites: ");

                int seriesIndex;
                try {
                    seriesIndex = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                }

                addProductionToFavorites(currentUser, series, seriesIndex);
                break;
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Better display for Actors
    public static void displayActorsWithIndexes(List<Actor> actors) {
        System.out.println("Actors:");
        for (int i = 0; i < actors.size(); i++) {
            System.out.println((i + 1) + ") " + actors.get(i).getName());
        }
    }

    // Better display for Movies
    public static void displayMoviesWithIndexes(List<Movie> movies) {
        System.out.println("Movies:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ") " + movies.get(i).getName());
        }
    }

    // Better display for Series
    public static void displaySeriesWithIndexes(List<Series> series) {
        System.out.println("Series:");
        for (int i = 0; i < series.size(); i++) {
            System.out.println((i + 1) + ") " + series.get(i).getName());
        }
    }

    // Better display for Ratings
    public static void displayRatingsWithIndexes(List<Rating> ratings) {
        System.out.println("Reviews:");
        for (int i = 0; i < ratings.size(); i++) {
            System.out.println((i + 1) + ") " + ratings.get(i));
        }
    }

    // Better display for Favorites
    public static void displayFavoritesWithIndexes(List<SortableElement> favorites) {
        System.out.println("Favorites:");
        for (int i = 0; i < favorites.size(); i++) {
            switch (favorites.get(i).getClass().getName()) {
                case "classes.Actor" -> System.out.println((i + 1) + ") Actor - " + favorites.get(i).getName());
                case "classes.Movie" -> System.out.println((i + 1) + ") Movie - " + favorites.get(i).getName());
                case "classes.Series" -> System.out.println((i + 1) + ") Series - " + favorites.get(i).getName());
            }
        }
    }

    // Better display for user Entries
    public static void displayAddedElementsWithIndexes(List<SortableElement> entries) {
        System.out.println("Your added Entries:");
        for (int i = 0; i < entries.size(); i++) {
            switch (entries.get(i).getClass().getName()) {
                case "classes.Actor" -> System.out.println((i + 1) + ") Actor - " + entries.get(i).getName());
                case "classes.Movie" -> System.out.println((i + 1) + ") Movie - " + entries.get(i).getName());
                case "classes.Series" -> System.out.println((i + 1) + ") Series - " + entries.get(i).getName());
            }
        }
    }

    // Better display for Productions
    public static void displayProductionsWithIndexes(List<Production> productions) {
        System.out.println("Productions:");
        for (int i = 0; i < productions.size(); i++) {
            switch (productions.get(i).getClass().getName()) {
                case "classes.Movie" -> System.out.println((i + 1) + ") Movie - " + productions.get(i).getName());
                case "classes.Series" -> System.out.println((i + 1) + ") Series - " + productions.get(i).getName());
            }
        }
    }

    // Remove favorite entry method
    public static void removeFavoritesEntry(User currentUser, List<SortableElement> favorites, int index) {
        if (index >= 1 && index <= favorites.size()) {
            SortableElement selectedEntry = favorites.get(index - 1);
            currentUser.removeFavorite(selectedEntry);
            System.out.println(selectedEntry.getName() + " removed from favorites!");
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Add actor to favorite method
    public static void addActorToFavorites(User currentUser, List<Actor> actors, int index) {
        if (index >= 1 && index <= actors.size()) {
            Actor selectedActor = actors.get(index - 1);
            currentUser.addFavorite(selectedActor);
            System.out.println(selectedActor.getName() + " added to favorites!");
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Add production to favorite method
    public static <T extends Production> void addProductionToFavorites(User currentUser,
                                                                       List<T> productions, int index) {
        if (index >= 1 && index <= productions.size()) {
            T selectedProduction = productions.get(index - 1);
            currentUser.addFavorite(selectedProduction);
            System.out.println(selectedProduction.getName() + " added to favorites!");
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Menu for operations with reviews
    public static void manageReviewsMenu(User currentUser, List<Production> productions, Scanner scanner) {
        System.out.println("\nManage Reviews Menu:");
        System.out.println("1) Add Review");
        System.out.println("2) Delete Review");
        System.out.println("3) View Reviews");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                displayProductionsWithIndexes(productions);
                System.out.print("Enter the index of the production to review: ");

                int indexAdd;
                try {
                    indexAdd = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                }

                addReview(currentUser, productions, indexAdd, scanner);
                break;
            case 2:
                List<Rating> userReviews = ((Regular) currentUser).getUserReviews(productions);
                if (!userReviews.isEmpty()) {
                    displayRatingsWithIndexes(userReviews);
                    System.out.print("Enter the index of the production to delete review from (0 to cancel): ");

                    int indexRemove;
                    try {
                        indexRemove = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                    }

                    // Check if the user chose to cancel
                    if (indexRemove == 0) {
                        System.out.println("Review removal canceled.");
                        return;
                    }

                    deleteReview(currentUser, productions, userReviews, indexRemove);
                } else {
                    System.out.println("No reviews found.");
                }
                break;
            case 3:
                displayProductionsWithIndexes(productions);
                System.out.print("Enter the index of the production to view the reviews: ");

                int indexView;
                try {
                    indexView = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                }

                viewReviews(productions, indexView);
                break;
            case 0:
                return;  // Back to main menu
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Review adding method
    public static void addReview(User currentUser, List<Production> productions, int index, Scanner scanner) {
        if (index >= 1 && index <= productions.size()) {
            Production selectedProduction = productions.get(index - 1);

            for (Rating r : selectedProduction.getRatings()) {
                if (r.getUsername().equals(currentUser.getUsername())) {
                    System.out.println("Review already added for this production!");
                    return;
                }
            }

            System.out.print("Enter your review for " + selectedProduction.getName() + ": ");

            System.out.print("Enter your rating (1 to 10): ");
            int grade;
            try {
                grade = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid input. Please enter a valid option.");
            }
            if (grade < 1 || grade > 10) {
                System.out.println("Invalid grade. Please enter a valid grade.");
                return;
            }
            System.out.print("Enter your review: ");
            String review = scanner.nextLine();

            Rating rating = new Rating(currentUser.getUsername(), grade, review);

            rating.addObserver(currentUser);
            // Notify the observers
            for (Rating r : selectedProduction.getRatings()) {
                r.notifyObservers("Production \"" + selectedProduction.getName() +
                        "\" you have reviewed has received a new review from \"" + currentUser.getUsername() +
                        "\" -> " + grade);
            }
            selectedProduction.notifyObservers("Production \"" + selectedProduction.getName() +
                    "\" you have created has received a new review from \"" + currentUser.getUsername() +
                    "\" -> " + grade);

            selectedProduction.addRating(rating);
            // Update experience
            currentUser.setExperienceStrategy(new AddReviewStrategy());
            currentUser.setExperience(currentUser.getExperience() +
                    currentUser.getExperienceStrategy().calculateExperience());
            System.out.println("Review added successfully!");
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Review deleting method
    public static void deleteReview(User currentUser, List<Production> productions,
                                    List<Rating> userRatings, int index) {
        if (index >= 1 && index <= userRatings.size()) {
            Rating selectedRating = userRatings.get(index - 1);

            for (Production p : productions) {
                if (p.getRatings().contains(selectedRating)) {
                    p.removeRating(selectedRating);
                    currentUser.setExperienceStrategy(new AddReviewStrategy());
                    currentUser.setExperience(currentUser.getExperience() -
                            currentUser.getExperienceStrategy().calculateExperience());
                    System.out.println("Review deleted successfully!");
                }
            }
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // View reviews sorted by experience
    public static void viewReviews(List<Production> productions, int index) {
        if (index >= 1 && index <= productions.size()) {
            Production selectedProduction = productions.get(index - 1);
            System.out.println("Reviews (sorted by user experience):");

            // Sort the ratings by user experience
            selectedProduction.getRatings().sort(Comparator.comparingInt(
                    rating -> getUserExperience((Rating) rating)).reversed());


            for (Rating rating : selectedProduction.getRatings()) {
                System.out.println(rating);
            }
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Menu to choose add or remove for request
    public static void addRemoveRequestMenu(User currentUser, List<Request> requests, Scanner scanner) {
        System.out.println("\nChoose action:");
        System.out.println("1) Add request");
        System.out.println("2) Remove request");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                addRequestMenu(currentUser, requests, scanner);
                break;
            case 2:
                removeRequestMenu(currentUser, requests, scanner);
                break;
            case 0:
                return; // Back to main menu
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Menu for adding a request
    public static void addRequestMenu(User currentUser, List<Request> requests, Scanner scanner) {
        System.out.println("Choose request type:");
        System.out.println("1) Delete account");
        System.out.println("2) Actor issue");
        System.out.println("3) Movie/Series issue");
        System.out.println("4) Others\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        RequestTypes requestType = null;
        String title = null;
        switch (choice) {
            case 1:
                requestType = RequestTypes.DELETE_ACCOUNT;
                break;
            case 2:
                requestType = RequestTypes.ACTOR_ISSUE;
                System.out.print("Enter actor name: ");
                title = scanner.nextLine();
                break;
            case 3:
                requestType = RequestTypes.MOVIE_ISSUE;
                System.out.print("Enter movie/series name: ");
                title = scanner.nextLine();
                break;
            case 4:
                requestType = RequestTypes.OTHERS;
                break;
            default:
                System.out.println("Invalid request type. Please enter a valid option.");
                return;
        }

        System.out.print("Enter request description: ");
        String description = scanner.nextLine();

        Request newRequest = new Request(requestType, LocalDateTime.now(), title,
                description, currentUser.getUsername(), "ADMIN");

        newRequest.addObserver(currentUser);

        if (currentUser instanceof Regular) {
            ((Regular) currentUser).createRequest(newRequest);
        }
        if (currentUser instanceof Contributor) {
            ((Contributor) currentUser).createRequest(newRequest);
            for (SortableElement elem : ((Contributor) currentUser).getAddedElements()) {
                if (elem.getName().equals(title)) {
                    System.out.println("Can't add request for your own contribution");
                    return;
                }
            }
        }
        System.out.println("Request added successfully!");
    }

    // Menu for removing a request
    public static void removeRequestMenu(User currentUser, List<Request> requests, Scanner scanner) {
        // Filter requests created by the currentUser
        List<Request> userRequests = requests.stream()
                .filter(request -> request.getUsername().equals(currentUser.getUsername())).toList();

        if (userRequests.isEmpty()) {
            System.out.println("You have no requests to remove.");
            return;
        }

        // Print user's requests
        System.out.println("Your requests:");
        for (int i = 0; i < userRequests.size(); i++) {
            System.out.println((i + 1) + ") " + userRequests.get(i));
        }

        // Ask the user to choose the index of the request to remove
        System.out.print("Enter the index of the request to remove (0 to cancel): ");
        int indexToRemove;
        try {
            indexToRemove = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        // Check if the user chose to cancel
        if (indexToRemove == 0) {
            System.out.println("Request removal canceled.");
            return;
        }

        if (indexToRemove >= 1 && indexToRemove <= userRequests.size()) {
            // Remove the chosen request
            Request removedRequest = userRequests.get(indexToRemove - 1);
            System.out.println("Request removed successfully!");

            // Remove the request from the main list
            requests.remove(removedRequest);

            if (currentUser instanceof Regular) {
                ((Regular) currentUser).removeRequest(removedRequest);
            }
            if (currentUser instanceof Contributor) {
                ((Contributor) currentUser).removeRequest(removedRequest);
            }
        } else {
            System.out.println("Invalid index. No request removed.");
        }
    }

    // Menu for managing entries
    public static void manageSystemEntries(User currentUser, List<Actor> actors,
                                    List<Production> productions, Scanner scanner) {
        System.out.println("Manage System Entries Menu:");
        System.out.println("1) Add to System");
        System.out.println("2) Remove from System");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                addToSystemMenu(currentUser, actors, productions, scanner);
                break;
            case 2:
                removeFromSystem(currentUser, scanner);
                break;
            case 0:
                return;  // Back to main menu
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Menu to add entry to system
    public static void addToSystemMenu(User currentUser, List<Actor> actors,
                                       List<Production> productions, Scanner scanner) {
        System.out.println("Choose category to add to System:");
        System.out.println("1) Add Actor to System");
        System.out.println("2) Add Movie to System");
        System.out.println("3) Add Series to System\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                System.out.print("Enter the name of the actor: ");
                String actorName = scanner.nextLine();

                Actor actor = new Actor();
                actor.setName(actorName);

                ((Staff) currentUser).addActorSystem(actor);
                break;
            case 2:
                System.out.print("Enter the name of the movie: ");
                String movieName = scanner.nextLine();

                Movie movie = new Movie();
                movie.setTitle(movieName);

                ((Staff) currentUser).addProductionSystem(movie);
                break;
            case 3:
                System.out.print("Enter the name of the series: ");
                String seriesName = scanner.nextLine();

                Series series = new Series();
                series.setTitle(seriesName);

                ((Staff) currentUser).addProductionSystem(series);
                break;
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Remove entries from the system
    public static void removeFromSystem(User currentUser, Scanner scanner) {
        // No entries by this user
        if (((Staff) currentUser).getAddedElements().isEmpty()) {
            System.out.println("No added entries.");
            return;
        }
        List<SortableElement> addedElementsList = new ArrayList<>(((Staff) currentUser).getAddedElements());

        displayAddedElementsWithIndexes(addedElementsList);

        System.out.print("Enter the index of the entry to remove (0 to cancel): ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        // Check if the user chose to cancel
        if (index == 0) {
            System.out.println("Entry removal canceled.");
            return;
        }

        if (index >= 1 && index <= ((Staff) currentUser).getAddedElements().size()) {
            if (addedElementsList.get(index - 1) instanceof  Actor) {
                ((Staff) currentUser).removeActorSystem(addedElementsList.get(index - 1).getName());
            } else if (addedElementsList.get(index - 1) instanceof  Production) {
                ((Staff) currentUser).removeProductionSystem(addedElementsList.get(index - 1).getName());
            } else {
                System.out.println("Your entry is unidentified! Error!");
            }
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Menu to update entry details
    public static void updateDetailsMenu(User currentUser, Scanner scanner) {
        System.out.println("\nChoose category to update:");
        System.out.println("1) Actor details");
        System.out.println("2) Movie details");
        System.out.println("3) Series details");
        System.out.println("0) Back to main menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                updateActorDetails(currentUser, scanner);
                break;
            case 2:
                updateMovieDetails(currentUser, scanner);
                break;
            case 3:
                updateSeriesDetails(currentUser, scanner);
                break;
            case 0:
                return; // Back to main menu
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // Update actor method
    private static void updateActorDetails(User currentUser, Scanner scanner) {
        List<SortableElement> addedElementsList = new ArrayList<>(((Staff) currentUser).getAddedElements());
        List<Actor> addedActorsList = new ArrayList<>();

        // Select only the added actors by the user
        for (SortableElement element : addedElementsList) {
            if (element instanceof Actor) {
                addedActorsList.add((Actor) element);
            }
        }

        if (addedActorsList.isEmpty()) {
            System.out.println("No actors to edit.");
            return;
        }

        displayActorsWithIndexes(addedActorsList);

        System.out.print("Enter the index of the actor to edit: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        if (index >= 1 && index <= addedActorsList.size()) {
            editActorDetailsMenu(currentUser ,addedActorsList.get(index - 1), scanner);
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Edit actor menu
    public static void editActorDetailsMenu(User currentUser, Actor actor, Scanner scanner) {
        boolean editing = true;
        Actor newActor = new Actor(actor.getName(), actor.getPerformances(), actor.getBiography());

        while (editing) {
            System.out.println("\nEdit Actor Details:");
            System.out.println("1) Edit Biography");
            System.out.println("2) Edit Performances");
            System.out.println("3) Save Changes");
            System.out.println("0) Cancel\n");

            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid input. Please enter a valid option.");
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter new biography: ");
                    String newBiography = scanner.nextLine();
                    newActor.setBiography(newBiography);
                    System.out.println("Biography updated.");
                    break;
                case 2:
                    Map<String, String> newPerformance = new HashMap<>();

                    System.out.print("Enter new performance name: ");
                    String newPerfName = scanner.nextLine();
                    newPerformance.put("title", newPerfName);

                    System.out.print("Enter new performance type: ");
                    String newPerfType = scanner.nextLine();
                    newPerformance.put("type", newPerfType);

                    newActor.getPerformances().add(newPerformance);
                    System.out.println("Performances updated.");
                    break;
                case 3:
                    // Save Changes and exit the loop
                    editing = false;
                    ((Staff) currentUser).updateActor(newActor);
                    System.out.println("Changes saved.");
                    break;
                case 0:
                    // Cancel and exit the loop
                    editing = false;
                    System.out.println("Changes canceled.");
                    break;
                default:
                    editing = false;
                    throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
            }
        }
    }

    // Update movie method
    private static void updateMovieDetails(User currentUser, Scanner scanner) {
        List<SortableElement> addedElementsList = new ArrayList<>(((Staff) currentUser).getAddedElements());
        List<Movie> addedMoviesList = new ArrayList<>();

        // Select only the added actors by the user
        for (SortableElement element : addedElementsList) {
            if (element instanceof Movie) {
                addedMoviesList.add((Movie) element);
            }
        }

        if (addedMoviesList.isEmpty()) {
            System.out.println("No movies to edit.");
            return;
        }

        displayMoviesWithIndexes(addedMoviesList);

        System.out.print("Enter the index of the movie to edit: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        if (index >= 1 && index <= addedMoviesList.size()) {
            editMovieDetailsMenu(currentUser ,addedMoviesList.get(index - 1), scanner);
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Edit movie menu
    public static void editMovieDetailsMenu(User currentUser, Movie movie, Scanner scanner) {
        boolean editing = true;
        Movie newMovie = new Movie(movie.getName(), movie.getDirectors(), movie.getActors(), movie.getGenres(),
                movie.getRatings(), movie.getPlot(), movie.getAverageRating(),
                movie.getDuration(), movie.getReleaseYear());

        while (editing) {
            System.out.println("\nEdit Movie Details:");
            System.out.println("1) Edit Duration");
            System.out.println("2) Edit Release Year");
            System.out.println("3) Edit Directors");
            System.out.println("4) Edit Actors");
            System.out.println("5) Edit Genres");
            System.out.println("6) Edit Plot");
            System.out.println("7) Save Changes");
            System.out.println("0) Cancel\n");

            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid input. Please enter a valid option.");
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter new duration: ");
                    String newDuration = scanner.nextLine();
                    newMovie.setDuration(newDuration);
                    System.out.println("Duration updated.");
                    break;
                case 2:
                    System.out.print("Enter new release year: ");
                    int newReleaseYear;
                    try {
                        newReleaseYear = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                    }
                    newMovie.setReleaseYear(newReleaseYear);
                    System.out.println("Release year updated.");
                    break;
                case 3:
                    System.out.print("Enter new directors (comma-separated): ");
                    String newDirectors = scanner.nextLine();
                    List<String> newDirectorList = Arrays.asList(newDirectors.split(","));
                    newMovie.setDirectors(newDirectorList);
                    System.out.println("Directors updated.");
                    break;
                case 4:
                    System.out.print("Enter new actors (comma-separated): ");
                    String newActors = scanner.nextLine();
                    List<String> newActorList = Arrays.asList(newActors.split(","));
                    newMovie.setActors(newActorList);
                    System.out.println("Actors updated.");
                    break;
                case 5:
                    System.out.print("Enter new genres (comma-separated): ");
                    String newGenres = scanner.nextLine();
                    List<Genre> newGenreList = Arrays.stream(newGenres.split(","))
                            .map(String::trim) // Trim whitespace from each genre
                            .filter(genre -> {
                                try {
                                    // Attempt to convert the input to a Genre enum
                                    Genre.valueOf(genre);
                                    return true;
                                } catch (IllegalArgumentException e) {
                                    // Invalid genre
                                    System.out.println("Invalid genre: " + genre);
                                    return false;
                                }
                            })
                            .map(Genre::valueOf)
                            .collect(Collectors.toList());

                    // Check if there's at least one valid genre
                    if (!newGenreList.isEmpty()) {
                        newMovie.setGenres(newGenreList);
                        System.out.println("Genres updated.");
                    } else {
                        System.out.println("No valid genres entered. Genres remain unchanged.");
                    }
                    break;
                case 6:
                    System.out.print("Enter new plot: ");
                    String newPlot = scanner.nextLine();
                    newMovie.setPlot(newPlot);
                    System.out.println("Plot updated.");
                    break;
                case 7:
                    // Save Changes and exit the loop
                    editing = false;
                    ((Staff) currentUser).updateProduction(newMovie);
                    System.out.println("Changes saved.");
                    break;
                case 0:
                    // Cancel and exit the loop
                    editing = false;
                    System.out.println("Changes canceled.");
                    break;
                default:
                    editing = false;
                    throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
            }
        }
    }

    // Update series method
    private static void updateSeriesDetails(User currentUser, Scanner scanner) {
        List<SortableElement> addedElementsList = new ArrayList<>(((Staff) currentUser).getAddedElements());
        List<Series> addedSeriesList = new ArrayList<>();

        // Select only the added actors by the user
        for (SortableElement element : addedElementsList) {
            if (element instanceof Series) {
                addedSeriesList.add((Series) element);
            }
        }

        if (addedSeriesList.isEmpty()) {
            System.out.println("No series to edit.");
            return;
        }

        displaySeriesWithIndexes(addedSeriesList);

        System.out.print("Enter the index of the series to edit: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        if (index >= 1 && index <= addedSeriesList.size()) {
            editSeriesDetailsMenu(currentUser ,addedSeriesList.get(index - 1), scanner);
        } else {
            System.out.println("Invalid index. Please enter a valid index.");
        }
    }

    // Edit series menu
    public static void editSeriesDetailsMenu(User currentUser, Series series, Scanner scanner) {
        boolean editing = true;
        Series newSeries = new Series(series.getName(), series.getDirectors(), series.getActors(), series.getGenres(),
                series.getRatings(), series.getPlot(), series.getAverageRating(), series.getReleaseYear(),
                series.getNumSeasons(), series.getSeasons());

        while (editing) {
            System.out.println("\nEdit Series Details:");
            System.out.println("1) Edit Number of Seasons");
            System.out.println("2) Edit Release Year");
            System.out.println("3) Edit Directors");
            System.out.println("4) Edit Actors");
            System.out.println("5) Edit Genres");
            System.out.println("6) Edit Plot");
            System.out.println("7) Save Changes");
            System.out.println("0) Cancel\n");

            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid input. Please enter a valid option.");
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter new number of seasons: ");
                    int newNumSeasons;
                    try {
                        newNumSeasons = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                    }
                    newSeries.setNumSeasons(newNumSeasons);
                    System.out.println("Number of seasons updated.");
                    break;
                case 2:
                    System.out.print("Enter new release year: ");
                    int newReleaseYear;
                    try {
                        newReleaseYear = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        throw new InvalidCommandException("Invalid input. Please enter a valid option.");
                    }
                    newSeries.setReleaseYear(newReleaseYear);
                    System.out.println("Release year updated.");
                    break;
                case 3:
                    System.out.print("Enter new directors (comma-separated): ");
                    String newDirectors = scanner.nextLine();
                    List<String> newDirectorList = Arrays.asList(newDirectors.split(","));
                    newSeries.setDirectors(newDirectorList);
                    System.out.println("Directors updated.");
                    break;
                case 4:
                    System.out.print("Enter new actors (comma-separated): ");
                    String newActors = scanner.nextLine();
                    List<String> newActorList = Arrays.asList(newActors.split(","));
                    newSeries.setActors(newActorList);
                    System.out.println("Actors updated.");
                    break;
                case 5:
                    System.out.print("Enter new genres (comma-separated): ");
                    String newGenres = scanner.nextLine();
                    List<Genre> newGenreList = Arrays.stream(newGenres.split(","))
                            .map(String::trim) // Trim whitespace from each genre
                            .filter(genre -> {
                                try {
                                    // Attempt to convert the input to a Genre enum
                                    Genre.valueOf(genre);
                                    return true;
                                } catch (IllegalArgumentException e) {
                                    // Invalid genre
                                    System.out.println("Invalid genre: " + genre);
                                    return false;
                                }
                            })
                            .map(Genre::valueOf)
                            .collect(Collectors.toList());

                    // Check if there's at least one valid genre
                    if (!newGenreList.isEmpty()) {
                        newSeries.setGenres(newGenreList);
                        System.out.println("Genres updated.");
                    } else {
                        System.out.println("No valid genres entered. Genres remain unchanged.");
                    }
                    break;
                case 6:
                    System.out.print("Enter new plot: ");
                    String newPlot = scanner.nextLine();
                    newSeries.setPlot(newPlot);
                    System.out.println("Plot updated.");
                    break;
                case 7:
                    // Save Changes and exit the loop
                    editing = false;
                    ((Staff) currentUser).updateProduction(newSeries);
                    System.out.println("Changes saved.");
                    break;
                case 0:
                    // Cancel and exit the loop
                    editing = false;
                    System.out.println("Changes canceled.");
                    break;
                default:
                    editing = false;
                    throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
            }
        }
    }

    // Add or remove user menu
    public static void addRemoveUserMenu(User currentUser,List<User> users, Scanner scanner) {
        System.out.println("\nUser Management Menu:");
        System.out.println("1) Add User");
        System.out.println("2) Remove User");
        System.out.println("0) Back to Main Menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                addUser(users, scanner);
                break;
            case 2:
                removeUser(currentUser, users, scanner);
                break;
            case 0:
                // Back to main menu
                break;
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
            }
    }

    // Add user method
    public static void addUser(List<User> users, Scanner scanner) {
        System.out.println("Please enter all needed user information:");

        // Get user type by input
        System.out.print("User type (Regular/Contributor/Admin): ");
        String userTypeInput = scanner.nextLine();

        // Validate user type
        AccountType userType;
        try {
            userType = AccountType.valueOf(userTypeInput.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Invalid user type. Please enter a valid user type.");
        }

        // Ask for user information
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Country: ");
        String country = scanner.nextLine();

        System.out.print("Age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid age. Please enter a valid age.");
        }

        System.out.print("Gender (M/F/N): ");
        String g = scanner.nextLine().toUpperCase();

        // Validate gender
        if (!g.equals("M") && !g.equals("F") && !g.equals("N")) {
            throw new InvalidCommandException("Invalid gender. Please enter 'M', 'F', or 'N'.");
        }
        char gender = g.charAt(0);

        System.out.print("Birth date (YYYY-MM-DD): ");
        String birthDate = scanner.nextLine();

        Credentials credentials = new Credentials(email, password);
        // Create a new User based on the provided information
        User newUser;
        try {
            // Build the Information field
            User.Information.Builder informationBuilder = new User.Information.Builder(credentials, name)
                    .country(country)
                    .age(age)
                    .gender(gender)
                    .birthDate(birthDate);

            User.Information information = informationBuilder.build();

            newUser = UserFactory.createUser(userType, information, User.generateUsername(name));
        } catch (User.Information.InformationIncompleteException e) {
            throw new InvalidCommandException("Incomplete user information. Please provide all required fields.");
        }

        // Add the new user to the list
        users.add(newUser);
        System.out.println("\nUser \"" + newUser.getUsername() + "\" added successfully.");
    }

    // Remove user method
    public static void removeUser(User currentUser, List<User> users, Scanner scanner) {
        IMDB imdb = IMDB.getInstance();
        // Filter users based on type (Regular or Contributor)
        List<User> filteredUsers = users.stream().filter(user -> user.getUserType() == AccountType.REGULAR ||
                        user.getUserType() == AccountType.CONTRIBUTOR).toList();

        // Check if there are users to remove
        if (filteredUsers.isEmpty()) {
            System.out.println("No users available for removal.");
            return;
        }

        // Display the list of users with indexes
        System.out.println("Users available for removal:");
        for (int i = 0; i < filteredUsers.size(); i++) {
            User user = filteredUsers.get(i);
            System.out.println((i + 1) + ") " + user.getUsername() + " - " + user.getUserType());
        }

        // Prompt the user to choose an index to remove
        System.out.print("Enter the index of the user to remove (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid index.");
        }

        // Check if the user chose to cancel
        if (choice == 0) {
            System.out.println("User removal canceled.");
            return;
        }

        // Validate the index
        if (choice < 1 || choice > filteredUsers.size()) {
            throw new InvalidCommandException("Invalid index. Please enter a valid index.");
        }

        // Remove the selected user from the main list
        User userToRemove = filteredUsers.get(choice - 1);

        // Delete the user's ratings
        if (userToRemove instanceof Regular) {
            for (Production production : imdb.productions) {
                for (Rating rating : production.getRatings()) {
                    if (rating.getUsername().equals(userToRemove.getUsername())) {
                        production.removeRating(rating);
                    }
                }
            }
        } else if (userToRemove instanceof Contributor) {
            // Delete the observer instance and replace it with the new user
            for (Production production : imdb.productions) {
                for (Observer observer : production.getObservers()) {
                    if (observer.equals(userToRemove)) {
                        production.removeObserver(userToRemove);
                        production.addObserver(currentUser);
                    }
                }
            }
            // Remove the users requests and update the old requests
            imdb.requests.removeIf(request -> request.getUsername().equals(userToRemove.getUsername()));
            for (Request request : imdb.requests) {
                if (request.getTo().equals(userToRemove.getUsername())) {
                    request.setTo("ADMIN");
                }
            }

            // Update the to-do list of requests for the user
            for (Request request : ((Contributor) userToRemove).getUserRequests()) {
                request.setTo("ADMIN");
                Admin.RequestsHolder.addRequest(request);
            }

            // Update the ownership of actors/productions
            for (SortableElement element : ((Contributor) userToRemove).getAddedElements()) {
                ((Admin) currentUser).addElement(element);
            }
        }

        users.remove(userToRemove);

        System.out.println("User removed successfully: " + userToRemove.getUsername());
    }

    // Menu for solving a request
    public static void solveRequestMenu(User currentUser, Scanner scanner) {
        System.out.println("\nSolve Request Menu:");
        System.out.println("1) View Requests");
        System.out.println("2) Mark Request as Solved");
        System.out.println("3) Mark Request as Rejected");
        System.out.println("0) Back to Main Menu\n");

        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid option.");
        }

        switch (choice) {
            case 1:
                viewRequests(((Staff) currentUser));
                break;
            case 2:
                markRequestStatus(((Staff) currentUser), true, scanner);
                break;
            case 3:
                markRequestStatus(((Staff) currentUser), false, scanner);
                break;
            case 0:
                // Back to Main Menu
                break;
            default:
                throw new InvalidCommandException("Invalid choice. Please enter a valid option.");
        }
    }

    // View requests with indexes
    private static void viewRequests(Staff currentUser) {
        System.out.println("\nRequests:");
        int i = 0, j = 0;
        if (currentUser instanceof Admin) {
            for (j = 0; j < Admin.RequestsHolder.requestsList.size(); j++) {
                Request request = Admin.RequestsHolder.requestsList.get(j);
                System.out.print((j + 1) + ") " + request);
            }
        }
        for (i = 0; i < currentUser.getUserRequests().size(); i++) {
            Request request = currentUser.getUserRequests().get(i);
            System.out.print((i + j + 1) + ") " + request);
        }
    }

    // Mark request as resolved/rejected
    private static void markRequestStatus(Staff currentUser, boolean status, Scanner scanner) {
        IMDB imdb = IMDB.getInstance();
        viewRequests(currentUser);

        // Prompt the user to choose a request to mark as solved/rejected
        System.out.print("Enter the index of the request to mark (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid index.");
        }

        // Check if the user chose to cancel
        if (choice == 0) {
            System.out.println("Request status change canceled.");
            return;
        }

        if (currentUser instanceof Contributor) {
            // Validate the index
            if (choice < 1 || choice > currentUser.getUserRequests().size()) {
                throw new InvalidCommandException("Invalid index. Please enter a valid index.");
            }
            resolveUserSpecificRequest(currentUser, choice, status);
        } else if (currentUser instanceof Admin) {
            if (choice < 1 || choice > currentUser.getUserRequests().size() + Admin.RequestsHolder.requestsList.size()) {
                throw new InvalidCommandException("Invalid index. Please enter a valid index.");
            }
            if (choice > Admin.RequestsHolder.requestsList.size()) {
                choice -= Admin.RequestsHolder.requestsList.size();
                resolveUserSpecificRequest(currentUser, choice, status);
            } else {
                resolveAdminSpecificRequest(choice, status);
            }
        }

        System.out.println("Request status updated successfully.");
    }

    // Method to solve a request for the admins
    public  static void resolveAdminSpecificRequest(int choice, boolean status) {
        IMDB imdb = IMDB.getInstance();
        Request selectedRequest = Admin.RequestsHolder.requestsList.get(choice - 1);

        if (!status) {
            selectedRequest.notifyObservers("Your request \"" + selectedRequest.getDescription() +
                    "\" has been rejected.");
            Admin.RequestsHolder.requestsList.remove(selectedRequest);
            imdb.requests.remove(selectedRequest);
        } else {
            selectedRequest.notifyObservers("Your request \"" + selectedRequest.getDescription() +
                    "\" has been solved. Thank you for helping!");
            Admin.RequestsHolder.requestsList.remove(selectedRequest);
            imdb.requests.remove(selectedRequest);

            for (User user : imdb.users) {
                if (selectedRequest.getUsername().equals(user.getUsername())) {
                    // Update the experience of the user
                    user.setExperienceStrategy(new ResolveRequestStrategy());
                    user.setExperience(user.getExperience() + user.getExperienceStrategy().calculateExperience());
                    break;
                }
            }
        }
    }

    // Method to resolve a request for specific user
    public static void resolveUserSpecificRequest(Staff currentUser, int choice, boolean status) {
        IMDB imdb = IMDB.getInstance();
        Request selectedRequest = currentUser.getUserRequests().get(choice - 1);

        if (!status) {
            selectedRequest.notifyObservers("Your request \"" + selectedRequest.getDescription() +
                    "\" has been rejected.");
            currentUser.removeUserRequest(selectedRequest);
            imdb.requests.remove(selectedRequest);
        } else {
            selectedRequest.notifyObservers("Your request \"" + selectedRequest.getDescription() +
                    "\" has been solved. Thank you for helping!");
            currentUser.removeUserRequest(selectedRequest);
            imdb.requests.remove(selectedRequest);

            for (User user : imdb.users) {
                if (selectedRequest.getUsername().equals(user.getUsername())) {
                    // Update the experience of the user
                    user.setExperienceStrategy(new ResolveRequestStrategy());
                    user.setExperience(user.getExperience() + user.getExperienceStrategy().calculateExperience());
                    break;
                }
            }
        }
    }

    // Add the actors from production to the requestHolder list for admins
    public static void addActorsToRequestHolder() {
        IMDB imdb = IMDB.getInstance();
        for (Production production : imdb.productions) {
            for (String actorName : production.getActors()) {
                // Check if actor is not in imdb.actors
                if (!containsActor(imdb.actors, actorName)) {
                    // Create an Actor element
                    Actor newActor = new Actor(actorName, null, null);

                    // Create a Request and add it to the admin list of requests
                    Admin.createActorRequest(newActor, production);
                }
            }
        }
    }

    // User login method
    private static User login(Scanner scanner) {
        while (true) {
            System.out.println("\n                  --Login--");
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            // Check for Quit option
            if (email.equals("quit")) {
                return null;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            // Check for Quit option
            if (password.equalsIgnoreCase("quit")) {
                return null;
            }

            User foundUser = authenticateUser(email, password);

            if (foundUser != null) {
                System.out.println("\nLogin successful!");
                return foundUser;
            } else {
                System.out.println("Incorrect email or password. Please try again. (Type \"quit\" to exit)");
            }
        }
    }

    // User authenticate verification method
    private static User authenticateUser(String email, String password) {
        IMDB imdb = IMDB.getInstance();
        for (User user : imdb.users) {
            if (user.getInformation().getCredentials().getEmail().equals(email)
                    && user.getInformation().getCredentials().getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // View menu for regular user
    private static boolean regularMenu(User regularUser, Scanner scanner) {
        IMDB imdb = IMDB.getInstance();
        List<Movie> movies = splitMoviesFromProductions();
        List<Series> series = splitSeriesFromProductions();

        while (true) {
            System.out.println("\nUsername: " + regularUser.getUsername());
            System.out.println("Experience: " + regularUser.getExperience());
            System.out.println("\nRegular User Menu:");
            System.out.println("1) View production details");
            System.out.println("2) View actors details");
            System.out.println("3) View notifications");
            System.out.println("4) Search for actor/movie/series");
            System.out.println("5) Add/Delete actor/movie/series to/from favorites");
            System.out.println("6) Add/Delete request");
            System.out.println("7) Add/Delete review for movie/series");
            System.out.println("8) Logout");
            System.out.println("Or type \"quit\" to exit the application.\n");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        viewProductionDetailsMenu(scanner);
                        break;
                    case "2":
                        viewActorDetails(scanner);
                        break;
                    case "3":
                        viewNotifications(regularUser);
                        break;
                    case "4":
                        searchMenu(imdb.actors, imdb.productions, scanner);
                        break;
                    case "5":
                        manageFavorites(regularUser, imdb.actors, movies, series, scanner);
                        break;
                    case "6":
                        addRemoveRequestMenu(regularUser, imdb.requests, scanner);
                        break;
                    case "7":
                        manageReviewsMenu(regularUser, imdb.productions, scanner);
                        break;
                    case "8":
                        System.out.println("\nLogging out...");
                        return false;
                    case "quit":
                        System.out.println("\nExiting application. Goodbye!");
                        return true;
                    default:
                        throw new InvalidCommandException("Invalid command. Please enter a number between 1 and 8.");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // View menu for contributor user
    private static boolean contributorMenu(User contributorUser, Scanner scanner) {
        IMDB imdb = IMDB.getInstance();
        List<Movie> movies = splitMoviesFromProductions();
        List<Series> series = splitSeriesFromProductions();

        while (true) {
            System.out.println("\nUsername: " + contributorUser.getUsername());
            System.out.println("Experience: " + contributorUser.getExperience());
            System.out.println("\nContributor User Menu:");
            System.out.println("1) View production details");
            System.out.println("2) View actors details");
            System.out.println("3) View notifications");
            System.out.println("4) Search for actor/movie/series");
            System.out.println("5) Add/Delete actor/movie/series to/from favorites");
            System.out.println("6) Add/Delete request");
            System.out.println("7) Add/Delete actor/movie/series to/from system");
            System.out.println("8) Update actor/movie/series details");
            System.out.println("9) Solve a request");
            System.out.println("10) Logout");
            System.out.println("Or type \"quit\" to exit the application.\n");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        viewProductionDetailsMenu(scanner);
                        break;
                    case "2":
                        viewActorDetails(scanner);
                        break;
                    case "3":
                        viewNotifications(contributorUser);
                        break;
                    case "4":
                        searchMenu(imdb.actors, imdb.productions, scanner);
                        break;
                    case "5":
                        manageFavorites(contributorUser, imdb.actors, movies, series, scanner);
                        break;
                    case "6":
                        addRemoveRequestMenu(contributorUser, imdb.requests, scanner);
                        break;
                    case "7":
                        manageSystemEntries(contributorUser, imdb.actors, imdb.productions, scanner);
                        break;
                    case "8":
                        updateDetailsMenu(contributorUser, scanner);
                        break;
                    case "9":
                        solveRequestMenu(contributorUser, scanner);
                        break;
                    case "10":
                        System.out.println("\nLogging out...");
                        return false;
                    case "quit":
                        System.out.println("\nExiting application. Goodbye!");
                        return true;
                    default:
                        throw new InvalidCommandException("Invalid command. Please enter a number between 1 and 10.");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // View menu for admin user
    private static boolean adminMenu(User adminUser, Scanner scanner) {
        IMDB imdb = IMDB.getInstance();
        List<Movie> movies = splitMoviesFromProductions();
        List<Series> series = splitSeriesFromProductions();

        while (true) {
            System.out.println("\nUsername: " + adminUser.getUsername());
            System.out.println("\nAdmin User Menu:");
            System.out.println("1) View production details");
            System.out.println("2) View actors details");
            System.out.println("3) View notifications");
            System.out.println("4) Search for actor/movie/series");
            System.out.println("5) Add/Delete actor/movie/series to/from favorites");
            System.out.println("6) Add/Delete actor/movie/series to/from system");
            System.out.println("7) Update actor/movie/series details");
            System.out.println("8) Solve a request");
            System.out.println("9) Add/Delete user");
            System.out.println("10) Logout");
            System.out.println("Or type \"quit\" to exit the application.\n");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        viewProductionDetailsMenu(scanner);
                        break;
                    case "2":
                        viewActorDetails(scanner);
                        break;
                    case "3":
                        viewNotifications(adminUser);
                        break;
                    case "4":
                        searchMenu(imdb.actors, imdb.productions, scanner);
                        break;
                    case "5":
                        manageFavorites(adminUser, imdb.actors, movies, series, scanner);
                        break;
                    case "6":
                        manageSystemEntries(adminUser, imdb.actors, imdb.productions, scanner);
                        break;
                    case "7":
                        updateDetailsMenu(adminUser, scanner);
                        break;
                    case "8":
                        solveRequestMenu(adminUser, scanner);
                        break;
                    case "9":
                        addRemoveUserMenu(adminUser, imdb.users, scanner);
                        break;
                    case "10":
                        System.out.println("\nLogging out...");
                        return false;
                    case "quit":
                        System.out.println("\nExiting application. Goodbye!");
                        return true;
                    default:
                        throw new InvalidCommandException("Invalid command. Please enter a number between 1 and 10.");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Choose between cli and gui
    private static void terminalOrGraphicMenu(Scanner scanner) {
        boolean done = false;
        while (!done) {
            System.out.println("                  ~~~IMDB App~~~");
            System.out.println("\nChoose an option:");
            System.out.println("1) Terminal app");
            System.out.println("2) Graphic interface app");
            System.out.println("0) Quit\n");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        terminalAppMenu(scanner);
                        done = true;
                        break;
                    case "2":
                        graphicAppMenu();
                        done = true;
                        break;
                    case "0":
                        return;
                    default:
                        throw new InvalidCommandException("Invalid choice. Please enter a valid option.\n");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Terminal app Menu
    private static void terminalAppMenu(Scanner scanner) {
        boolean exit = false;

        System.out.println("\n            ~~IMDB Terminal App~~");

        // Log in
        while (!exit){
            User currentUser = login(scanner);

            if (currentUser != null) {
                System.out.println("Welcome back " + currentUser.getInformation().getName() + "!");

                switch (currentUser) {
                    case Regular regular ->
                            exit = regularMenu(currentUser, scanner);
                    case Contributor contributor ->
                            exit = contributorMenu(currentUser, scanner);
                    case Admin admin ->
                            exit = adminMenu(currentUser, scanner);
                    default -> {
                        System.out.println("\nUser type unknown. Exiting...");
                        exit = true;
                    }
                }

            } else {
                System.out.println("\nLogin failed. Exiting...");
                exit = true;
            }
        }
    }

    // Graphic app Menu
    private static void graphicAppMenu() {
        IMDB imdb = IMDB.getInstance();
        // Create the login frame
        JFrame loginFrame = new JFrame("Login Page");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(new BorderLayout());

        // Create and add components to the login frame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        // Add components to the panel
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(loginButton);

        // Add an action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform login logic here
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                for (User user : imdb.users) {
                    if (user.getInformation().getCredentials().getEmail().equals(email) &&
                    user.getInformation().getCredentials().getPassword().equals(password)) {
                        // Successful login, you can open the main page or perform any other action
                        JOptionPane.showMessageDialog(null, "Login successful!");
                        loginFrame.dispose(); // Close the login frame
                        openMainPage(user);
                        return;
                    }
                }

                // Display an error message for an unsuccessful login
                JOptionPane.showMessageDialog(null,
                        "Invalid username or password. Please try again.");
            }
        });

        // Add the panel to the login frame
        loginFrame.add(panel, BorderLayout.CENTER);

        // Center the frame on the screen
        loginFrame.setLocationRelativeTo(null);

        // Make the frame visible
        loginFrame.setVisible(true);
    }

    // Main page GUI Menu
    private static void openMainPage(User currentUser) {
        JFrame mainFrame = new JFrame("Main Page");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        // Create and add components to the main frame
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 4));

        JLabel welcomeLabel = new JLabel("Welcome to the Main Page!");
        JButton profileButton = new JButton("View Profile");
        JButton productionsButton = new JButton("View Productions (sorted)");
        JButton actorsButton = new JButton("View Actors (sorted)");
        JButton favoritesButton = new JButton("View Favorites");

        // Recommendation Section
        JTextArea actorRecommendationTextArea = new JTextArea();
        actorRecommendationTextArea.setEditable(false);
        JScrollPane actorRecommendationScrollPane = new JScrollPane(actorRecommendationTextArea);

        JTextArea productionRecommendationTextArea = new JTextArea();
        productionRecommendationTextArea.setEditable(false);
        JScrollPane productionRecommendationScrollPane = new JScrollPane(productionRecommendationTextArea);

        // Populate recommendations
        populateRandomActors(actorRecommendationTextArea);
        populateRandomProductions(productionRecommendationTextArea);

        // Add components to the main panel
        mainPanel.add(welcomeLabel);
        mainPanel.add(actorRecommendationScrollPane);
        mainPanel.add(productionRecommendationScrollPane);
        mainPanel.add((new JLabel()));  // Empty label for spacing
        mainPanel.add(profileButton);
        mainPanel.add(productionsButton);
        mainPanel.add(actorsButton);
        mainPanel.add(favoritesButton);

        // Search Section
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(15);  // Set the desired width
        JComboBox<String> searchTypeComboBox = new JComboBox<>(new String[]{"Actor", "Movie", "Series"});
        JButton performSearchButton = new JButton("Perform Search");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(searchLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(searchField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(searchTypeComboBox, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(performSearchButton, gbc);

        // JTextArea to display search results
        JTextArea searchResultsTextArea = new JTextArea();
        searchResultsTextArea.setEditable(false);
        JScrollPane searchResultsScrollPane = new JScrollPane(searchResultsTextArea);

        // Add action listeners to the buttons
        profileButton.addActionListener(e -> viewProfile(currentUser));

        productionsButton.addActionListener(e -> viewSortedProductions(currentUser));

        actorsButton.addActionListener(e -> viewSortedActors(currentUser));

        favoritesButton.addActionListener(e -> viewFavorites(currentUser));

        performSearchButton.addActionListener(e ->
                performSearch(searchResultsTextArea, searchField.getText(),
                        Objects.requireNonNull(searchTypeComboBox.getSelectedItem()).toString()));

        // Add the search components and results area above the main panel
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(searchPanel, BorderLayout.NORTH);
        mainFrame.add(searchResultsScrollPane, BorderLayout.CENTER);
        mainFrame.add(mainPanel, BorderLayout.SOUTH);

        // Center the frame on the screen
        mainFrame.setLocationRelativeTo(null);

        // Make the frame visible
        mainFrame.setVisible(true);
    }

    // Generate 10 random actors
    private static void populateRandomActors(JTextArea recommendationTextArea) {
        IMDB imdb = IMDB.getInstance();
        List<Actor> sortedActors = new ArrayList<>(imdb.actors);
        sortedActors.sort(Comparator.comparing(Actor::getName));
        // Shuffle the actors list
        Collections.shuffle(sortedActors, new Random());

        // Display 5 random actors in the recommendation section
        recommendationTextArea.append("Top 10 Recommended Actors:\n");
        for (int i = 0; i < Math.min(10, sortedActors.size()); i++) {
            recommendationTextArea.append(sortedActors.get(i).getName() + "\n");
        }
    }

    // Generate 10 random productions
    private static void populateRandomProductions(JTextArea recommendationTextArea) {
        IMDB imdb = IMDB.getInstance();
        List<Production> randomProductions = new ArrayList<>(imdb.productions);

        // Shuffle the actors list
        Collections.shuffle(randomProductions, new Random());

        // Display 5 random actors in the recommendation section
        recommendationTextArea.append("Top 10 Recommended Productions:\n");
        for (int i = 0; i < Math.min(10, randomProductions.size()); i++) {
            recommendationTextArea.append(randomProductions.get(i).getName() + "\n");
        }
    }

    // Display sorted actors
    private static void viewSortedActors(User currentUser) {
        IMDB imdb = IMDB.getInstance();
        List<Actor> sortedActors = new ArrayList<>(imdb.actors);
        sortedActors.sort(Comparator.comparing(Actor::getName));

        JFrame actorsFrame = new JFrame("List of Actors");
        actorsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        actorsFrame.setSize(400, 300);
        actorsFrame.setLayout(new BorderLayout());

        // Create a JList to display the list of actors
        DefaultListModel<Actor> listModel = new DefaultListModel<>();
        for (Actor actor : sortedActors) {
            listModel.addElement(actor);
        }

        JList<Actor> actorsList = new JList<>(listModel);
        actorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane actorsScrollPane = new JScrollPane(actorsList);
        actorsFrame.add(actorsScrollPane, BorderLayout.CENTER);

        // Create a button to add the selected actor to favorites
        JButton addToFavoritesButton = new JButton("Add to Favorites");
        addToFavoritesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected actor from the list
                Actor selectedActor = actorsList.getSelectedValue();
                if (selectedActor != null) {
                    if (currentUser.getFavorites().contains(selectedActor)) {
                        JOptionPane.showMessageDialog(null,
                                selectedActor.getName() + " already added to Favorites!");
                    } else {
                        currentUser.getFavorites().add(selectedActor);
                        JOptionPane.showMessageDialog(null,
                                selectedActor.getName() + " added to Favorites!");
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "Please select an actor to add to Favorites.");
                }
            }
        });

        // Add the button to add to favorites at the bottom of the frame
        actorsFrame.add(addToFavoritesButton, BorderLayout.SOUTH);

        // Center the frame on the screen
        actorsFrame.setLocationRelativeTo(null);

        // Make the frame visible
        actorsFrame.setVisible(true);
    }

    // Display sorted productions
    private static void viewSortedProductions(User currentUser) {
        IMDB imdb = IMDB.getInstance();
        List<Production> sortedProductions = new ArrayList<>(imdb.productions);
        sortedProductions.sort(Comparator.comparing(Production::getTitle));

        JFrame productionsFrame = new JFrame("List of Productions");
        productionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productionsFrame.setSize(400, 300);
        productionsFrame.setLayout(new BorderLayout());

        // Create a JList to display the list of productions
        DefaultListModel<Production> listModel = new DefaultListModel<>();
        for (Production production : sortedProductions) {
            listModel.addElement(production);
        }

        JList<Production> productionsList = new JList<>(listModel);
        productionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane productionsScrollPane = new JScrollPane(productionsList);
        productionsFrame.add(productionsScrollPane, BorderLayout.CENTER);

        // Create a button to add the selected production to favorites
        JButton addToFavoritesButton = new JButton("Add to Favorites");
        addToFavoritesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected production from the list
                Production selectedProduction = productionsList.getSelectedValue();
                if (selectedProduction != null) {
                    if (currentUser.getFavorites().contains(selectedProduction)) {
                        JOptionPane.showMessageDialog(null,
                                selectedProduction.getTitle() + " already added to Favorites!");
                    } else {
                        currentUser.getFavorites().add(selectedProduction);
                        JOptionPane.showMessageDialog(null,
                                selectedProduction.getTitle() + " added to Favorites!");
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "Please select a production to add to Favorites.");
                }
            }
        });

        // Add the button to add to favorites at the bottom of the frame
        productionsFrame.add(addToFavoritesButton, BorderLayout.SOUTH);

        // Center the frame on the screen
        productionsFrame.setLocationRelativeTo(null);

        // Make the frame visible
        productionsFrame.setVisible(true);
    }

    // User profile page
    private static void viewProfile(User currentUser) {
        JFrame profileFrame = new JFrame("Your information");
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setSize(400, 300);
        profileFrame.setLayout(new BorderLayout());

        JTextArea profileTextArea = new JTextArea();
        profileTextArea.setEditable(false);

        profileTextArea.append("Username: " + currentUser.getUsername() + '\n' + "Experience: ");
        profileTextArea.append(String.valueOf(currentUser.getExperience()));
        profileTextArea.append("\n");
        profileTextArea.append("Name: " + currentUser.getInformation().getName() + '\n');
        profileTextArea.append("Country: " + currentUser.getInformation().getCountry() + '\n');
        profileTextArea.append("Birth date: " + currentUser.getInformation().getBirthDate() + '\n' + "Age: ");
        profileTextArea.append(String.valueOf(currentUser.getInformation().getAge()));
        profileTextArea.append("\nGender: ");
        profileTextArea.append(String.valueOf(currentUser.getInformation().getGender()));

        // Add the text area to the frame
        JScrollPane profileScrollPane = new JScrollPane(profileTextArea);
        profileFrame.add(profileScrollPane, BorderLayout.CENTER);

        // Center the frame on the screen
        profileFrame.setLocationRelativeTo(null);

        // Make the frame visible
        profileFrame.setVisible(true);
    }

    // Favorites page
    private static void viewFavorites(User currentUser) {
        JFrame favoritesFrame = new JFrame("Your favorites");
        favoritesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        favoritesFrame.setSize(400, 300);
        favoritesFrame.setLayout(new BorderLayout());

        JTextArea favoritesTextArea = new JTextArea();
        favoritesTextArea.setEditable(false);

        for (SortableElement elem : currentUser.getFavorites()) {
            favoritesTextArea.append(elem.getName() + '\n');
        }

        // Add the text area to the frame
        JScrollPane favoritesScrollPane = new JScrollPane(favoritesTextArea);
        favoritesFrame.add(favoritesScrollPane, BorderLayout.CENTER);

        // Center the frame on the screen
        favoritesFrame.setLocationRelativeTo(null);

        // Make the frame visible
        favoritesFrame.setVisible(true);
    }

    // Perform the search based on the search type (movies, series, actors)
    private static void performSearch(JTextArea searchResultsTextArea, String query, String searchType) {
        IMDB imdb = IMDB.getInstance();

        switch (searchType) {
            case "Movie":
                searchMoviesByTitle(searchResultsTextArea, splitMoviesFromProductions(), query);
                break;
            case "Series":
                searchSeriesByTitle(searchResultsTextArea, splitSeriesFromProductions(), query);
                break;
            case "Actor":
                searchActorsByName(searchResultsTextArea, imdb.actors, query);
                break;
            default:
                // Handle invalid search type
                searchResultsTextArea.append("Invalid search type.\n");
        }
    }

    // Search movie
    private static void searchMoviesByTitle(JTextArea searchResultsTextArea, List<Movie> movies, String query) {
        boolean ok = false;

        for (Movie movie : movies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                ok = true;
                break;
            }
        }

        if (!ok) {
           searchResultsTextArea.append("No matching movies found.\n\n");
           return;
        }

        // Display the results in the JTextArea
        searchResultsTextArea.append("Matching movies:\n");
        for (Movie movie : movies) {
            // Add your condition for matching based on the query
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResultsTextArea.append(movie.getTitle() + "\n");
            }
        }
        searchResultsTextArea.append("\n\n");
    }

    // Search series
    private static void searchSeriesByTitle(JTextArea searchResultsTextArea, List<Series> series, String query) {
        boolean ok = false;

        for (Series s : series) {
            if (s.getTitle().toLowerCase().contains(query.toLowerCase())) {
                ok = true;
                break;
            }
        }

        if (!ok) {
            searchResultsTextArea.append("No matching series found.\n\n");
            return;
        }

        // Display the results in the JTextArea
        searchResultsTextArea.append("Matching series:\n");
        for (Series s : series) {
            // Add your condition for matching based on the query
            if (s.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResultsTextArea.append(s.getTitle() + "\n");
            }
        }
        searchResultsTextArea.append("\n\n");
    }

    // Search actor
    private static void searchActorsByName(JTextArea searchResultsTextArea, List<Actor> actors, String query) {
        boolean ok = false;

        for (Actor actor : actors) {
            if (actor.getName().toLowerCase().contains(query.toLowerCase())) {
                ok = true;
                break;
            }
        }

        if (!ok) {
            searchResultsTextArea.append("No matching actor found.\n\n");
            return;
        }

        // Display the results in the JTextArea
        searchResultsTextArea.append("Matching actors:\n");
        for (Actor actor : actors) {
            // Add your condition for matching based on the query
            if (actor.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResultsTextArea.append(actor.getName() + "\n");
            }
        }
        searchResultsTextArea.append("\n\n");
    }

    // IMDB run method
    private static void run() {
        // Obtain the singleton instance
        IMDB imdb = IMDB.getInstance();

        // Parse all the information
        imdb.actors = parseNormalJsonFile("output/actors.json", Actor[].class);
        imdb.requests = parseNormalJsonFile("output/requests.json", Request[].class);
        imdb.productions = parseProductionJsonFile("output/production.json");
        imdb.users = parseAccountsJsonFile("output/accounts.json");

        // Add the requests for adding missing actors to system (optional)
        // addActorsToRequestHolder();

        // Add the production observers for notification purposes
        addCorrectProductionObservers();

        // Start of IMDB terminal application code
        Scanner scanner = new Scanner(System.in);

        terminalOrGraphicMenu(scanner);

        // Close the scanner
        scanner.close();

        System.out.println();

        // Save the changes
        saveUsersToJson(imdb.users, "output/accounts.json");
        saveActorsToJson(imdb.actors, "output/actors.json");
        saveRequestsToJson(imdb.requests, "output/requests.json");
        saveProductionToJson(imdb.productions, "output/production.json");
    }

    public static void main(String[] args) {
        run();
    }
}