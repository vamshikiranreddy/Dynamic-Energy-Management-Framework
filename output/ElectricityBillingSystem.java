import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class ElectricityBillingSystem {
    public static void main(String[] args) {
        // Start with splash screen
        SwingUtilities.invokeLater(() -> {
            new SplashScreen().setVisible(true);
        });
    }
}

class DatabaseConnection implements AutoCloseable {
    private static final String URL = "jdbc:mysql://localhost:3306/ebs";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // Change to your MySQL password
    		
    private Connection connection;
    private Statement statement;
    
    public DatabaseConnection() throws SQLException, ClassNotFoundException {
    	Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        statement = connection.createStatement();
    }
    
    public PreparedStatement prepareStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }
    
    public ResultSet executeQuery(String query) throws SQLException {
        return statement.executeQuery(query);
    }
    
    public int executeUpdate(String query) throws SQLException {
        return statement.executeUpdate(query);
    }
    
    @Override
    public void close() throws SQLException {
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
    
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Electricity Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Remove window decorations
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(0, 120, 215));
        
        // Load splash image from resources
        ImageIcon icon = loadImage("/images/splash.jpg");
        
        if (icon.getImage() != null) {
            Image image = icon.getImage().getScaledInstance(720, 550, Image.SCALE_SMOOTH);
            contentPane.add(new JLabel(new ImageIcon(image)), BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel("Electricity Billing System", JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));
            label.setForeground(Color.WHITE);
            contentPane.add(label, BorderLayout.CENTER);
        }
        
        // Add loading progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(255, 215, 0));
        progressBar.setBackground(new Color(0, 80, 180));
        contentPane.add(progressBar, BorderLayout.SOUTH);
        
        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        
        // Animation and loading thread
        new Thread(() -> {
            try {
                // Simulate loading process
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30);
                }
                
                // Close splash and open login
                dispose();
                new LoginScreen().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private ImageIcon loadImage(String path) {
        try {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            }
            return new ImageIcon();
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return new ImageIcon();
        }
    }
}

class LoginScreen extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    public LoginScreen() {
        setTitle("Login Page");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 120, 215), 0, getHeight(), new Color(0, 80, 180));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Left panel with image
        ImageIcon icon = loadImage("/images/login.jpg");
        JLabel imageLabel = new JLabel(icon);
        mainPanel.add(imageLabel, BorderLayout.WEST);
        
        // Center panel with login form
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("ADMIN LOGIN", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(userLabel, gbc);
        
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);
        
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(255, 215, 0));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(loginButton, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Footer with copyright info
        JLabel footerLabel = new JLabel("© 2023 Electricity Billing System", JLabel.CENTER);
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private ImageIcon loadImage(String path) {
        try {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image image = icon.getImage().getScaledInstance(400, 500, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            }
            return new ImageIcon();
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return new ImageIcon();
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Login")) {
            try (DatabaseConnection conn = new DatabaseConnection()) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter both username and password");
                    return;
                }
                
                String query = "SELECT * FROM login WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    new MainApplication().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid login credentials");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}

class MainApplication extends JFrame implements ActionListener {
    public MainApplication() {
        setTitle("Electricity Billing System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create menu bar with modern styling
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 120, 215));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Master Menu
        JMenu masterMenu = createMenu("Master", 'M');
        JMenuItem newCustomerItem = createMenuItem("New Customer", "user_add.png", 'N', KeyEvent.VK_N);
        JMenuItem customerDetailsItem = createMenuItem("Customer Details", "users.png", 'D', KeyEvent.VK_D);
        masterMenu.add(newCustomerItem);
        masterMenu.add(customerDetailsItem);
        
        // Billing Menu
        JMenu billingMenu = createMenu("Billing", 'B');
        JMenuItem calculateBillItem = createMenuItem("Calculate Bill", "calculator.png", 'C', KeyEvent.VK_C);
        JMenuItem generateBillItem = createMenuItem("Generate Bill", "invoice.png", 'G', KeyEvent.VK_G);
        JMenuItem lastBillItem = createMenuItem("Bill History", "history.png", 'H', KeyEvent.VK_H);
        billingMenu.add(calculateBillItem);
        billingMenu.add(generateBillItem);
        billingMenu.add(lastBillItem);
        
        // Payment Menu
        JMenu paymentMenu = createMenu("Payment", 'P');
        JMenuItem payBillItem = createMenuItem("Pay Bill", "payment.png", 'P', KeyEvent.VK_P);
        paymentMenu.add(payBillItem);
        
        // Utility Menu
        JMenu utilityMenu = createMenu("Utility", 'U');
        JMenuItem notepadItem = createMenuItem("Notepad", "notepad.png", 'N', KeyEvent.VK_N);
        JMenuItem calculatorItem = createMenuItem("Calculator", "calculator.png", 'C', KeyEvent.VK_C);
        utilityMenu.add(notepadItem);
        utilityMenu.add(calculatorItem);
        
        // Help Menu
        JMenu helpMenu = createMenu("Help", 'H');
        JMenuItem aboutItem = createMenuItem("About", "info.png", 'A', KeyEvent.VK_A);
        helpMenu.add(aboutItem);
        
        // Exit Menu
        JMenu exitMenu = createMenu("Exit", 'X');
        JMenuItem exitItem = createMenuItem("Exit", "exit.png", 'X', KeyEvent.VK_X);
        exitMenu.add(exitItem);
        
        // Add all menus to menu bar
        menuBar.add(masterMenu);
        menuBar.add(billingMenu);
        menuBar.add(paymentMenu);
        menuBar.add(utilityMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        menuBar.add(exitMenu);
        
        setJMenuBar(menuBar);
        
        // Dashboard panel
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        
        // Add welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Electricity Billing System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        dashboardPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Add quick stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try (DatabaseConnection conn = new DatabaseConnection()) {
            // Total customers
            ResultSet rs = conn.executeQuery("SELECT COUNT(*) FROM customer");
            rs.next();
            statsPanel.add(createStatPanel("Total Customers", rs.getString(1), "customers.png"));
            
            // Pending bills
            rs = conn.executeQuery("SELECT COUNT(*) FROM bill WHERE status = 'Pending'");
            rs.next();
            statsPanel.add(createStatPanel("Pending Bills", rs.getString(1), "pending.png"));
            
            // Total revenue
            rs = conn.executeQuery("SELECT SUM(amount) FROM bill WHERE status = 'Paid'");
            rs.next();
            statsPanel.add(createStatPanel("Total Revenue", "₹" + rs.getString(1), "revenue.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(dashboardPanel);
        
        // Add action listeners
        newCustomerItem.addActionListener(this);
        customerDetailsItem.addActionListener(this);
        calculateBillItem.addActionListener(this);
        generateBillItem.addActionListener(this);
        lastBillItem.addActionListener(this);
        payBillItem.addActionListener(this);
        notepadItem.addActionListener(this);
        calculatorItem.addActionListener(this);
        aboutItem.addActionListener(this);
        exitItem.addActionListener(this);
    }
    
    private JMenu createMenu(String text, char mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        return menu;
    }
    
    private JMenuItem createMenuItem(String text, String iconName, char mnemonic, int acceleratorKey) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        item.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, ActionEvent.CTRL_MASK));
        item.setFont(new Font("Arial", Font.PLAIN, 14));
        
        if (iconName != null) {
            ImageIcon icon = loadImage("/images/icons/" + iconName);
            if (icon.getImage() != null) {
                Image image = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                item.setIcon(new ImageIcon(image));
            }
        }
        
        return item;
    }
    
    private JPanel createStatPanel(String title, String value, String iconName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title label
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Value label
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 120, 215));
        panel.add(valueLabel, BorderLayout.CENTER);
        
        // Icon
        ImageIcon icon = loadImage("/images/icons/" + iconName);
        if (icon.getImage() != null) {
            Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(image), JLabel.CENTER);
            panel.add(iconLabel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private ImageIcon loadImage(String path) {
        try {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            }
            return new ImageIcon();
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return new ImageIcon();
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case "New Customer":
                new NewCustomerForm().setVisible(true);
                break;
            case "Customer Details":
                new CustomerDetails().setVisible(true);
                break;
            case "Calculate Bill":
                new CalculateBill().setVisible(true);
                break;
            case "Generate Bill":
                new GenerateBill().setVisible(true);
                break;
            case "Bill History":
                new BillHistory().setVisible(true);
                break;
            case "Pay Bill":
                new PayBill().setVisible(true);
                break;
            case "Notepad":
                try {
                    Runtime.getRuntime().exec("notepad.exe");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Calculator":
                try {
                    Runtime.getRuntime().exec("calc.exe");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "About":
                showAboutDialog();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Electricity Billing System\nVersion 1.0\n\n" +
            "Developed by:\nYour Name\n\n" +
            "© 2023 All Rights Reserved",
            "About",
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(getClass().getResource("/images/icons/info_large.png")));
    }
}

class NewCustomerForm extends JFrame implements ActionListener {
    private JTextField nameField, meterField, addressField, stateField, cityField, emailField, phoneField;
    
    public NewCustomerForm() {
        setTitle("Add New Customer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form title
        JLabel titleLabel = new JLabel("Customer Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 120, 215));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        
        // Create form fields
        nameField = createFormField("Full Name");
        meterField = createFormField("Meter Number");
        addressField = createFormField("Address");
        stateField = createFormField("State");
        cityField = createFormField("City");
        emailField = createFormField("Email");
        phoneField = createFormField("Phone Number");
        
        // Add fields to form panel
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Meter Number:"));
        formPanel.add(meterField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("State:"));
        formPanel.add(stateField);
        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0, 120, 215));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.addActionListener(this);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.addActionListener(this);
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JTextField createFormField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Submit")) {
            try (DatabaseConnection conn = new DatabaseConnection()) {
                // Validate fields
                if (nameField.getText().isEmpty() || meterField.getText().isEmpty() || 
                    addressField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all required fields");
                    return;
                }
                
                // Insert customer
                String query = "INSERT INTO customer (name, meter_number, address, state, city, email, phone) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, meterField.getText());
                stmt.setString(3, addressField.getText());
                stmt.setString(4, stateField.getText());
                stmt.setString(5, cityField.getText());
                stmt.setString(6, emailField.getText());
                stmt.setString(7, phoneField.getText());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Customer added successfully!");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add customer");
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) { // Duplicate entry
                    JOptionPane.showMessageDialog(this, "Meter number already exists");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else if (ae.getActionCommand().equals("Cancel")) {
            this.dispose();
        }
    }
}

class CustomerDetails extends JFrame {
    public CustomerDetails() {
        setTitle("Customer Details");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Customer Details", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 120, 215));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 120, 215));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        searchPanel.add(new JLabel("Search by Name or Meter No:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Table
        JTable customerTable = new JTable();
        customerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        customerTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 120, 215));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton printButton = new JButton("Print");
        printButton.setBackground(new Color(0, 120, 215));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(printButton);
        
        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load customer data
        loadCustomerData(customerTable, "");
        
        // Add action listeners
        searchButton.addActionListener(e -> {
            loadCustomerData(customerTable, searchField.getText());
        });
        
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadCustomerData(customerTable, "");
        });
        
        printButton.addActionListener(e -> {
            try {
                customerTable.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error printing table: " + ex.getMessage());
            }
        });
    }
    
    private void loadCustomerData(JTable table, String searchTerm) {
        try (DatabaseConnection conn = new DatabaseConnection()) {
            String query = "SELECT name, meter_number, address, state, city, email, phone FROM customer";
            
            if (!searchTerm.isEmpty()) {
                query += " WHERE name LIKE ? OR meter_number LIKE ?";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            
            if (!searchTerm.isEmpty()) {
                stmt.setString(1, "%" + searchTerm + "%");
                stmt.setString(2, "%" + searchTerm + "%");
            }
            
            ResultSet rs = stmt.executeQuery();
            
            // Create table model
            Vector<String> columns = new Vector<>();
            columns.add("Name");
            columns.add("Meter Number");
            columns.add("Address");
            columns.add("State");
            columns.add("City");
            columns.add("Email");
            columns.add("Phone");
            
            Vector<Vector<String>> data = new Vector<>();
            
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("name"));
                row.add(rs.getString("meter_number"));
                row.add(rs.getString("address"));
                row.add(rs.getString("state"));
                row.add(rs.getString("city"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                data.add(row);
            }
            
            table.setModel(new DefaultTableModel(data, columns));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customer data: " + e.getMessage());
        }
    }
}

class CalculateBill extends JFrame implements ActionListener {
    private JComboBox<String> meterCombo, monthCombo;
    private JTextField unitsField;
    private JTextArea resultArea;
    
    public CalculateBill() {
        setTitle("Calculate Bill");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Calculate Electricity Bill", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 120, 215));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        
        // Meter number combo
        meterCombo = new JComboBox<>();
        meterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Month combo
        monthCombo = new JComboBox<>(new String[] {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Units field
        unitsField = new JTextField();
        unitsField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add components to form panel
        formPanel.add(new JLabel("Meter Number:"));
        formPanel.add(meterCombo);
        formPanel.add(new JLabel("Month:"));
        formPanel.add(monthCombo);
        formPanel.add(new JLabel("Units Consumed:"));
        formPanel.add(unitsField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(new Color(0, 120, 215));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.addActionListener(this);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(200, 0, 0));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.addActionListener(this);
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        
        // Result area
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load meter numbers
        loadMeterNumbers();
    }
    
    private void loadMeterNumbers() {
        try (DatabaseConnection conn = new DatabaseConnection()) {
            ResultSet rs = conn.executeQuery("SELECT meter_number FROM customer");
            while (rs.next()) {
                meterCombo.addItem(rs.getString("meter_number"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading meter numbers: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Calculate")) {
            calculateBill();
        } else if (ae.getActionCommand().equals("Clear")) {
            unitsField.setText("");
            resultArea.setText("");
        }
    }
    
    private void calculateBill() {
        try {
            // Validate inputs
            if (meterCombo.getSelectedItem() == null || monthCombo.getSelectedItem() == null || 
                unitsField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }
            
            String meterNumber = meterCombo.getSelectedItem().toString();
            String month = monthCombo.getSelectedItem().toString();
            double units = Double.parseDouble(unitsField.getText());
            
            if (units <= 0) {
                JOptionPane.showMessageDialog(this, "Units consumed must be greater than 0");
                return;
            }
            
            // Calculate bill amount
            double amount = 0;
            if (units <= 100) {
                amount = units * 4.50;
            } else if (units <= 200) {
                amount = 100 * 4.50 + (units - 100) * 6.00;
            } else {
                amount = 100 * 4.50 + 100 * 6.00 + (units - 200) * 7.50;
            }
            
            // Get tax rates
            try (DatabaseConnection conn = new DatabaseConnection()) {
                ResultSet rs = conn.executeQuery("SELECT * FROM tax WHERE id = 1");
                if (rs.next()) {
                    double meterRent = rs.getDouble("meter_rent");
                    double serviceCharge = rs.getDouble("service_charge");
                    double fixedCharge = rs.getDouble("fixed_charge");
                    double gst = rs.getDouble("gst");
                    
                    // Calculate total amount with taxes
                    double totalAmount = amount + meterRent + serviceCharge + fixedCharge;
                    double gstAmount = totalAmount * gst / 100;
                    totalAmount += gstAmount;
                    
                    // Display results
                    resultArea.setText(String.format(
                        "ELECTRICITY BILL\n\n" +
                        "Meter Number: %s\n" +
                        "Month: %s\n" +
                        "Units Consumed: %.2f\n\n" +
                        "Base Amount: ₹%.2f\n" +
                        "Meter Rent: ₹%.2f\n" +
                        "Service Charge: ₹%.2f\n" +
                        "Fixed Charge: ₹%.2f\n" +
                        "GST (%.2f%%): ₹%.2f\n\n" +
                        "TOTAL PAYABLE: ₹%.2f",
                        meterNumber, month, units, amount, meterRent, 
                        serviceCharge, fixedCharge, gst, gstAmount, totalAmount
                    ));
                    
                    // Save to database
                    String query = "INSERT INTO bill (meter_number, month, units, amount) " +
                                  "VALUES (?, ?, ?, ?) " +
                                  "ON DUPLICATE KEY UPDATE units = ?, amount = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, meterNumber);
                    stmt.setString(2, month);
                    stmt.setDouble(3, units);
                    stmt.setDouble(4, totalAmount);
                    stmt.setDouble(5, units);
                    stmt.setDouble(6, totalAmount);
                    
                    stmt.executeUpdate();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid units consumed");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating bill: " + e.getMessage());
        }
    }
}

class GenerateBill extends JFrame implements ActionListener {
    private JComboBox<String> meterCombo, monthCombo;
    private JTextArea billArea;
    
    public GenerateBill() {
        setTitle("Generate Bill");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Generate Electricity Bill", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 120, 215));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        
        // Meter number combo
        meterCombo = new JComboBox<>();
        meterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Month combo
        monthCombo = new JComboBox<>(new String[] {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add components to form panel
        formPanel.add(new JLabel("Meter Number:"));
        formPanel.add(meterCombo);
        formPanel.add(new JLabel("Month:"));
        formPanel.add(monthCombo);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton generateButton = new JButton("Generate");
        generateButton.setBackground(new Color(0, 120, 215));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.addActionListener(this);
        
        JButton printButton = new JButton("Print");
        printButton.setBackground(new Color(0, 120, 215));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        printButton.addActionListener(this);
        
        buttonPanel.add(generateButton);
        buttonPanel.add(printButton);
        
        // Bill area
        billArea = new JTextArea();
        billArea.setFont(new Font("Arial", Font.PLAIN, 14));
        billArea.setEditable(false);
        billArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(billArea);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load meter numbers
        loadMeterNumbers();
    }
    
    private void loadMeterNumbers() {
        try (DatabaseConnection conn = new DatabaseConnection()) {
            ResultSet rs = conn.executeQuery("SELECT meter_number FROM customer");
            while (rs.next()) {
                meterCombo.addItem(rs.getString("meter_number"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading meter numbers: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Generate")) {
            generateBill();
        } else if (ae.getActionCommand().equals("Print")) {
            try {
                billArea.print();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error printing bill: " + e.getMessage());
            }
        }
    }
    
    private void generateBill() {
        try {
            if (meterCombo.getSelectedItem() == null || monthCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select meter number and month");
                return;
            }
            
            String meterNumber = meterCombo.getSelectedItem().toString();
            String month = monthCombo.getSelectedItem().toString();
            
            try (DatabaseConnection conn = new DatabaseConnection()) {
                // Get customer details
                String customerQuery = "SELECT * FROM customer WHERE meter_number = ?";
                PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
                customerStmt.setString(1, meterNumber);
                ResultSet customerRs = customerStmt.executeQuery();
                
                // Get bill details
                String billQuery = "SELECT * FROM bill WHERE meter_number = ? AND month = ?";
                PreparedStatement billStmt = conn.prepareStatement(billQuery);
                billStmt.setString(1, meterNumber);
                billStmt.setString(2, month);
                ResultSet billRs = billStmt.executeQuery();
                
                // Get tax rates
                String taxQuery = "SELECT * FROM tax WHERE id = 1";
                ResultSet taxRs = conn.executeQuery(taxQuery);
                
                if (customerRs.next() && billRs.next() && taxRs.next()) {
                    // Format bill
                    StringBuilder bill = new StringBuilder();
                    bill.append(String.format("%50s\n", "ELECTRICITY BILL"));
                    bill.append(String.format("%50s\n\n", "=============================="));
                    
                    // Customer details
                    bill.append(String.format("%-20s: %s\n", "Customer Name", customerRs.getString("name")));
                    bill.append(String.format("%-20s: %s\n", "Meter Number", customerRs.getString("meter_number")));
                    bill.append(String.format("%-20s: %s\n", "Address", customerRs.getString("address")));
                    bill.append(String.format("%-20s: %s\n", "City", customerRs.getString("city")));
                    bill.append(String.format("%-20s: %s\n", "State", customerRs.getString("state")));
                    bill.append(String.format("%-20s: %s\n", "Phone", customerRs.getString("phone")));
                    bill.append(String.format("%-20s: %s\n\n", "Email", customerRs.getString("email")));
                    
                    // Bill details
                    bill.append(String.format("%-20s: %s\n", "Billing Month", month));
                    bill.append(String.format("%-20s: %.2f units\n\n", "Units Consumed", billRs.getDouble("units")));
                    
                    // Charges
                    bill.append(String.format("%-30s₹%10.2f\n", "Base Amount:", billRs.getDouble("amount")));
                    bill.append(String.format("%-30s₹%10.2f\n", "Meter Rent:", taxRs.getDouble("meter_rent")));
                    bill.append(String.format("%-30s₹%10.2f\n", "Service Charge:", taxRs.getDouble("service_charge")));
                    bill.append(String.format("%-30s₹%10.2f\n", "Fixed Charge:", taxRs.getDouble("fixed_charge")));
                    bill.append(String.format("%-30s₹%10.2f\n\n", "GST (" + taxRs.getDouble("gst") + "%):", 
                        billRs.getDouble("amount") * taxRs.getDouble("gst") / 100));
                    
                    // Total
                    bill.append(String.format("%-30s₹%10.2f\n", "TOTAL PAYABLE:", billRs.getDouble("amount")));
                    bill.append(String.format("\n%50s\n", "=============================="));
                    bill.append(String.format("%50s\n", "Thank you for your payment!"));
                    
                    billArea.setText(bill.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "No bill found for selected meter and month");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating bill: " + e.getMessage());
        }
    }
}

class BillHistory extends JFrame {
    private JComboBox<String> meterCombo;
    private JTable billTable;
    
    public BillHistory() {
        setTitle("Bill History");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Bill Payment History", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 120, 215));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // Meter number combo
        meterCombo = new JComboBox<>();
        meterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 120, 215));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        searchPanel.add(new JLabel("Meter Number:"));
        searchPanel.add(meterCombo);
        searchPanel.add(searchButton);
        
        // Table
        billTable = new JTable();
        billTable.setFont(new Font("Arial", Font.PLAIN, 12));
        billTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(billTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 120, 215));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton printButton = new JButton("Print");
        printButton.setBackground(new Color(0, 120, 215));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(printButton);
        
        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load meter numbers
        loadMeterNumbers();
        
        // Load all bills initially
        loadBillData("");
        
        // Add action listeners
        searchButton.addActionListener(e -> {
            if (meterCombo.getSelectedItem() != null) {
                loadBillData(meterCombo.getSelectedItem().toString());
            }
        });
        
        refreshButton.addActionListener(e -> {
            meterCombo.setSelectedIndex(-1);
            loadBillData("");
        });
        
        printButton.addActionListener(e -> {
            try {
                billTable.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error printing table: " + ex.getMessage());
            }
        });
    }
    
    private void loadMeterNumbers() {
        try (DatabaseConnection conn = new DatabaseConnection()) {
            ResultSet rs = conn.executeQuery("SELECT meter_number FROM customer");
            while (rs.next()) {
                meterCombo.addItem(rs.getString("meter_number"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading meter numbers: " + e.getMessage());
        }
    }
    
    private void loadBillData(String meterNumber) {
        try (DatabaseConnection conn = new DatabaseConnection()) {
            String query = "SELECT b.meter_number, c.name, b.month, b.units, b.amount, b.status, b.payment_date " +
                          "FROM bill b JOIN customer c ON b.meter_number = c.meter_number";
            
            if (!meterNumber.isEmpty()) {
                query += " WHERE b.meter_number = ?";
            }
            
            query += " ORDER BY b.month DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            
            if (!meterNumber.isEmpty()) {
                stmt.setString(1, meterNumber);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            // Create table model
            Vector<String> columns = new Vector<>();
            columns.add("Meter Number");
            columns.add("Customer Name");
            columns.add("Month");
            columns.add("Units");
            columns.add("Amount");
            columns.add("Status");
            columns.add("Payment Date");
            
            Vector<Vector<String>> data = new Vector<>();
            
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("meter_number"));
                row.add(rs.getString("name"));
                row.add(rs.getString("month"));
                row.add(rs.getString("units"));
                row.add("₹" + rs.getString("amount"));
                row.add(rs.getString("status"));
                
                Date paymentDate = rs.getDate("payment_date");
                row.add(paymentDate != null ? paymentDate.toString() : "N/A");
                
                data.add(row);
            }
            
            billTable.setModel(new DefaultTableModel(data, columns));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bill data: " + e.getMessage());
        }
    }
}

class PayBill extends JFrame implements ActionListener {
    private JComboBox<String> meterCombo, monthCombo;
    private JTextField amountField;
    private JTextArea receiptArea;
    
    public PayBill() {
        setTitle("Pay Bill");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Pay Electricity Bill", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 120, 215));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        
        // Meter number combo
        meterCombo = new JComboBox<>();
        meterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Month combo
        monthCombo = new JComboBox<>();
        monthCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Amount field
        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setEditable(false);
        
        // Add components to form panel
        formPanel.add(new JLabel("Meter Number:"));
        formPanel.add(meterCombo);
        formPanel.add(new JLabel("Month:"));
        formPanel.add(monthCombo);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loadButton = new JButton("Load Bill");
        loadButton.setBackground(new Color(0, 120, 215));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadButton.addActionListener(this);
        
        JButton payButton = new JButton("Pay Now");
        payButton.setBackground(new Color(0, 120, 215));
        payButton.setForeground(Color.WHITE);
        payButton.setFont(new Font("Arial", Font.BOLD, 14));
        payButton.addActionListener(this);
        
        buttonPanel.add(loadButton);
        buttonPanel.add(payButton);
        
        // Receipt area
        receiptArea = new JTextArea();
        receiptArea.setFont(new Font("Arial", Font.PLAIN, 14));
        receiptArea.setEditable(false);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Load meter numbers
        loadMeterNumbers();
        
        // Add action listener for meter combo
        meterCombo.addActionListener(e -> {
            if (meterCombo.getSelectedItem() != null) {
                loadPendingBills(meterCombo.getSelectedItem().toString());
            }
        });
    }
    
    private void loadMeterNumbers() {
        try (DatabaseConnection conn = new DatabaseConnection()) {
            ResultSet rs = conn.executeQuery("SELECT meter_number FROM customer");
            while (rs.next()) {
                meterCombo.addItem(rs.getString("meter_number"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading meter numbers: " + e.getMessage());
        }
    }
    
    private void loadPendingBills(String meterNumber) {
        monthCombo.removeAllItems();
        amountField.setText("");
        receiptArea.setText("");
        
        try (DatabaseConnection conn = new DatabaseConnection()) {
            String query = "SELECT month, amount FROM bill WHERE meter_number = ? AND status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, meterNumber);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                monthCombo.addItem(rs.getString("month"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading pending bills: " + e.getMessage());
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Load Bill")) {
            loadBillAmount();
        } else if (ae.getActionCommand().equals("Pay Now")) {
            payBill();
        }
    }
    
    private void loadBillAmount() {
        try {
            if (meterCombo.getSelectedItem() == null || monthCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select meter number and month");
                return;
            }
            
            String meterNumber = meterCombo.getSelectedItem().toString();
            String month = monthCombo.getSelectedItem().toString();
            
            try (DatabaseConnection conn = new DatabaseConnection()) {
                String query = "SELECT amount FROM bill WHERE meter_number = ? AND month = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, meterNumber);
                stmt.setString(2, month);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    amountField.setText("₹" + rs.getString("amount"));
                } else {
                    JOptionPane.showMessageDialog(this, "No bill found for selected meter and month");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bill amount: " + e.getMessage());
        }
    }
    
    private void payBill() {
        try {
            if (meterCombo.getSelectedItem() == null || monthCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select meter number and month");
                return;
            }
            
            String meterNumber = meterCombo.getSelectedItem().toString();
            String month = monthCombo.getSelectedItem().toString();
            
            try (DatabaseConnection conn = new DatabaseConnection()) {
                // Update bill status
                String updateQuery = "UPDATE bill SET status = 'Paid', payment_date = CURDATE() " +
                                   "WHERE meter_number = ? AND month = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, meterNumber);
                updateStmt.setString(2, month);
                
                int rowsAffected = updateStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get customer and bill details for receipt
                    String customerQuery = "SELECT name FROM customer WHERE meter_number = ?";
                    PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
                    customerStmt.setString(1, meterNumber);
                    ResultSet customerRs = customerStmt.executeQuery();
                    
                    String billQuery = "SELECT * FROM bill WHERE meter_number = ? AND month = ?";
                    PreparedStatement billStmt = conn.prepareStatement(billQuery);
                    billStmt.setString(1, meterNumber);
                    billStmt.setString(2, month);
                    ResultSet billRs = billStmt.executeQuery();
                    
                    if (customerRs.next() && billRs.next()) {
                        // Generate receipt
                        StringBuilder receipt = new StringBuilder();
                        receipt.append(String.format("%50s\n", "PAYMENT RECEIPT"));
                        receipt.append(String.format("%50s\n\n", "=============================="));
                        
                        receipt.append(String.format("%-20s: %s\n", "Customer Name", customerRs.getString("name")));
                        receipt.append(String.format("%-20s: %s\n", "Meter Number", meterNumber));
                        receipt.append(String.format("%-20s: %s\n", "Payment Date", new Date(rowsAffected, rowsAffected, rowsAffected).toString()));
                        receipt.append(String.format("%-20s: %s\n\n", "Billing Month", month));
                        
                        receipt.append(String.format("%-30s₹%10.2f\n\n", "Amount Paid:", billRs.getDouble("amount")));
                        receipt.append(String.format("%50s\n", "=============================="));
                        receipt.append(String.format("%50s\n", "Payment Successful!"));
                        receipt.append(String.format("%50s\n", "Thank you for your payment!"));
                        
                        receiptArea.setText(receipt.toString());
                        JOptionPane.showMessageDialog(this, "Payment successful!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Payment failed. Please try again.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
        }
    }
}
