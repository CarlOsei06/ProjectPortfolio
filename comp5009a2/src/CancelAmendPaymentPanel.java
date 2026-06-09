import java.awt.*;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class CancelAmendPaymentPanel extends JPanel {

    public enum ActionType { CANCEL, AMEND }

    public CancelAmendPaymentPanel(MainApp mainApp,
                                   String bookingID,
                                   String surname,
                                   double cost,
                                   ActionType actionType,
                                   Booking updatedBooking) {

        setLayout(new BorderLayout(10, 10));

        String pageTitle = actionType == ActionType.CANCEL
                ? "Cancel Booking Payment"
                : "Amend Booking Payment";

        JLabel title = new JLabel(pageTitle, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ================= BOOKING INFO PANEL =================
        JPanel bookingInfoPanel = new JPanel(new BorderLayout());
        bookingInfoPanel.setBorder(createBorder("Booking Information"));

        JLabel bookingInfoLabel = new JLabel(
                "<html>" +
                        "<b>Booking:</b> " + bookingID + "<br>" +
                        "<b>Surname:</b> " + surname + "<br>" +
                        "<b>Action:</b> " + actionType + "<br>" +
                        "<b>Amount Due:</b> £" + String.format("%.2f", cost) + "<br><br>" +
                        "<font color='green'><b>READY FOR PAYMENT</b></font>" +
                "</html>"
        );

        bookingInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bookingInfoPanel.add(bookingInfoLabel, BorderLayout.CENTER);

        // ================= PAYMENT PANEL =================
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(createBorder("Payment Details"));

        GridBagConstraints payGbc = new GridBagConstraints();
        payGbc.insets = new Insets(8, 8, 8, 8);

        JComboBox<String> paymentMethodBox =
                new JComboBox<>(new String[]{"Credit Card", "Debit Card", "PayPal", "Stripe"});

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField cardField = new JTextField(18);
        JTextField cvvField = new JTextField(5);
        JTextField expiryField = new JTextField(7);

        JCheckBox emailConfirmationBox = new JCheckBox("Send email confirmation", true);
        JCheckBox saveDetailsBox = new JCheckBox("Save payment details for next time", true);

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
        paymentPanel.add(new JLabel("Name:"), payGbc);

        payGbc.gridx = 1;
        payGbc.anchor = GridBagConstraints.WEST;
        paymentPanel.add(nameField, payGbc);

        payGbc.gridx = 0;
        payGbc.gridy = 2;
        payGbc.anchor = GridBagConstraints.EAST;
        paymentPanel.add(new JLabel("Email:"), payGbc);

        payGbc.gridx = 1;
        payGbc.anchor = GridBagConstraints.WEST;
        paymentPanel.add(emailField, payGbc);

        JPanel cardLinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cardLinePanel.add(new JLabel("Card Number:"));
        cardLinePanel.add(cardField);
        cardLinePanel.add(new JLabel("CVV:"));
        cardLinePanel.add(cvvField);
        cardLinePanel.add(new JLabel("Expiry:"));
        cardLinePanel.add(expiryField);

        payGbc.gridx = 0;
        payGbc.gridy = 3;
        payGbc.gridwidth = 2;
        payGbc.anchor = GridBagConstraints.CENTER;
        paymentPanel.add(cardLinePanel, payGbc);

        payGbc.gridy = 4;
        paymentPanel.add(emailConfirmationBox, payGbc);

        payGbc.gridy = 5;
        paymentPanel.add(saveDetailsBox, payGbc);

        JButton confirmBtn = new JButton("Confirm Payment");
        confirmBtn.setPreferredSize(new Dimension(150, 35));

        payGbc.gridy = 6;
        paymentPanel.add(confirmBtn, payGbc);

        // ================= ADD TO MAIN =================
        gbc.gridy = 0;
        mainPanel.add(bookingInfoPanel, gbc);

        gbc.gridy = 1;
        mainPanel.add(paymentPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM =================
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton backBtn = new JButton("Back to Menu");
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // ================= LOAD SAVED PAYMENT =================
        String[] saved = PaymentManager.getSavedPayment(bookingID);
        if (saved != null) {
            emailField.setText(saved[1]);
            paymentMethodBox.setSelectedItem(saved[2]);
            cardField.setText(saved[3]);
            cvvField.setText(saved[4]);
            expiryField.setText(saved[5]);

            if (saved.length > 6) {
                emailConfirmationBox.setSelected(Boolean.parseBoolean(saved[6]));
            }

            if (saved.length > 7) {
                saveDetailsBox.setSelected(Boolean.parseBoolean(saved[7]));
            }

            cardField.setEditable(false);
            cvvField.setEditable(false);
            expiryField.setEditable(false);
        }

        backBtn.addActionListener(e -> mainApp.showPage("menu"));

        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String paymentMethod = paymentMethodBox.getSelectedItem().toString();
            String card = cardField.getText().trim();
            String cvv = cvvField.getText().trim();
            String expiry = expiryField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || card.isEmpty() || cvv.isEmpty() || expiry.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Enter a valid email.");
                return;
            }

            if (!card.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Card must be 16 digits.");
                return;
            }

            if (!cvv.matches("\\d{3,4}")) {
                JOptionPane.showMessageDialog(this, "CVV must be 3 or 4 digits.");
                return;
            }

            if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Expiry must be MM/YY.");
                return;
            }

            String[] expiryParts = expiry.split("/");
            int expiryMonth = Integer.parseInt(expiryParts[0]);
            int expiryYear = Integer.parseInt(expiryParts[1]);

            LocalDateTime now = LocalDateTime.now();

            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear() % 100;

            if (expiryYear < currentYear ||
                    (expiryYear == currentYear && expiryMonth < currentMonth)) {

                JOptionPane.showMessageDialog(this, "Card has expired.");
                return;
            }

            try {
                PaymentManager.savePayment(
                        bookingID,
                        email,
                        paymentMethod,
                        card,
                        cvv,
                        expiry,
                        emailConfirmationBox.isSelected(),
                        saveDetailsBox.isSelected()
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not save payment details.");
            }

            if (actionType == ActionType.CANCEL) {
                String result = BookingManager.cancelBooking(bookingID, surname);

                switch (result) {
                    case "success":
                        JOptionPane.showMessageDialog(
                                this,
                                "Payment Successful!\n\n" +
                                        "Booking cancelled: " + bookingID + "\n" +
                                        "Amount Paid: £" + String.format("%.2f", cost) + "\n" +
                                        "Payment Method: " + paymentMethod,
                                "Payment Confirmation",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        mainApp.showPage("menu");
                        break;

                    case "not found":
                        JOptionPane.showMessageDialog(this, "Booking not found.");
                        break;

                    case "already cancelled":
                        JOptionPane.showMessageDialog(this, "Already cancelled.");
                        break;

                    default:
                        JOptionPane.showMessageDialog(this, "Error cancelling booking.");
                }
            } else {
                if (updatedBooking == null) {
                    JOptionPane.showMessageDialog(this, "No updated booking data.");
                    return;
                }

                boolean ok = BookingManager.updateBooking(updatedBooking);

                if (ok) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Payment Successful!\n\n" +
                                    "Booking updated: " + updatedBooking.getBookingRef() + "\n" +
                                    "Amount Paid: £" + String.format("%.2f", cost) + "\n" +
                                    "Payment Method: " + paymentMethod,
                            "Payment Confirmation",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    mainApp.showPage("menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update booking.");
                }
            }
        });
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