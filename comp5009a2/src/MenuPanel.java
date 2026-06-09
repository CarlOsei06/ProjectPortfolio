
import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    public MenuPanel(MainApp main) {

        setLayout(new FlowLayout());

        JLabel title = new JLabel("Main Menu");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton cancelBtn = new JButton("Cancel Booking");
        JButton confirmBtn = new JButton("Confirmation"); // Confirmation page allows drivers to upload their ID and view booking details after confirming a booking, this page shouldnt be visible from the menu but is included here for testing purposes
        JButton bookingBtn = new JButton("New Booking");
        JButton browseBtn = new JButton("Browse Services");
        JButton paymentBtn = new JButton("Payment");
        JButton amendBtn = new JButton("Amend Booking");

        cancelBtn.addActionListener(e -> main.showPage("cancel"));
        confirmBtn.addActionListener(e -> main.showPage("confirmation"));
        bookingBtn.addActionListener(e -> main.showPage("booking"));
        browseBtn.addActionListener(e -> main.showPage("browse"));
        paymentBtn.addActionListener(e-> main.showPage("payment"));
        amendBtn.addActionListener(e -> openAmendBookingLookup(main));


        add(title);
        add(cancelBtn);
        add(confirmBtn);
        //add(bookingBtn);
        add(browseBtn);
       // add(paymentBtn);
        add(amendBtn);

    }

    // Open a dialog to search for a booking by ref and surname, then show amend UI
    private void openAmendBookingLookup(MainApp main) {
        JPanel searchPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel refLabel = new JLabel("Booking Ref:");
        JTextField refField = new JTextField();

        JLabel surnameLabel = new JLabel("Surname:");
        JTextField surnameField = new JTextField();

        searchPanel.add(refLabel);
        searchPanel.add(refField);
        searchPanel.add(surnameLabel);
        searchPanel.add(surnameField);

        int result = JOptionPane.showConfirmDialog(
                this,
                searchPanel,
                "Search Booking",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String ref = refField.getText().trim();
            String surname = surnameField.getText().trim();

            if (ref.isEmpty() || surname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both booking ref and surname.");
                return;
            }

            // Look up the booking
            Booking booking = BookingManager.getBooking(ref, surname);

            if (booking == null) {
                JOptionPane.showMessageDialog(this, "Booking not found.");
                return;
            }

            // Show the amend UI with the booking
            main.showAmendPage(booking);
        }
    }
}