package interfaces;

import classes.*;

public interface StaffInterface {
    // Method for adding a production to the system
    void addProductionSystem(Production p);

    // Method for adding an actor to the system
    void addActorSystem(Actor a);

    // Method for removing a production added by the user from the system
    void removeProductionSystem(String name);

    // Method for removing an actor added by the user from the system
    void removeActorSystem(String name);

    // Method for updating details about a production added by the user in the system
    void updateProduction(Production p);

    // Method for updating details about an actor added by the user in the system
    void updateActor(Actor a);
}
