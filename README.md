# IMDb-Style Application
## Author: Alexandru Ghita

This project is an implementation of an IMDb-style application, featuring both a fully functional command-line interface (CLI) and a graphical user interface (GUI).
The application supports complete testing of all features and provides persistence by saving changes to JSON files.
On subsequent runs, the application loads the saved data for continued use.
While the GUI demonstrates backend-to-frontend connectivity, it does not implement the full range of functionalities available in the CLI.

## Development Overview:
1) Time Investment: Approximately 50 hours over two weeks.
2) Difficulty:
* Time: Challenging due to the scope and required effort.
* Complexity: Moderate, with no particularly confusing or difficult concepts.

The project significantly deepened my understanding of object-oriented programming (OOP) principles, including encapsulation and design patterns.

## Implementation Details:
1) Core Design:
All classes adhere to the principle of encapsulation.
A Singleton instance is used in the IMDB class (the main class) to manage parsed JSON data stored in lists.
The Jackson library is used for parsing and writing JSON files.

2) Design Patterns:
* Factory Pattern: A UserFactory is implemented to create User objects.
* Builder Pattern: Used for the Information field within the User class to simplify object construction.
* Strategy Pattern: Enables the dynamic selection of user experience increment methods in the User class.
* Observer Pattern:
User objects act as observers for Rating, Review, and Production subjects.
Notifications are sent to the creator of a production when a review or rating is received.

3) Functionality:
* Full compliance with the project requirements, with all features testable via the CLI.
* Bonus Features: Enhanced sorting and filtering options for productions.
* Persistence:
Changes made during a session are saved to JSON files.
On the next run, the application successfully parses the saved data, enabling real data persistence.

4) GUI:
A functional graphical interface demonstrates connectivity between the backend and frontend.
Focused on showcasing the integration rather than replicating all CLI features.

5) Key Features:
Comprehensive IMDb-like functionality, including user management, production reviews, and ratings.
Integration of multiple design patterns for modularity and scalability.
Real-time data persistence using JSON files.
Bonus features for enhanced user experience, such as advanced sorting and filtering.

This project was both challenging and rewarding, providing an opportunity to implement and refine various OOP concepts and patterns
in a practical and impactful way. The result is a robust, extensible application with a solid foundation for further development.
