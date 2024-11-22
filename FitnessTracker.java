import java.sql.*;
import java.util.Scanner;

public class FitnessTracker  {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/FitnessTracker";
    private static final String USER = "root";
    private static final String PASS = "Omkar@451784";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Welcome to the Fitness Tracker!");

            Integer loggedInUserId = null;

            while (true) {
                if (loggedInUserId == null) {
                    System.out.println("1. Register\n2. Login\n3. Exit");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            loggedInUserId = register(conn, scanner);
                            break;
                        case 2:
                            loggedInUserId = login(conn, scanner);
                            break;
                        case 3:
                            System.out.println("Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid choice, please try again.");
                    }
                } else {
                    System.out.println("1. Add Workout\n2. View Progress\n3. Calculate and Store BMI\n4. Logout");
                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    switch (choice) {
                        case 1:
                            addWorkout(conn, scanner, loggedInUserId);
                            break;
                        case 2:
                            viewProgress(conn, loggedInUserId);
                            break;
                        case 3:
                            calculateAndStoreBMI(conn, scanner, loggedInUserId);
                            break;
                        case 4:
                            loggedInUserId = null;
                            break;
                        default:
                            System.out.println("Invalid choice, please try again.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int register(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        System.out.println("Enter age:");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String query = "INSERT INTO users (username, password, age) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, age);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("User registered successfully!");
                return rs.getInt(1);  // Return the generated user ID
            }
        }
        return -1;  // Return -1 if the registration fails
    }

    private static int login(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful!");
                return rs.getInt("id");  // Return the user ID
            } else {
                System.out.println("Login failed. Please check your username and password.");
            }
        }
        return -1;  // Return -1 if the login fails
    }

    private static void addWorkout(Connection conn, Scanner scanner, int userId) throws SQLException {
        System.out.println("Enter workout description:");
        String workout = scanner.nextLine();
        System.out.println("Enter duration (minutes):");
        int duration = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String query = "INSERT INTO workouts (user_id, workout, duration, date) VALUES (?, ?, ?, CURDATE())";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, workout);
            pstmt.setInt(3, duration);
            pstmt.executeUpdate();
            System.out.println("Workout added successfully!");
        }
    }

    private static void viewProgress(Connection conn, int userId) throws SQLException {
        String query = "SELECT date, workout, duration FROM workouts WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getDate("date") + " - " + rs.getString("workout") + " for " + rs.getInt("duration") + " minutes");
            }
        }
    }

    private static void calculateAndStoreBMI(Connection conn, Scanner scanner, int userId) throws SQLException {
        System.out.println("Enter height (in meters):");
        double height = scanner.nextDouble();
        System.out.println("Enter weight (in kilograms):");
        double weight = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        double bmiValue = weight / (height * height);
        System.out.printf("Your BMI is: %.2f\n", bmiValue);

        String query = "INSERT INTO bmi (user_id, height, weight, bmi_value, date) VALUES (?, ?, ?, ?, CURDATE())";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, height);
            pstmt.setDouble(3, weight);
            pstmt.setDouble(4, bmiValue);
            pstmt.executeUpdate();
            System.out.println("BMI calculated and stored successfully!");
        }
    }
}
