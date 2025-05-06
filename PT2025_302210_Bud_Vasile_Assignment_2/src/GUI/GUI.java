package GUI;

import BusinessLogic.Scheduler;
import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    private JTextField txtClients = new JTextField(5);
    private JTextField txtServers = new JTextField(5);
    private JTextField txtSimTime = new JTextField(5);
    private JTextField txtMinArrival = new JTextField(5);
    private JTextField txtMaxArrival = new JTextField(5);
    private JTextField txtMinService = new JTextField(5);
    private JTextField txtMaxService = new JTextField(5);
    private JButton btnRun = new JButton("Run Simulation");
    private JButton btnStop = new JButton("Stop Simulation");
    private JButton btnChangeStrategy = new JButton("Change Strategy");
    private JLabel lblCurrentStrategy = new JLabel("Current strategy:");

    private JTextPane logPane = new JTextPane();
    private StyledDocument logDoc;

    private JButton btnTest1 = new JButton("Test 1");
    private JButton btnTest2 = new JButton("Test 2");
    private JButton btnTest3 = new JButton("Test 3");

    private SelectionPolicy currentPolicy = SelectionPolicy.SHORTEST_QUEUE;

    private SimulationManager simulationManager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public GUI() {
        setTitle("Queue Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setOpaque(false);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 2, 2));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        inputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Simulation Input"), BorderFactory.createEmptyBorder(5, 150, 5, 150)));
        inputPanel.setBackground(new Color(250, 250, 255));

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("👥 Number of Clients:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtClients, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("🔧 Number of Servers:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtServers, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("⏱️ Simulation Time:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtSimTime, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("🚶 Min Arrival Time:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtMinArrival, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("🏃 Max Arrival Time:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtMaxArrival, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel("📥 Min Service Time:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtMinService, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(new JLabel("📤 Max Service Time:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtMaxService, gbc);

        txtClients.setFont(defaultFont);
        txtServers.setFont(defaultFont);
        txtSimTime.setFont(defaultFont);
        txtMinArrival.setFont(defaultFont);
        txtMaxArrival.setFont(defaultFont);
        txtMinService.setFont(defaultFont);
        txtMaxService.setFont(defaultFont);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.setBorder(BorderFactory.createLineBorder(new Color(29, 52, 85), 2));
        buttonsPanel.setBackground(new Color(250, 250, 255));
        buttonsPanel.add(btnRun);
        buttonsPanel.add(btnStop);
        buttonsPanel.add(btnChangeStrategy);

        lblCurrentStrategy.setText("Current strategy: " + currentPolicy.toString());
        lblCurrentStrategy.setFont(defaultFont);
        buttonsPanel.add(lblCurrentStrategy);

        btnRun.setBackground(new Color(29, 52, 85));
        btnRun.setForeground(Color.WHITE);
        btnRun.setFont(defaultFont);
        btnStop.setBackground(new Color(29, 52, 85));
        btnStop.setForeground(Color.WHITE);
        btnStop.setFont(defaultFont);
        btnChangeStrategy.setBackground(new Color(29, 52, 85));
        btnChangeStrategy.setForeground(Color.WHITE);
        btnChangeStrategy.setFont(defaultFont);

        topPanel.add(inputPanel);
        topPanel.add(buttonsPanel);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Simulation Log"));
        logPanel.setBackground(Color.WHITE);
        logDoc = logPane.getStyledDocument();
        logPane.setEditable(false);
        logPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(logPane);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel testPanel = new JPanel(new FlowLayout());
        testPanel.setBorder(BorderFactory.createTitledBorder("Predefined Tests"));
        testPanel.setBackground(new Color(250, 250, 255));
        testPanel.setBorder(BorderFactory.createLineBorder(new Color(29, 52, 85), 2));
        testPanel.add(btnTest1);
        testPanel.add(btnTest2);
        testPanel.add(btnTest3);

        btnTest1.setFont(defaultFont);
        btnTest2.setFont(defaultFont);
        btnTest3.setFont(defaultFont);

        btnTest1.setBackground(new Color(29, 52, 85));
        btnTest1.setForeground(Color.WHITE);
        btnTest2.setBackground(new Color(29, 52, 85));
        btnTest2.setForeground(Color.WHITE);
        btnTest3.setBackground(new Color(29, 52, 85));
        btnTest3.setForeground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(logPanel, BorderLayout.CENTER);
        add(testPanel, BorderLayout.SOUTH);

        btnChangeStrategy.addActionListener(e -> toggleStrategy());
        btnRun.addActionListener(e -> runSimulation());
        btnStop.addActionListener(e -> stopSimulation());
        btnTest1.addActionListener(e -> loadTest(4, 2, 60, 2, 30, 2, 4));
        btnTest2.addActionListener(e -> loadTest(50, 5, 60, 2, 40, 1, 7));
        btnTest3.addActionListener(e -> loadTest(1000, 20, 200, 10, 100, 3, 9));

        setVisible(true);
    }

    public void toggleStrategy() {
        if (currentPolicy == SelectionPolicy.SHORTEST_QUEUE) {
            currentPolicy = SelectionPolicy.SHORTEST_TIME;
        } else {
            currentPolicy = SelectionPolicy.SHORTEST_QUEUE;
        }
        lblCurrentStrategy.setText("Current strategy: " + currentPolicy);
        if (simulationManager != null) {
            simulationManager.getScheduler().changeStrategy(currentPolicy);
        }
    }

    public void runSimulation() {
        if (txtClients.getText().isEmpty() || txtServers.getText().isEmpty() || txtSimTime.getText().isEmpty() || txtMinArrival.getText().isEmpty() || txtMaxArrival.getText().isEmpty() || txtMinService.getText().isEmpty() || txtMaxService.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields before starting the simulation.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int n = Integer.parseInt(txtClients.getText());
        int q = Integer.parseInt(txtServers.getText());
        int simTime = Integer.parseInt(txtSimTime.getText());
        int minArrival = Integer.parseInt(txtMinArrival.getText());
        int maxArrival = Integer.parseInt(txtMaxArrival.getText());
        int minService = Integer.parseInt(txtMinService.getText());
        int maxService = Integer.parseInt(txtMaxService.getText());

        simulationManager = new SimulationManager(simTime, maxArrival, minArrival, maxService, minService, q, n, currentPolicy, this);
        executor.submit(simulationManager);
    }

    public void stopSimulation() {
        if (simulationManager != null) {
            Scheduler s = simulationManager.getScheduler();
            s.shutdown();
        }
        executor.shutdownNow();
        executor = Executors.newSingleThreadExecutor();
        updateLog("Simulation stopped.");
    }

    public void loadTest(int n, int q, int simTime, int minArr, int maxArr, int minServ, int maxServ) {
        txtClients.setText(String.valueOf(n));
        txtServers.setText(String.valueOf(q));
        txtSimTime.setText(String.valueOf(simTime));
        txtMinArrival.setText(String.valueOf(minArr));
        txtMaxArrival.setText(String.valueOf(maxArr));
        txtMinService.setText(String.valueOf(minServ));
        txtMaxService.setText(String.valueOf(maxServ));
    }

    public void updateLog(String content) {
        SwingUtilities.invokeLater(() -> {
            try {
                logDoc.remove(0, logDoc.getLength());
                String[] lines = content.split("\n");

                for (String line : lines) {
                    if (line.startsWith("Queue") && line.contains("processing")) {
                        int idx = line.indexOf(":");
                        String prefix = line.substring(0, idx + 1);
                        String rest = line.substring(idx + 1);

                        Style prefixStyle = logPane.addStyle("queuePrefix", null);
                        StyleConstants.setFontSize(prefixStyle, 14);
                        StyleConstants.setFontFamily(prefixStyle, "Consolas");
                        StyleConstants.setBold(prefixStyle, true);
                        StyleConstants.setForeground(prefixStyle, new Color(29, 52, 85));

                        logDoc.insertString(logDoc.getLength(), "▶ " + prefix, prefixStyle);

                        int pIndex = rest.indexOf("processing");
                        if (pIndex >= 0) {
                            String before = rest.substring(0, pIndex);
                            String processing = rest.substring(pIndex, rest.indexOf(")", pIndex) + 1);
                            String after = rest.substring(rest.indexOf(")", pIndex) + 1);

                            if (!before.isBlank()) {
                                Style normalTaskStyle = logPane.addStyle("queueTasks", null);
                                StyleConstants.setFontSize(normalTaskStyle, 14);
                                StyleConstants.setFontFamily(normalTaskStyle, "Consolas");
                                StyleConstants.setForeground(normalTaskStyle, new Color(29, 52, 85));
                                logDoc.insertString(logDoc.getLength(), before, normalTaskStyle);
                            }
                            Style processingStyle = logPane.addStyle("processing", null);
                            StyleConstants.setFontSize(processingStyle, 14);
                            StyleConstants.setFontFamily(processingStyle, "Consolas");
                            StyleConstants.setBold(processingStyle, true);
                            StyleConstants.setForeground(processingStyle, new Color(255, 179, 0, 255)); // galben
                            logDoc.insertString(logDoc.getLength(), processing, processingStyle);

                            if (!after.isBlank()) {
                                Style afterStyle = logPane.addStyle("after", null);
                                StyleConstants.setFontSize(afterStyle, 14);
                                StyleConstants.setFontFamily(afterStyle, "Consolas");
                                StyleConstants.setForeground(afterStyle, new Color(29, 52, 85));
                                logDoc.insertString(logDoc.getLength(), after, afterStyle);
                            }
                        }
                        logDoc.insertString(logDoc.getLength(), "\n", null);
                    } else {
                        Style style = logPane.addStyle("default", null);
                        StyleConstants.setFontSize(style, 14);
                        StyleConstants.setFontFamily(style, "Consolas");
                        StyleConstants.setBold(style, false);

                        if (line.startsWith("Time")) {
                            line = "⏰ " + line;
                            StyleConstants.setForeground(style, new Color(30, 136, 229));
                            StyleConstants.setBold(style, true);
                        } else if (line.contains("closed")) {
                            line = "❌ " + line;
                            StyleConstants.setForeground(style, new Color(29, 52, 85));
                            StyleConstants.setItalic(style, true);
                        } else {
                            StyleConstants.setForeground(style, new Color(0, 0, 0, 255));
                        }
                        logDoc.insertString(logDoc.getLength(), line + "\n", style);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GUI();
    }

}