import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class DatabaseHelper {
    // Database Connection Variables
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker_db"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "12345678"; // Update this if your password changes

    // Connect Method
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 1. Add Expense (Now accepts Date parameter)
    public static void addExpense(String date, String category, double amount, String description) {
        String query = "INSERT INTO expenses (date, category, amount, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, date);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
            System.out.println("✅ Expense Added Successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Load Data (To populate the table)
    public static void loadData(DefaultTableModel model) {
        model.setRowCount(0); // Clear old data to avoid duplicates
        String query = "SELECT * FROM expenses ORDER BY id DESC";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("date");
                String cat = rs.getString("category");
                double amt = rs.getDouble("amount");
                String desc = rs.getString("description");
                model.addRow(new Object[]{id, date, cat, amt, desc});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Delete Expense
    public static void deleteExpense(int id) {
        String query = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
