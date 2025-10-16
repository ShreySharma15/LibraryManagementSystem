import java.sql.*;
import java.util.Scanner;

public class LibraryManagement {

    // Database connection details
    static final String DB_URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "Shrey";  // username
    static final String PASS = "R@nd0mMain$"; //MySQL password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        System.out.println("===== Welcome to Library Management System =====");

        while (true) {
            System.out.println("\n1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addBook(sc);
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    issueBook(sc);
                    break;
                case 4:
                    returnBook(sc);
                    break;
                case 5:
                    System.out.println("Exiting... Thank you for using the system!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // Method 1: Add Book
    static void addBook(Scanner sc) {
        System.out.print("Enter book title: ");
        String title = sc.nextLine();
        System.out.print("Enter author name: ");
        String author = sc.nextLine();

        String query = "INSERT INTO books(title, author) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, title);
            ps.setString(2, author);
            ps.executeUpdate();

            System.out.println("Book added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    // Method 2: View Books
    static void viewBooks() {
        String query = "SELECT * FROM books";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== Available Books ===");
            System.out.printf("%-5s %-25s %-20s %-10s\n", "ID", "Title", "Author", "Available");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean available = rs.getBoolean("available");

                System.out.printf("%-5d %-25s %-20s %-10s\n", id, title, author, available ? "Yes" : "No");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
    }

    // Method 3: Issue Book
    static void issueBook(Scanner sc) {
        System.out.print("Enter Book ID to issue: ");
        int bookId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter borrower name: ");
        String borrower = sc.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {

            // Check if book exists and is available
            PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id = ?");
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getBoolean("available")) {
                // Insert issue record
                PreparedStatement issue = conn.prepareStatement(
                        "INSERT INTO issued_books(book_id, issued_to, issue_date) VALUES(?, ?, CURDATE())");
                issue.setInt(1, bookId);
                issue.setString(2, borrower);
                issue.executeUpdate();

                // Update availability
                PreparedStatement update = conn.prepareStatement("UPDATE books SET available = FALSE WHERE id = ?");
                update.setInt(1, bookId);
                update.executeUpdate();

                System.out.println("Book issued successfully!");
            } else {
                System.out.println("Book not available or invalid Book ID!");
            }

        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    // Method 4: Return Book
    static void returnBook(Scanner sc) {
        System.out.print("Enter Book ID to return: ");
        int bookId = sc.nextInt();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Update return date
            PreparedStatement updateReturn = conn.prepareStatement(
                    "UPDATE issued_books SET return_date = CURDATE() WHERE book_id = ? AND return_date IS NULL");
            updateReturn.setInt(1, bookId);
            int rows = updateReturn.executeUpdate();

            if (rows > 0) {
                // Mark book as available again
                PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET available = TRUE WHERE id = ?");
                updateBook.setInt(1, bookId);
                updateBook.executeUpdate();

                System.out.println("Book returned successfully!");
            } else {
                System.out.println("No matching issue record found!");
            }

        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }
}