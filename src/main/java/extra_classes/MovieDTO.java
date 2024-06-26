package extra_classes;

import classes.Rating;
import enums.Genre;

import java.util.List;

public class MovieDTO extends ProductionDTO{
    public String duration;

    public MovieDTO(String title, String type, List<String> directors, List<String> actors,
                    List<Genre> genres, List<Rating> ratings, String plot, Double averageRating,
                    Integer releaseYear, String duration) {
        super(title, type, directors, actors, genres, ratings, plot, averageRating, releaseYear);
        this.duration = duration;
    }
}
