package classes;

public class Credentials {
    private String email;
    private String password;

    // Constructors
    public Credentials() {
    }
    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
