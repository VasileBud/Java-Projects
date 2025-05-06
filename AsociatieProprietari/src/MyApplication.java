import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends JFrame {

    private Connection connection;
    private JPanel panelLogin;
    private JTabbedPane tabbedPane;
    private String dbUsername;
    private String dbPassword;

    public MyApplication() {
        this.setTitle("My Application - Asociatie Proprietari");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);

        setupLoginPanel();
        this.setContentPane(panelLogin);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDatabaseConnection();
            }
        });

        this.setVisible(true);
    }

    private void setupLoginPanel() {
        panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setBackground(Color.decode("#F5F5F5"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Asociatie Proprietari", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelLogin.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panelLogin.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panelLogin.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panelLogin.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panelLogin.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelLogin.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            dbUsername = usernameField.getText();
            dbPassword = new String(passwordField.getPassword());

            if (openDatabaseConnection()) {
                setupMainPanel();
                setContentPane(tabbedPane);
                invalidate();
                validate();
            }
        });
    }

    private boolean openDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/asociatieproprietari";
            connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void closeDatabaseConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                System.err.println("Eroare la inchiderea conexiunii: " + ex.getMessage());
            }
        }
    }

    private void setupMainPanel() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        JPanel dashboardTab = new JPanel(new BorderLayout());

        JPanel sqlPanel = new JPanel(new BorderLayout());
        JTextArea sqlTextArea = new JTextArea(3, 50);
        JScrollPane sqlScrollPane = new JScrollPane(sqlTextArea);
        sqlScrollPane.setBorder(BorderFactory.createTitledBorder("Introduceti comanda SQL"));
        sqlPanel.add(sqlScrollPane, BorderLayout.NORTH);

        JButton executeButton = new JButton("Executa");
        executeButton.setPreferredSize(new Dimension(150, 30));
        sqlPanel.add(executeButton, BorderLayout.SOUTH);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        dashboardTab.add(sqlPanel, BorderLayout.NORTH);
        dashboardTab.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String sqlQuery = sqlTextArea.getText().trim();
            if (sqlQuery.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduceti o comanda SQL.", "Avertizare", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlQuery)) {

                DefaultTableModel model = new DefaultTableModel();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnName(i));
                }

                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = resultSet.getObject(i);
                    }
                    model.addRow(row);
                }

                resultsTable.setModel(model);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea comenzii SQL: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        setupMainTab();
        tabbedPane.addTab("Executa cod SQL", dashboardTab);
        setupDatabaseTab();
        setupQueryTabs();

        this.setContentPane(tabbedPane);
        this.revalidate();
        this.repaint();
    }

    private void setupDatabaseTab() {
        JPanel dbTab = new JPanel(new BorderLayout());
        JPanel sideMenu = new JPanel(new GridLayout(0, 1, 10, 10));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sideMenu.setBackground(Color.decode("#F0F4F8"));

        JTabbedPane tableTabbedPane = new JTabbedPane();

        List<String> tableNames = getTableNames();
        for (String tableName : tableNames) {

            JButton tableButton = new JButton(tableName);
            sideMenu.add(tableButton);

            JPanel tablePanel = new JPanel(new BorderLayout());
            JTable table = loadTableData(tableName);
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            tableTabbedPane.addTab(tableName, tablePanel);

            tableButton.addActionListener(e -> tableTabbedPane.setSelectedIndex(tableTabbedPane.indexOfTab(tableName)));
        }

        dbTab.add(sideMenu, BorderLayout.WEST);
        dbTab.add(tableTabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("Baza de date", dbTab);
    }

    private void setupQueryTabs() {
        setupQueryTab1();
        setupQueryTab2();
        setupQueryTab3();
        setupQueryTab4();
        setupQueryTab5();
        setupQueryTab6();
        setupQueryTab7();
        setupQueryTab8();
    }

    private void setupQueryTab1() {
        JPanel queryTab1 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gasire Proprietar"));


        JPanel parametersPanel = new JPanel(new BorderLayout());
        JLabel labelInput = new JLabel("Introduceti literele sau numele complet:");
        JTextField textFieldInput = new JTextField();

        parametersPanel.add(labelInput, BorderLayout.WEST);
        parametersPanel.add(textFieldInput, BorderLayout.CENTER);

        inputPanel.add(parametersPanel, BorderLayout.CENTER);

        JButton executeButton = new JButton("Cauta Proprietar");
        executeButton.setVerticalTextPosition(SwingConstants.TOP);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        inputPanel.add(executeButton, BorderLayout.SOUTH);
        queryTab1.add(inputPanel, BorderLayout.NORTH);
        queryTab1.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String input = textFieldInput.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduceti literele sau numele complet.", "Avertizare", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] inputs = input.split(" ");
            String letter1 = "%" + inputs[0] + "%";
            String letter2 = inputs.length > 1 ? "%" + inputs[1] + "%" : "%";

            String sqlQuery;
            if (inputs.length > 1) {
                sqlQuery = "SELECT * FROM Proprietar WHERE nume LIKE ? OR nume LIKE ? ORDER BY nume DESC, prenume ASC";
            } else {
                sqlQuery = "SELECT * FROM Proprietar WHERE nume LIKE ? ORDER BY nume DESC, prenume ASC";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, letter1);
                if (inputs.length > 1) {
                    preparedStatement.setString(2, letter2);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Gasire Proprietar", queryTab1);
    }

    private void setupQueryTab2() {
        JPanel queryTab2 = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Gasire chitanta din anul selectat"));
        JLabel yearLabel = new JLabel("Anul:");
        JComboBox<String> yearComboBox = new JComboBox<>(new String[]{"2021", "2022", "2023", "2024", "2025"});
        JButton executeButton = new JButton("Afiseaza");

        yearLabel.setPreferredSize(new Dimension(50, 25));
        yearComboBox.setPreferredSize(new Dimension(100, 25));
        executeButton.setPreferredSize(new Dimension(150, 30));

        topPanel.add(yearLabel);
        topPanel.add(yearComboBox);
        topPanel.add(executeButton);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        queryTab2.add(topPanel, BorderLayout.NORTH);
        queryTab2.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String yearInput = (String) yearComboBox.getSelectedItem();
            if (yearInput == null || yearInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selectati anul pentru cautare.", "Avertizare", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlQuery = "SELECT * FROM Chitanta WHERE YEAR(data) = ? ORDER BY valoare DESC";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, yearInput);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Afișare Chitanțe", queryTab2);
    }

    private void setupQueryTab3() {
        JPanel queryTab3 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Luna
        JLabel labelLuna = new JLabel("Luna:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        inputPanel.add(labelLuna, gbc);

        JComboBox<Integer> comboLuna = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            comboLuna.addItem(i);
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(comboLuna, gbc);

        JLabel labelAn = new JLabel("Anul:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        inputPanel.add(labelAn, gbc);

        JComboBox<Integer> comboAn = new JComboBox<>();
        for (int i = 2020; i <= 2030; i++) {
            comboAn.addItem(i);
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(comboAn, gbc);

        JLabel labelAdresa = new JLabel("Adresa:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        inputPanel.add(labelAdresa, gbc);

        JTextField addressField = new JTextField("Cluj-Napoca, Strada Observatorului, Nr. 9", 25);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(addressField, gbc);

        JButton executeButton = new JButton("Afiseaza Consum");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(executeButton, gbc);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogării"));

        queryTab3.add(inputPanel, BorderLayout.NORTH);
        queryTab3.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            int luna = (Integer) comboLuna.getSelectedItem();
            int an = (Integer) comboAn.getSelectedItem();
            String adresa = addressField.getText().trim();

            if (adresa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduceti o adresa valida.", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sqlQuery = "SELECT a.nr_ap, GetProprietarByAp(a.id_ap) AS proprietar, c.valoare " +
                    "FROM Apartament a " +
                    "JOIN Consum c ON a.id_ap = c.id_ap " +
                    "WHERE c.luna = ? AND c.an = ? AND a.adresa = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, luna);
                preparedStatement.setInt(2, an);
                preparedStatement.setString(3, adresa);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Consum Apartamente", queryTab3);
    }

    private void setupQueryTab4() {
        JPanel queryTab4 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Selectati anul si luna pentru consum (optional)"));


        JLabel labelLuna = new JLabel("Luna:");
        JComboBox<String> comboLuna = new JComboBox<>();
        comboLuna.addItem("Toate");
        for (int i = 1; i <= 12; i++) {
            comboLuna.addItem(String.valueOf(i));
        }

        JLabel labelAn = new JLabel("Anul:");
        JComboBox<String> comboAn = new JComboBox<>();
        comboAn.addItem("Toti");
        for (int i = 2020; i <= 2030; i++) {
            comboAn.addItem(String.valueOf(i));
        }

        inputPanel.add(labelLuna);
        inputPanel.add(comboLuna);
        inputPanel.add(labelAn);
        inputPanel.add(comboAn);

        JButton executeButton = new JButton("Gaseste Apartamente");

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Perechi de apartamente cu acelasi consum"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(executeButton, BorderLayout.SOUTH);

        queryTab4.add(topPanel, BorderLayout.NORTH);
        queryTab4.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String selectedLuna = (String) comboLuna.getSelectedItem();
            String selectedAn = (String) comboAn.getSelectedItem();

            boolean hasLuna = !selectedLuna.equals("Toate");
            boolean hasAn = !selectedAn.equals("Toti");

            String sqlQuery = "SELECT c1.id_ap AS id_ap1, c2.id_ap AS id_ap2 " +
                    "FROM Consum c1 " +
                    "JOIN Consum c2 ON c1.cantitate = c2.cantitate " +
                    "AND c1.an = c2.an AND c1.luna = c2.luna " +
                    "WHERE c1.id_ap < c2.id_ap";

            if (hasLuna) sqlQuery += " AND c1.luna = ?";
            if (hasAn) sqlQuery += " AND c1.an = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                int paramIndex = 1;
                if (hasLuna) preparedStatement.setInt(paramIndex++, Integer.parseInt(selectedLuna));
                if (hasAn) preparedStatement.setInt(paramIndex++, Integer.parseInt(selectedAn));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    model.addColumn("ID Apartament 1");
                    model.addColumn("ID Apartament 2");

                    while (resultSet.next()) {
                        model.addRow(new Object[]{
                                resultSet.getInt("id_ap1"),
                                resultSet.getInt("id_ap2")
                        });
                    }

                    resultsTable.setModel(model);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Apartamente Consum Identic", queryTab4);
    }

    private void setupQueryTab5() {
        JPanel queryTab5 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;

        JLabel labelLuna = new JLabel("Luna:");
        inputPanel.add(labelLuna, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JComboBox<Integer> comboLuna = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            comboLuna.addItem(i);
        }
        inputPanel.add(comboLuna, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        JLabel labelAn = new JLabel("Anul:");
        inputPanel.add(labelAn, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JComboBox<Integer> comboAn = new JComboBox<>();
        for (int i = 2020; i <= 2030; i++) {
            comboAn.addItem(i);
        }
        inputPanel.add(comboAn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JButton executeButton = new JButton("Afisează Consum Maxim");
        executeButton.setPreferredSize(new Dimension(200, 40));
        inputPanel.add(executeButton, gbc);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        queryTab5.add(inputPanel, BorderLayout.NORTH);
        queryTab5.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            int luna = (Integer) comboLuna.getSelectedItem();
            int an = (Integer) comboAn.getSelectedItem();

            String sqlQuery = "CALL GetMaxConsum(?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, an);
                preparedStatement.setInt(2, luna);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Consum Maxim", queryTab5);
    }

    private void setupQueryTab6() {
        JPanel queryTab6 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;

        JLabel labelIdAp = new JLabel("ID Apartament:");
        inputPanel.add(labelIdAp, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JTextField textIdAp = new JTextField("1", 10);
        inputPanel.add(textIdAp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        JLabel labelLuna = new JLabel("Luna:");
        inputPanel.add(labelLuna, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JComboBox<Integer> comboLuna = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            comboLuna.addItem(i);
        }
        comboLuna.setSelectedItem(3);
        inputPanel.add(comboLuna, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        JLabel labelAn = new JLabel("Anul:");
        inputPanel.add(labelAn, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        JComboBox<Integer> comboAn = new JComboBox<>();
        for (int i = 2020; i <= 2030; i++) {
            comboAn.addItem(i);
        }
        comboAn.setSelectedItem(2024);
        inputPanel.add(comboAn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JButton executeButton = new JButton("Afiseaza Proprietari");
        executeButton.setPreferredSize(new Dimension(200, 40));
        inputPanel.add(executeButton, gbc);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        queryTab6.add(inputPanel, BorderLayout.NORTH);
        queryTab6.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String idAp = textIdAp.getText().trim();
            int luna = (Integer) comboLuna.getSelectedItem();
            int an = (Integer) comboAn.getSelectedItem();

            if (idAp.isEmpty() || !idAp.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Introduceti un ID valid de apartament.", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idApartament = Integer.parseInt(idAp);

            String sqlQuery = "SELECT DISTINCT p.nume, p.prenume " +
                    "FROM Proprietar p " +
                    "JOIN Apartament a ON p.id_proprietar = a.id_proprietar " +
                    "JOIN Consum c ON a.id_ap = c.id_ap " +
                    "WHERE c.valoare = ( " +
                    "   SELECT c1.valoare FROM Consum c1 " +
                    "   WHERE c1.id_ap = ? AND c1.an = ? AND c1.luna = ? " +
                    ") " +
                    "AND c.an = ? AND c.luna = ? " +
                    "AND c.id_ap != ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, idApartament);
                preparedStatement.setInt(2, an);
                preparedStatement.setInt(3, luna);
                preparedStatement.setInt(4, an);
                preparedStatement.setInt(5, luna);
                preparedStatement.setInt(6, idApartament);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Proprietari Consum Identic", queryTab6);
    }

    private void setupQueryTab7() {
        JPanel queryTab7 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Filtrare Incasari Trimestriale", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel labelTrimestru = new JLabel("Selectati Trimestrul:");
        JComboBox<String> comboTrimestru = new JComboBox<>(new String[]{"Toate", "1", "2", "3", "4"});
        inputPanel.add(labelTrimestru, gbc);
        gbc.gridx = 1;
        inputPanel.add(comboTrimestru, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel labelIdAp = new JLabel("Introduceti ID Apartament (optional):");
        JTextField textIdAp = new JTextField(10);
        inputPanel.add(labelIdAp, gbc);
        gbc.gridx = 1;
        inputPanel.add(textIdAp, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton executeButton = new JButton("Afiseaza Incasari Trimestriale");
        executeButton.setPreferredSize(new Dimension(300, 30));

        inputPanel.add(executeButton, gbc);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        queryTab7.add(inputPanel, BorderLayout.NORTH);
        queryTab7.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String selectedTrimestru = (String) comboTrimestru.getSelectedItem();
            String idApText = textIdAp.getText().trim();

            boolean filterByTrimestru = !selectedTrimestru.equals("Toate");
            boolean filterByIdAp = !idApText.isEmpty();

            String sqlQuery = "SELECT QUARTER(data) AS trimestru, id_ap, " +
                    "MIN(valoare) AS minim, AVG(valoare) AS mediu, MAX(valoare) AS maxim " +
                    "FROM Chitanta ";

            if (filterByTrimestru || filterByIdAp) {
                sqlQuery += "WHERE ";
                if (filterByTrimestru) {
                    sqlQuery += "QUARTER(data) = ? ";
                    if (filterByIdAp) sqlQuery += "AND ";
                }
                if (filterByIdAp) {
                    sqlQuery += "id_ap = ? ";
                }
            }

            sqlQuery += "GROUP BY trimestru, id_ap";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                int paramIndex = 1;
                if (filterByTrimestru) {
                    preparedStatement.setInt(paramIndex++, Integer.parseInt(selectedTrimestru));
                }
                if (filterByIdAp) {
                    preparedStatement.setInt(paramIndex, Integer.parseInt(idApText));
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la pregatirea interogarii: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Incasari Trimestriale", queryTab7);
    }

    private void setupQueryTab8() {
        JPanel queryTab8 = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;

        JLabel labelAdresa = new JLabel("Adresa:");
        JTextField textFieldAdresa = new JTextField("Turda, Strada Baladei, Nr. 3");

        JLabel labelData = new JLabel("Data (YYYY-MM-DD):");
        JTextField textFieldData = new JTextField("2024-10-01");

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(labelAdresa, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(textFieldAdresa, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(labelData, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(textFieldData, gbc);

        JButton executeButton = new JButton("Afiseaza Restantieri");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(executeButton, gbc);

        JTable resultsTable = new JTable();
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Rezultatele interogarii"));

        queryTab8.add(inputPanel, BorderLayout.NORTH);
        queryTab8.add(resultsScrollPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> {
            String adresa = textFieldAdresa.getText().trim();
            String data = textFieldData.getText().trim();

            if (adresa.isEmpty() || data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduceti o adresa si o data valida.", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sqlQuery = """
                        SELECT DISTINCT p.nume, p.prenume
                        FROM Proprietar p
                        JOIN Apartament a ON p.id_proprietar = a.id_proprietar
                        JOIN Consum c ON a.id_ap = c.id_ap
                        LEFT JOIN Chitanta ch ON c.id_ap = ch.id_ap AND ch.data <= ?
                        WHERE a.adresa = ?
                          AND c.an * 12 + c.luna < YEAR(?) * 12 + MONTH(?)
                          AND IFNULL((SELECT SUM(ch1.valoare)
                                      FROM Chitanta ch1
                                      WHERE ch1.id_ap = c.id_ap AND ch1.data <= ?), 0) < 
                              (SELECT SUM(c1.valoare)
                               FROM Consum c1
                               WHERE c1.id_ap = c.id_ap AND c1.an * 12 + c1.luna < YEAR(?) * 12 + MONTH(?));
                    """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, adresa);
                preparedStatement.setString(3, data);
                preparedStatement.setString(4, data);
                preparedStatement.setString(5, data);
                preparedStatement.setString(6, data);
                preparedStatement.setString(7, data);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        model.addColumn(metaData.getColumnName(i));
                    }

                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = resultSet.getObject(i);
                        }
                        model.addRow(row);
                    }

                    resultsTable.setModel(model);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Eroare la executarea interogării: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Restantieri", queryTab8);
    }

    private void setupMainTab() {
        JPanel mainTab = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel titleMain = new JLabel("Asociatie Proprietari", SwingConstants.CENTER);
        titleMain.setFont(new Font("Copperplate Gothic Bold", Font.BOLD, 26));
        gbc.gridwidth = 2;
        mainTab.add(titleMain, gbc);

        gbc.gridy++;
        JLabel titleLabel = new JLabel("Meniu Principal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        mainTab.add(titleLabel, gbc);

        gbc.gridy++;
        JButton btnExecutaCodSql = new JButton("Executa cod Sql");
        btnExecutaCodSql.setPreferredSize(new Dimension(250, 40));
        mainTab.add(btnExecutaCodSql, gbc);

        gbc.gridy++;
        JButton btnVizualizareBazaDate = new JButton("Vizualizare Baza de Date");
        btnVizualizareBazaDate.setPreferredSize(new Dimension(250, 40));
        mainTab.add(btnVizualizareBazaDate, gbc);

        gbc.gridy++;
        JButton btnExecutaInterogari = new JButton("Executa Interogari");
        btnExecutaInterogari.setPreferredSize(new Dimension(250, 40));
        mainTab.add(btnExecutaInterogari, gbc);

        gbc.gridy++;
        JButton btnIesire = new JButton("Iesire");
        btnIesire.setPreferredSize(new Dimension(250, 40));
        mainTab.add(btnIesire, gbc);
        
        btnExecutaCodSql.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        btnVizualizareBazaDate.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        btnExecutaInterogari.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        btnIesire.addActionListener(e -> System.exit(0));

        tabbedPane.addTab("Meniu Principal", mainTab);
    }

    private List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {
            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la obtinerea numelor tabelelor: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
        return tableNames;
    }

    private JTable loadTableData(String tableName) {
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Eroare la incarcarea datelor din tabel: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
        return table;
    }

    public static void main(String[] args) {
        new MyApplication();
    }
}
