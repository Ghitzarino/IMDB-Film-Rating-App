package classes;

import java.text.DecimalFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.Genre;

public class Movie extends Production {
    private String duration;

    // Default Constructor
    public Movie() {
        super();
    }

    // Parameterized Constructor
    public Movie(String title, List<String> directors, List<String> actors, List<Genre> genres,
                 List<Rating> ratings, String plot, Double rating, String duration, Integer releaseYear) {
        super(title, directors, actors, genres, ratings, plot, rating, releaseYear);
        this.duration = duration;
    }

    // Getters and Setters
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    // Overridden displayInfo method
    @Override
    public void displayInfo() {
        // Implement displayInfo specific to Movie
        if (this.getName() != null) {
            System.out.println("Movie Title: " + this.getName());
        }
        if (this.getDirectors() != null) {
            System.out.println("Directors: " + this.getDirectors());
        }
        if (this.getActors() != null) {
            System.out.println("Actors: " + this.getActors());
        }
        if (this.getGenres() != null) {
            System.out.println("Genres: " + this.getGenres());
        }
        if (this.getPlot() != null) {
            System.out.println("Plot: " + this.getPlot());
        }
        if (this.getDuration() != null) {
            System.out.println("Duration: " + this.getDuration());
        }
        if (this.getReleaseYear() != null) {
            System.out.println("Release Year: " + this.getReleaseYear());
        }
        if (this.getRatings() != null) {
            System.out.println("Ratings: " + this.getRatings());
        }
        if (this.getAverageRating() != null) {
            System.out.printf("Average Rating: %.2f%n", this.getAverageRating());
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Movie: ").append(this.getName()).append("\n");

        if (!this.getDirectors().isEmpty()) {
            result.append("     -directors: ").append(this.getDirectors()).append("\n");
        } else {
            result.append("     -directors: N/A\n");
        }

        if (!this.getActors().isEmpty()) {
            result.append("     -actors: ").append(this.getActors()).append("\n");
        } else {
            result.append("     -actors: N/A\n");
        }

        if (!this.getGenres().isEmpty()) {
            result.append("     -genres: ").append(this.getGenres()).append("\n");
        } else {
            result.append("     -genres: N/A\n");
        }

        int numReviews = this.getRatings() != null ? this.getRatings().size() : 0;
        result.append("     -reviews: ").append(numReviews).append("\n");

        // Format average rating to one decimal place
        if (this.getAverageRating() != 0.0) {
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            result.append("     -average rating: ").append(decimalFormat.format(this.getAverageRating())).append("\n");
        } else {
            result.append("     -average rating: N/A\n");
        }

        if (this.getDuration() != null) {
            result.append("     -duration: ").append(this.getDuration()).append("\n");
        } else {
            result.append("     -duration: N/A\n");
        }

        if (this.getReleaseYear() != 0) {
            result.append("     -release year: ").append(this.getReleaseYear()).append("\n");
        } else {
            result.append("     -release year: N/A\n");
        }

        if (this.getPlot() != null) {
            result.append("     -plot: ").append(this.getPlot()).append("\n");
        } else {
            result.append("     -plot: N/A\n");
        }

        return result.toString();
    }
}
