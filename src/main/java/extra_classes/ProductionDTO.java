package extra_classes;

import classes.Rating;
import enums.Genre;

import java.util.List;

public class ProductionDTO {
    public String title;
    public String type;
    public List<String> directors;
    public List<String> actors;
    public List<Genre> genres;
    public List<Rating> ratings;
    public String plot;
    public Double averageRating;
    public Integer releaseYear;

    public ProductionDTO(String title, String type, List<String> directors, List<String> actors,
                         List<Genre> genres, List<Rating> ratings, String plot, Double averageRating,
                         Integer releaseYear) {
        this.title = title;
        this.type = type;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.ratings = ratings;
        this.plot = plot;
        this.averageRating = averageRating;
        this.releaseYear = releaseYear;
    }
}
