package interfaces;

import classes.*;

public interface RequestsManager {
    // Method for creating a request
    void createRequest(Request r);

    // Method for deleting a request
    void removeRequest(Request r);
}