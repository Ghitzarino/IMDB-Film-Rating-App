package classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.RequestTypes;
import interfaces.Observer;
import interfaces.Subject;

public class Request implements Subject {
    private RequestTypes type;
    private LocalDateTime createdDate;
    private String title;
    private String description;
    private String username;
    private String to;
    @JsonIgnore
    private final List<Observer> observers = new ArrayList<>();

    // Default Constructor
    public Request() {
    }

    // Parameterized Constructor
    public Request(RequestTypes type, LocalDateTime createdDate, String title,
                   String description, String username, String to) {
        this.type = type;
        this.createdDate = createdDate;
        this.title = title;
        this.description = description;
        this.username = username;
        this.to = to;
    }

    // Getters and Setters
    public RequestTypes getType() {
        return type;
    }

    public void setType(RequestTypes type) {
        this.type = type;
    }

    public String getCreatedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return createdDate.format(formatter);
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public void setCreatedDate(String createdDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        this.createdDate = LocalDateTime.parse(createdDate, formatter);
    }

    public String getTitle() {
        return title;
    }
    public void setActorName(String title) {
        this.title = title;
    }
    public void setMovieTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String ret = "";
        ret += "Request:" +
                "\n     -type: " + type +
                "\n     -created on: " + createdDate.format(formatter);

        if (this.type.equals(RequestTypes.ACTOR_ISSUE) || this.type.equals(RequestTypes.MOVIE_ISSUE)) {
            ret += "\n     -subject: " + title;
        }

        ret +=  "\n     -description: " + description +
                "\n     -username: " + username +
                "\n     -to: " + to + '\n';

        return ret;
    }
}