package classes;

import interfaces.Observer;
import interfaces.Subject;

import java.util.ArrayList;
import java.util.List;

public class Rating implements Subject {
    private String username;
    private int rating; // 1 to 10
    private String comment;
    private final List<Observer> observers = new ArrayList<>();

    // Default Constructor
    public Rating() {
    }

    // Parameterized Constructor
    public Rating(String username, int rating, String comment) {
        this.username = username;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public String toString() {
        return "Review:" +
                "\n     -username: " + username + " with " + IMDB.getUserExperience(this) + " experience" +
                "\n     -rating: " + rating +
                "\n     -comment: " + comment + "\n";
    }
}