# IMDB-Style Application  
**Author**: Alexandru Ghita  

This project is an IMDb-style application built in **Java**, featuring both a **Command-Line Interface (CLI)** and a **Graphical User Interface (GUI)**. The application provides a comprehensive platform for managing users, productions, reviews, and ratings, with full data persistence using **JSON files**.  

---

## **Features**  
- **User Management**: Register, log in, and manage user accounts with different roles (e.g., regular user, admin).  
- **Production Management**: Add, edit, and delete movies and series, complete with details like descriptions, genres, and cast.  
- **Reviews and Ratings**: Users can leave reviews and ratings for productions, with notifications sent to production creators.  
- **Advanced Sorting and Filtering**: Enhanced options for searching and organizing productions.  
- **Persistence**: All changes are saved to JSON files and reloaded on subsequent runs.  
- **GUI**: A functional graphical interface built with **Java Swing** demonstrates backend-to-frontend connectivity.  

---

## **Technologies Used**  
- **Java**: Core programming language for the application.  
- **Jackson Library**: For parsing and writing JSON files to enable data persistence.  
- **Java Swing**: For building the graphical user interface.  

---

## **Design Patterns**  
The application leverages several design patterns to ensure modularity, scalability, and maintainability:  
- **Singleton**: Used in the `IMDB` class to manage parsed JSON data stored in lists.  
- **Factory Pattern**: A `UserFactory` creates `User` objects based on roles.  
- **Builder Pattern**: Simplifies the construction of the `Information` field within the `User` class.  
- **Strategy Pattern**: Enables dynamic selection of user experience increment methods in the `User` class.  
- **Observer Pattern**: Users act as observers for `Rating`, `Review`, and `Production` subjects, receiving notifications when updates occur.  

---

## **How to Run**  
1. Clone the repository to your local machine.  
2. Navigate to the `src/main/java/classes` folder.  
3. Run the `IMDB.java` file.  
4. Choose between the **Terminal (CLI)** or **Graphic Interface (GUI)** option.  
5. Use the test account with email `"a"` and password `"a"` to explore the environment.  

---

## **Implementation Details**  
### **Core Design**  
- All classes adhere to the principle of **encapsulation**.  
- Data is stored in JSON files and parsed using the **Jackson library**.  
- The application supports **real-time data persistence**, ensuring changes are saved and reloaded on subsequent runs.  

### **Functionality**  
- **CLI**: Full compliance with project requirements, enabling testing of all features.  
- **GUI**: Demonstrates backend-to-frontend connectivity using **Java Swing**, focusing on integration rather than replicating all CLI features.  
- **Bonus Features**: Enhanced sorting and filtering options for productions.  

---

## **Development Overview**  
- **Time Investment**: Approximately 50 hours over two weeks.  
- **Difficulty**:  
  - **Time**: Challenging due to the scope and required effort.  
  - **Complexity**: Moderate, with no particularly confusing or difficult concepts.  

This project significantly deepened my understanding of **object-oriented programming (OOP)** principles, including **encapsulation** and **design patterns**, while providing a practical and impactful application.  

---

## **Key Takeaways**  
- A robust, extensible application with a solid foundation for further development.  
- Hands-on experience with **Java**, **JSON parsing**, **GUI development**, and **design patterns**.  
- Improved skills in **problem-solving**, **software design**, and **project management**.  

---

This project was both challenging and rewarding, offering an opportunity to implement and refine various OOP concepts in a practical and impactful way.  
