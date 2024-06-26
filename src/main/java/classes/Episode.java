package classes;

public class Episode {
    private String episodeName;
    private String duration;

    // Default Constructor
    public Episode() {
    }

    // Parameterized Constructor
    public Episode(String episodeName, String duration) {
        this.episodeName = episodeName;
        this.duration = duration;
    }

    // Getters and Setters
    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "[" + episodeName + " - " + duration + "]";
    }
}
