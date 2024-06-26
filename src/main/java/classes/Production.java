package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.Genre;
import interfaces.Observer;
import interfaces.SortableElement;
import interfaces.Subject;

public abstract class Production implements Comparable<Object>, SortableElement, Subject {
    @JsonIgnore
    public String type;
    private String title;
    private List<String> directors;
    private List<String> actors;
    private List<Genre> genres;
    private List<Rating> ratings;
    private String plot;
    private Double averageRating;
    private Integer releaseYear;
    @JsonIgnore
    private final List<Observer> observers = new ArrayList<>();

    // Default Constructor
    public Production() {
        this.directors = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }

    // Parameterized Constructor
    public Production(String title, List<String> directors, List<String> actors, List<Genre> genres,
                      List<Rating> ratings, String plot, Double averageRating, Integer releaseYear) {
        this.title = title;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.ratings = ratings;
        this.plot = plot;
        this.averageRating = averageRating;
        this.releaseYear = releaseYear;
    }

    // Getters and Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Double getAverageRating() {
        return Objects.requireNonNullElse(this.averageRating, 0.0);
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReleaseYear() {
        return Objects.requireNonNullElse(this.releaseYear, 0);
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<Observer> getObservers() {
        return this.observers;
    }

    public String getName() {
        return title;
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String notification) {
        for (Observer observer : observers) {
            observer.update(notification);
        }
    }

    // Abstract method to be implemented by subclasses
    public abstract void displayInfo();

    // Additional methods

    public void addRating(Rating r){
        this.ratings.add(r);
        // Update the rating for the production
        this.averageRating = 0.0;
        for (Rating i : ratings) {
            this.averageRating += i.getRating();
        }
        this.averageRating /= ratings.size();
    }

    public void removeRating(Rating r){
        this.ratings.remove(r);
        // Update the rating for the production
        this.averageRating = 0.0;
        for (Rating i : ratings) {
            this.averageRating += i.getRating();
        }
        this.averageRating /= ratings.size();
    }

    @Override
    public int compareTo(Object o) {
        // Implementation for sorting
        return this.title.compareTo(((Production) o).title);
    }
}
