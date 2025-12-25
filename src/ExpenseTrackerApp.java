import com.toedter.calendar.JDateChooser; // IMPORT THIS!
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseTrackerApp extends JFrame {
    
    private JTextField amountField, descField;
    private JComboBox<String> categoryBox;
    private JDateChooser dateChooser; // The Calendar Component
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public ExpenseTrackerApp() {
        setTitle("Expense Tracker Project");
        setSize(1000, 650); // Made window slightly wider
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP PANEL (INPUTS) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        inputPanel.setBackground(new Color(230, 240, 255));

        // 1. Date Picker (NEW)
        inputPanel.add(new JLabel("Date:"));
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Set default to today
        dateChooser.setDateFormatString("yyyy-MM-dd"); // MySQL format
        dateChooser.setPreferredSize(new Dimension(120, 25));
        inputPanel.add(dateChooser);

        // 2. Category
        inputPanel.add(new JLabel("Category:"));
        String[] categories = {"Food", "Travel", "Shopping", "Bills", "Other"};
        categoryBox = new JComboBox<>(categories);
        inputPanel.add(categoryBox);

        // 3. Amount
        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(8);
        inputPanel.add(amountField);

        // 4. Description
        inputPanel.add(new JLabel("Description:"));
        descField = new JTextField(12);
        inputPanel.add(descField);

        // 5. Add Button
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        inputPanel.add(addButton);

        // 6. Delete Button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(204, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (TABLE) ---
        String[] columns = {"ID", "Date", "Category", "Amount", "Description"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BOTTOM PANEL (TOTAL) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total Expense: 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- ACTION LOGIC ---
        
        addButton.addActionListener(e -> {
            try {
                // Get Date from DateChooser
                if(dateChooser.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Please select a date!");
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(dateChooser.getDate());

                String cat = (String) categoryBox.getSelectedItem();
                double amt = Double.parseDouble(amountField.getText());
                String desc = descField.getText();

                if(desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description cannot be empty!");
                    return;
                }

                // Pass date to DatabaseHelper
                DatabaseHelper.addExpense(date, cat, amt, desc);
                
                refreshData();
                amountField.setText("");
                descField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric Amount!");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                DatabaseHelper.deleteExpense(id);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete!");
            }
        });

        refreshData();
        setVisible(true);
    }

    private void refreshData() {
        DatabaseHelper.loadData(model);
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += (double) model.getValueAt(i, 3);
        }
        totalLabel.setText("Total Expense: " + total);
    }

    public static void main(String[] args) {
        new ExpenseTrackerApp();
    }
}
// End of application