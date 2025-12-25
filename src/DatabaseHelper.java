import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class DatabaseHelper {
    // Database Config - CHANGE PASSWORD HERE
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private static final String USER = "root"; 
    private static final String PASS = "12345678"; // Apna MySQL Password yaha daalo

    // Connection Method
    public static Connection connect() {
        try {
            // Load Driver explicitly for older Java versions
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }

    // Add Expense
    public static void addExpense(String date, String category, double amount, String description) {
        // NOTE: We added 'date' as the first parameter ?
        String query = "INSERT INTO expenses (date, category, amount, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, date); // Now we set the date manually
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load Data into Table
    public static void loadData(DefaultTableModel model) {
        model.setRowCount(0); // Clear table before loading
        String query = "SELECT * FROM expenses ORDER BY date DESC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getDate("date"),
                    rs.getString("category"),
                    rs.getDouble("amount"),
                    rs.getString("description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Delete Expense
    public static void deleteExpense(int id) {
        String query = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}