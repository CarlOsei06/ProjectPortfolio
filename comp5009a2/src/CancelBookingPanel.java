import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class CancelBookingPanel extends JPanel {

    private MainApp mainApp;

    private JLabel resultLabel;
    private JButton cancelBtn;

    public CancelBookingPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));

        // ================= TOP =================
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel title = new JLabel("Cancel Booking");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ================= FORM PANEL =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        "Booking Details",
                        TitledBorder.LEFT,
                        TitledBorder.TOP
                )
        );

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 10, 10, 10);

        JTextField bookingField = new JTextField(15);
        JTextField surnameField = new JTextField(15);

        // Booking ID row
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Booking ID:"), formGbc);

        formGbc.gridx = 1;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(bookingField, formGbc);

        // Surname row
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Surname:"), formGbc);

        formGbc.gridx = 1;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(surnameField, formGbc);

        // Get Booking button
        JButton getBtn = new JButton("Get Booking");

        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(getBtn, formGbc);

        // ================= RESULT PANEL =================
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        "Booking Information",
                        TitledBorder.LEFT,
                        TitledBorder.TOP
                )
        );

        // Result label defaults to "No booking selected." and will be updated when a booking is retrieved
        resultLabel = new JLabel("No booking selected.");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        resultPanel.add(resultLabel, BorderLayout.CENTER);

        // ================= CANCEL BUTTON =================
        cancelBtn = new JButton("Cancel Booking");
        cancelBtn.setVisible(false);

        // ================= ADD PANELS TO MAIN =================
        gbc.gridy = 0;
        mainPanel.add(formPanel, gbc);

        gbc.gridy = 1;
        mainPanel.add(resultPanel, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(cancelBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // ================= GET BOOKING =================
        getBtn.addActionListener(e -> {

            String bookingID = bookingField.getText().trim();
            String surname = surnameField.getText().trim();

            // Check if fields are empty
            if (bookingID.isEmpty() || surname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            // If fields are filled, attempt to retrieve booking
            Booking b = BookingManager.getBooking(bookingID, surname);

            // If no booking is found, show message and hide cancel button
            if (b == null) {
                resultLabel.setText("No booking found.");
                cancelBtn.setVisible(false);
                return;
            }

            // If booking is found, display details and show cancel button if booking is active
            String status = BookingManager.getStatus(bookingID);

            if (status.equals("CANCELLED")) {
                status = "CANCELLED"; // Show as cancelled if booking is cancelled
            } else if (BookingManager.hasBookingPassed(bookingID)) {
                status = "COMPLETED"; // Show as completed if booking date has passed, even if cancelled
            } else {
                status = "ACTIVE"; // Show as active if booking is upcoming and not cancelled
            }

            // Set status colour: green for active, red for cancelled, blue for completed
            String statusColour = status.equals("ACTIVE") ? "green" : "red";
            statusColour = status.equals("COMPLETED") ? "blue" : statusColour;

            // Format dates and calculate time until booking
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime placement = LocalDateTime.parse(b.placementDateTime);
            LocalDateTime booking = LocalDateTime.parse(b.bookingDateTime);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(now, booking);

            boolean past = duration.isNegative(); // Check if booking date is in the past

            if (past) {
                duration = duration.abs(); // Get absolute value of duration if booking date is in the past so the number is positive
            }

            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;

            // Display time until booking in format "Xh Ym until booking" or "Booking passed Xh Ym ago" if booking date is in the past
            String timeUntil = past
                    ? "Booking passed " + hours + "h " + minutes + "m ago"
                    : hours + "h " + minutes + "m until booking";

            // Set result label text with booking details and status
            resultLabel.setText(
                "<html>" +
                        "<b>Booking:</b> " + b.bookingRef + "<br>" +
                        "<b>Surname:</b> " + b.surname + "<br>" +
                        "<b>Placement Date:</b> " + placement.format(dateFormat) + "<br>" +
                        "<b>Booking Date:</b> " + booking.format(dateFormat) + "<br>" +
                        "<b>Time Remaining:</b> " + timeUntil + "<br>" +
                        "<b>Vehicle:</b> " + b.vehicleType + "<br>" +
                        "<b>Luggage:</b> " + b.luggageCount + "<br>" +
                        "<b>Cost:</b> £" + String.format("%.2f", b.cost) + "<br><br>" +
                        "<font color='" + statusColour + "'><b>" + status + "</b></font>" +
                "</html>"
        );

            // Only show cancel button if booking is active (upcoming and not cancelled)
            cancelBtn.setVisible(status.equals("ACTIVE"));
        });

        // ================= CANCEL BOOKING =================
        cancelBtn.addActionListener(e -> {
            String id = bookingField.getText().trim(), name = surnameField.getText().trim();
            if (id.isEmpty() || name.isEmpty()) { JOptionPane.showMessageDialog(this, "Please fill in all fields."); return; }
            double fee = BookingManager.getCancellationFee(id);
            if (JOptionPane.showConfirmDialog(this, fee > 0 ? "Under36h: fee £" + String.format("%.2f", fee) + ". Continue?" : "Continue to cancel?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                mainApp.showCancelAmendPaymentPage(id, name, fee, CancelAmendPaymentPanel.ActionType.CANCEL, null);
        });

        // ================= BOTTOM =================
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back to Menu");

        backButton.addActionListener(e -> mainApp.showPage("menu"));

        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}