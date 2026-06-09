import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class BookingPanel extends JPanel {

    private MainApp mainApp;

    public BookingPanel(MainApp mainApp, String vehicleText, String luggageText, double distance) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        JLabel title = new JLabel("New Booking");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        // Added wrapper panel to keep booking panels centred
        JPanel centerWrapper = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(350, 220));
        leftPanel.setBorder(createBorder("Booking Date & Traveller"));

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.insets = new Insets(8, 8, 8, 8);

        JTextField surnameField = new JTextField(15);

        JComboBox<String> day = new JComboBox<>();
        JComboBox<String> month = new JComboBox<>();
        JComboBox<String> year = new JComboBox<>();
        JComboBox<String> hour = new JComboBox<>();
        JComboBox<String> minute = new JComboBox<>();

        for (int i = 1; i <= 31; i++) day.addItem(String.format("%02d", i));
        for (int i = 1; i <= 12; i++) month.addItem(String.format("%02d", i));
        for (int i = 2026; i <= 2030; i++) year.addItem(String.valueOf(i));
        for (int i = 0; i < 24; i++) hour.addItem(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minute.addItem(String.format("%02d", i));

        JPanel datePanel = new JPanel();
        datePanel.add(day);
        datePanel.add(new JLabel("/"));
        datePanel.add(month);
        datePanel.add(new JLabel("/"));
        datePanel.add(year);

        JPanel timePanel = new JPanel();
        timePanel.add(hour);
        timePanel.add(new JLabel(":"));
        timePanel.add(minute);

        leftGbc.gridx = 0; leftGbc.gridy = 0;
        leftPanel.add(new JLabel("Surname:"), leftGbc);
        leftGbc.gridx = 1;
        leftPanel.add(surnameField, leftGbc);

        leftGbc.gridx = 0; leftGbc.gridy = 1;
        leftPanel.add(new JLabel("Date:"), leftGbc);
        leftGbc.gridx = 1;
        leftPanel.add(datePanel, leftGbc);

        leftGbc.gridx = 0; leftGbc.gridy = 2;
        leftPanel.add(new JLabel("Time:"), leftGbc);
        leftGbc.gridx = 1;
        leftPanel.add(timePanel, leftGbc);

        // Added booking summary panel connected to Browse Services
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(350, 220));
        rightPanel.setBorder(createBorder("Selected Service"));

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(8, 8, 8, 8);

        JLabel vehicleLabel = new JLabel("Vehicle Type: " + vehicleText);
        JLabel luggageLabel = new JLabel("Luggage: " + luggageText);
        JLabel distanceLabel = new JLabel("Distance: " + distance + " km");
        JButton editButton = new JButton("Edit Booking");

        rightGbc.gridy = 0;
        rightPanel.add(vehicleLabel, rightGbc);
        rightGbc.gridy = 1;
        rightPanel.add(luggageLabel, rightGbc);
        rightGbc.gridy = 2;
        rightPanel.add(distanceLabel, rightGbc);
        rightGbc.gridy = 3;
        rightPanel.add(editButton, rightGbc);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        centerWrapper.add(mainPanel, BorderLayout.CENTER);

        // Added cost beside payment button
        JPanel actionPanel = new JPanel();

        JLabel costLabel = new JLabel();
        costLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        final double[] currentCost = new double[1];

        Runnable updateCost = () -> {
            int selectedHour = Integer.parseInt(hour.getSelectedItem().toString());

            String timeOfDay;
            //  Map to tariff periods (Morning: 6-11, Afternoon: 12-17, Evening: all other hours)
            switch (selectedHour) {
                case 6, 7, 8, 9, 10, 11 ->
                        timeOfDay = "Morning";
                case 12, 13, 14, 15, 16, 17 ->
                        timeOfDay = "Afternoon";
                default ->
                        timeOfDay = "Evening";
            }

            currentCost[0] = TariffCalculator.calculateCost(
                    vehicleText,
                    luggageText,
                    distance,
                    timeOfDay
            );

            costLabel.setText("Cost: £" + String.format("%.2f", currentCost[0]));
        };

        hour.addActionListener(e -> updateCost.run());
        minute.addActionListener(e -> updateCost.run());

        updateCost.run();

        JButton paymentButton = new JButton("Proceed to Payment");

        actionPanel.add(costLabel);
        actionPanel.add(paymentButton);

        centerWrapper.add(actionPanel, BorderLayout.SOUTH);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Menu");
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        editButton.addActionListener(e -> mainApp.showPage("browse"));
        backButton.addActionListener(e -> mainApp.showPage("menu"));

        // Saves booking and redirects user directly to payment
        paymentButton.addActionListener(e -> {
            try {
                String surname = surnameField.getText().trim();

                if (surname.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter surname.");
                    return;
                }

                String bookingDateTime =
                        year.getSelectedItem() + "-" +
                        month.getSelectedItem() + "-" +
                        day.getSelectedItem() + "T" +
                        hour.getSelectedItem() + ":" +
                        minute.getSelectedItem();

                LocalDateTime.parse(
                        bookingDateTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                );
                // Parse luggage string format
                int luggageCount = luggageText.startsWith("3+") ? 3 :
                        Integer.parseInt(luggageText.substring(0, 1));

                String bookingRef = BookingManager.addBooking(
                        surname,
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm")),
                        vehicleText,
                        luggageCount,
                        bookingDateTime,
                        currentCost[0]
                );

                mainApp.showPaymentPage(bookingRef, surname);

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date selected.");
            }
        });
    }

    private TitledBorder createBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title
        );
    }
}