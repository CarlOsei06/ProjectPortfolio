import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class BookingPaymentPanel extends JPanel {

    private MainApp mainApp;
    private Booking currentBooking;

    private JPanel searchPanel;
    private JPanel bookingInfoPanel;
    private JPanel paymentPanel;
    private JPanel loyaltyContainer;
    private LoyaltyDiscountPanel loyaltyDiscountPanel;

    private JLabel bookingInfoLabel;

    private JTextField emailField;
    private JTextField cardNumberField;
    private JTextField csvField;
    private JTextField expiryField;

    private JComboBox<String> paymentMethodBox;
    private JCheckBox emailConfirmationBox;
    private JCheckBox saveDetailsBox;

    private Booking pendingBooking;

    private boolean paymentCompleted = false;

    private JButton payBtn;

    public BookingPaymentPanel(MainApp mainApp, String bookingRef, String surname) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Pay for Booking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ================= SEARCH PANEL =================
        searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(createBorder("Find Booking"));

        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(8, 8, 8, 8);

        JTextField bookingField = new JTextField(20);
        JTextField surnameField = new JTextField(20);

        bookingField.setText(bookingRef == null ? "" : bookingRef);
        surnameField.setText(surname == null ? "" : surname);

        JButton getBookingBtn = new JButton("Get Booking");

        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(new JLabel("Booking ID:"), searchGbc);

        searchGbc.gridx = 1;
        searchGbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(bookingField, searchGbc);

        searchGbc.gridx = 0;
        searchGbc.gridy = 1;
        searchGbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(new JLabel("Surname:"), searchGbc);

        searchGbc.gridx = 1;
        searchGbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(surnameField, searchGbc);

        searchGbc.gridx = 0;
        searchGbc.gridy = 2;
        searchGbc.gridwidth = 2;
        searchGbc.anchor = GridBagConstraints.CENTER;
        searchPanel.add(getBookingBtn, searchGbc);

        // ================= BOOKING INFO PANEL =================
        bookingInfoPanel = new JPanel(new BorderLayout());
        bookingInfoPanel.setBorder(createBorder("Booking Information"));
        bookingInfoLabel = new JLabel("No booking selected.");
        bookingInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bookingInfoPanel.add(bookingInfoLabel, BorderLayout.CENTER);
        bookingInfoPanel.setVisible(false);

        // ================= PAYMENT PANEL =================
        paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(createBorder("Payment Details"));
        paymentPanel.setVisible(false);

        GridBagConstraints payGbc = new GridBagConstraints();
        payGbc.insets = new Insets(8, 8, 8, 8);

        paymentMethodBox = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "PayPal", "Stripe"});
        cardNumberField = new JTextField(18);
        csvField = new JTextField(5);
        expiryField = new JTextField(7);
        emailField = new JTextField(20);

        emailConfirmationBox = new JCheckBox("Send email confirmation", true);
        saveDetailsBox = new JCheckBox("Save payment details for next time");

        payGbc.gridx = 0;
        payGbc.gridy = 0;
        payGbc.anchor = GridBagConstraints.EAST;
        paymentPanel.add(new JLabel("Payment Method:"), payGbc);

        payGbc.gridx = 1;
        payGbc.anchor = GridBagConstraints.WEST;
        paymentPanel.add(paymentMethodBox, payGbc);

        payGbc.gridx = 0;
        payGbc.gridy = 1;
        payGbc.anchor = GridBagConstraints.EAST;
        paymentPanel.add(new JLabel("Email:"), payGbc);

        payGbc.gridx = 1;
        payGbc.anchor = GridBagConstraints.WEST;
        paymentPanel.add(emailField, payGbc);

        JPanel cardLinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cardLinePanel.add(new JLabel("Card Number:"));
        cardLinePanel.add(cardNumberField);
        cardLinePanel.add(new JLabel("CSV:"));
        cardLinePanel.add(csvField);
        cardLinePanel.add(new JLabel("Expiry:"));
        cardLinePanel.add(expiryField);

        payGbc.gridx = 0;
        payGbc.gridy = 2;
        payGbc.gridwidth = 2;
        payGbc.anchor = GridBagConstraints.CENTER;
        paymentPanel.add(cardLinePanel, payGbc);

        payGbc.gridy = 3;
        paymentPanel.add(emailConfirmationBox, payGbc);

        payGbc.gridy = 4;
        paymentPanel.add(saveDetailsBox, payGbc);

        payBtn = new JButton("Pay Now");
        payBtn.setPreferredSize(new Dimension(150, 35));

        payGbc.gridy = 5;
        paymentPanel.add(payBtn, payGbc);

        // ================= ADD TO MAIN =================

        loyaltyContainer = new JPanel(new BorderLayout());
        loyaltyContainer.setVisible(false);

        gbc.gridy = 0;
        mainPanel.add(searchPanel, gbc);

        gbc.gridy = 1;
        mainPanel.add(bookingInfoPanel, gbc);

        gbc.gridy = 2;
        mainPanel.add(loyaltyContainer, gbc);

        gbc.gridy = 3;
        mainPanel.add(paymentPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back to Menu");

        backButton.addActionListener(e -> {

            if (!paymentCompleted && pendingBooking != null) {

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to leave?\n" +
                        "This booking has not been paid for and will not be saved.",
                        "Leave Payment?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }

                BookingManager.deleteBooking(pendingBooking.bookingRef);
            }

            mainApp.showPage("menu");
        });
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ================= GET BOOKING ACTION =================
        getBookingBtn.addActionListener(e -> {
            String bookingID = bookingField.getText().trim();
            String enteredSurname = surnameField.getText().trim();

            if (bookingID.isEmpty() || enteredSurname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Booking ID and Surname.");
                return;
            }

            Booking booking = BookingManager.getBooking(bookingID, enteredSurname);

            if (booking == null) {
                JOptionPane.showMessageDialog(this, "No booking found.");
                return;
            }

            if (BookingManager.getStatus(bookingID).equals("CANCELLED")) {
                JOptionPane.showMessageDialog(this, "This booking is cancelled and cannot be paid.");
                return;
            }

            if (BookingManager.hasBookingPassed(bookingID)) {
                JOptionPane.showMessageDialog(this, "This booking has already passed and cannot be paid.");
                return;
            }

            currentBooking = booking;

            pendingBooking = booking;

            searchPanel.setVisible(false);
            bookingInfoPanel.setVisible(true);
            paymentPanel.setVisible(true);

            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime placement = LocalDateTime.parse(booking.placementDateTime);
            LocalDateTime bookingTime = LocalDateTime.parse(booking.bookingDateTime);

            bookingInfoLabel.setText(
                "<html>" +
                    "<b>Booking:</b> " + booking.bookingRef + "<br>" +
                    "<b>Surname:</b> " + booking.surname + "<br>" +
                    "<b>Placement Date:</b> " + placement.format(format) + "<br>" +
                    "<b>Booking Date:</b> " + bookingTime.format(format) + "<br>" +
                    "<b>Vehicle:</b> " + booking.vehicleType + "<br>" +
                    "<b>Luggage:</b> " + booking.luggageCount + "<br>" +
                    "<b>Total Cost:</b> £" + String.format("%.2f", booking.cost) + "<br><br>" +
                    "<font color='green'><b>READY FOR PAYMENT</b></font>" +
                "</html>"
            );

            loyaltyContainer.removeAll();
            loyaltyDiscountPanel = new LoyaltyDiscountPanel(booking.surname, booking.cost);
            loyaltyContainer.add(loyaltyDiscountPanel, BorderLayout.CENTER);
            loyaltyContainer.setVisible(true);



            loadSavedPaymentDetails(booking.bookingRef);

            revalidate();
            repaint();
        });

        // ================= PAY ACTION =================
        payBtn.addActionListener(e -> {
            if (currentBooking == null) {
                JOptionPane.showMessageDialog(this, "Please load a booking first.");
                return;
            }

            String email = emailField.getText().trim();
            String paymentMethod = paymentMethodBox.getSelectedItem().toString();
            String cardNumber = cardNumberField.getText().trim();
            String csv = csvField.getText().trim();
            String expiry = expiryField.getText().trim();


            // This checks the amount of digits for the card, the CVS and the expiry code
            // as well as making sure the date for the expiry code is a valid date

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required.");
                return;
            }
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
                return;
            }
            if (!cardNumber.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Card number must be exactly 16 digits.");
                return;
            }
            if (!csv.matches("\\d{3}")) {
                JOptionPane.showMessageDialog(this, "CSV must be exactly 3 digits.");
                return;
            }
            if (!expiry.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Expiration date must be in MM/YY format."
                );
                return;
            }

            String[] expiryParts = expiry.split("/");
            int expiryMonth = Integer.parseInt(expiryParts[0]);
            int expiryYear = Integer.parseInt(expiryParts[1]);

            LocalDateTime now = LocalDateTime.now();

            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear() % 100; // last 2 digits

            if (expiryYear < currentYear ||
                (expiryYear == currentYear && expiryMonth < currentMonth)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Card has expired."
                );
                return;
            }

            PaymentManager.savePayment(
                currentBooking.bookingRef,
                email,
                paymentMethod,
                cardNumber,
                csv,
                expiry,
                emailConfirmationBox.isSelected(),
                saveDetailsBox.isSelected()
            );

            Booking existing = BookingManager.getBooking(
                    currentBooking.bookingRef,
                    currentBooking.surname
            );

            if (existing == null) {
                BookingManager.addExistingBooking(currentBooking);
            }

            paymentCompleted = true;

            double finalAmountPaid = loyaltyDiscountPanel.getFinalCalculatedPrice();
            

            String message =
                "Payment Successful!\n\n" +
                "Booking Reference: " + currentBooking.bookingRef + "\n" +
                "Amount Paid: £" + String.format("%.2f", finalAmountPaid) + "\n" +
                "Payment Method: " + paymentMethod;

            if (emailConfirmationBox.isSelected()) {
                message += "\n\nEmail confirmation sent to: " + email;
            }

            JOptionPane.showMessageDialog(
                this,
                message,
                "Payment Confirmation",
                JOptionPane.INFORMATION_MESSAGE
            );
            paymentCompleted = true;
            mainApp.showPage("confirmation");
        });

        if (bookingRef != null && !bookingRef.isEmpty()) {
            getBookingBtn.doClick();
        }
    }

    private void loadSavedPaymentDetails(String bookingRef) {
        String[] saved = PaymentManager.getSavedPayment(bookingRef);

        if (saved == null) {
            return;
        }

        emailField.setText(saved[1]);
        paymentMethodBox.setSelectedItem(saved[2]);
        cardNumberField.setText(saved[3]);
        csvField.setText(saved[4]);
        expiryField.setText(saved[5]);
        emailConfirmationBox.setSelected(Boolean.parseBoolean(saved[6]));
        saveDetailsBox.setSelected(Boolean.parseBoolean(saved[7]));
    }

    private TitledBorder createBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP
        );
    }
}