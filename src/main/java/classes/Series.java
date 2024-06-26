package classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.Genre;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Series extends Production {
    private Integer numSeasons;
    private Map<String, List<Episode>> seasons;

    // Default Constructor
    public Series() {
        super();
        this.seasons = new HashMap<>();
    }

    // Parameterized Constructor
    public Series(String title, List<String> directors, List<String> actors, List<Genre> genres,
                  List<Rating> ratings, String plot, Double rating,
                  Integer releaseYear, Integer numSeasons, Map<String, List<Episode>> seasons) {
        super(title, directors, actors, genres, ratings, plot, rating, releaseYear);
        this.numSeasons = numSeasons;
        this.seasons = seasons;
    }

    // Getters and Setters

    public Integer getNumSeasons() {
        return numSeasons;
    }

    public void setNumSeasons(Integer numSeasons) {
        this.numSeasons = numSeasons;
    }

    public Map<String, List<Episode>> getSeasons() {
        return seasons;
    }

    public void setSeasons(Map<String, List<Episode>> seasons) {
        this.seasons = seasons;
    }

    // Overridden displayInfo method
    @Override
    public void displayInfo() {
        // Implement displayInfo specific to Series
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
        if (this.getReleaseYear() != null) {
            System.out.println("Release Year: " + this.getReleaseYear());
        }
        if (this.getNumSeasons() != null) {
            System.out.println("Number of Seasons: " + this.getNumSeasons());
        }
        if (this.getSeasons() != null) {
            System.out.println("Episodes: " + this.getSeasons());
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
        result.append("Series: ").append(this.getName()).append("\n");

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

        if (this.getNumSeasons() != null) {
            result.append("     -seasons: ").append(this.getNumSeasons()).append("\n");
        } else {
            result.append("     -seasons: N/A\n");
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

