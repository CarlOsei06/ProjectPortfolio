import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BrowseServicesUI extends JPanel {

    // Constructor
    public BrowseServicesUI(MainApp main) {
        setLayout(new BorderLayout(10, 10));

        // Added title to match other panels
        JPanel topPanel = new JPanel();
        JLabel title = new JLabel("Browse Services & Tariffs");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(createBorder("Tariff Calculator"));

        // Components
        // vehicle selection
        JLabel vehicleLabel = new JLabel("Vehicle Type:");
        String[] vehicles = {"Standard", "Executive", "Minivan"};
        JComboBox<String> vehicleBox = new JComboBox<>(vehicles);

        // selection for the amount of bags the user has
        JLabel luggageLabel = new JLabel("Luggage Amount:");
        String[] luggageOptions = {"0 bags", "1 bag", "2 bags", "3+ bags"};
        JComboBox<String> luggageBox = new JComboBox<>(luggageOptions);

        // allows you to add the distance to the airport
        JLabel distanceLabel = new JLabel("Distance to Airport (km):");
        JTextField distanceField = new JTextField();

        // Removed time of day selection because it is chosen on the booking page
        // Replaced calculate button with automatic price update
        JLabel resultLabel = new JLabel("From: £0.00");

        // Added continue button to connect browse page to booking page
        JButton continueButton = new JButton("Continue to Booking");

        // Add components to panel in order of how they are used
        panel.add(vehicleLabel);
        panel.add(vehicleBox);

        panel.add(luggageLabel);
        panel.add(luggageBox);

        panel.add(distanceLabel);
        panel.add(distanceField);

        panel.add(new JLabel(""));
        panel.add(resultLabel);

        // Added wrapper to centre the form like other panels
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        mainPanel.add(panel, gbc);

        gbc.gridy = 1;
        mainPanel.add(continueButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Added a back button to return to the main menu and reset fields after calculation
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> main.showPage("menu"));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        Runnable updatePrice = () -> {
            try {
                double distance = Double.parseDouble(distanceField.getText().trim());
                String vehicle = (String) vehicleBox.getSelectedItem();
                String luggage = (String) luggageBox.getSelectedItem();

                // Uses Morning as the lowest tariff to display "From" price
                double cost = calculateCost(vehicle, luggage, distance, "Morning");
                resultLabel.setText("From: £" + String.format("%.2f", cost));

            } catch (Exception ex) {
                resultLabel.setText("From: £0.00");
            }
        };

        vehicleBox.addActionListener(e -> updatePrice.run());
        luggageBox.addActionListener(e -> updatePrice.run());

        distanceField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updatePrice.run();
            }

            public void removeUpdate(DocumentEvent e) {
                updatePrice.run();
            }

            public void changedUpdate(DocumentEvent e) {
                updatePrice.run();
            }
        });

        continueButton.addActionListener(e -> {
            try {
                double distance = Double.parseDouble(distanceField.getText().trim());
                String vehicle = (String) vehicleBox.getSelectedItem();
                String luggage = (String) luggageBox.getSelectedItem();

                main.showBookingPage(vehicle, luggage, distance);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid distance before continuing.");
            }
        });
    }

    // Wrapper method for UI
    double calculateCost(String vehicle, String luggage, double distance, String time) {
        return TariffCalculator.calculateCost(vehicle, luggage, distance, time);
    }

    // Added border helper to match other panels
    private TitledBorder createBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title
        );
    }
}