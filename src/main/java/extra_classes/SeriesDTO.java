package extra_classes;

import classes.Episode;
import classes.Rating;
import enums.Genre;

import java.util.List;
import java.util.Map;

public class SeriesDTO extends ProductionDTO{
    public Integer numSeasons;
    public Map<String, List<Episode>> seasons;

    public SeriesDTO(String title, String type, List<String> directors, List<String> actors,
                     List<Genre> genres, List<Rating> ratings, String plot, Double averageRating,
                     Integer releaseYear, Integer numSeasons, Map<String, List<Episode>> seasons) {
        super(title, type, directors, actors, genres, ratings, plot, averageRating, releaseYear);
        this.numSeasons = numSeasons;
        this.seasons = seasons;
    }
}
