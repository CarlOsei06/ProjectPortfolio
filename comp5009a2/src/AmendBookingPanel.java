import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;

public class AmendBookingPanel extends JPanel {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    private MainApp mainApp;

    public AmendBookingPanel(MainApp mainApp, Booking booking) {

        this.mainApp = mainApp;

        // If no booking exists return user to menu
        if (booking == null) {

            JOptionPane.showMessageDialog(this, "No booking selected.");

            SwingUtilities.invokeLater(() -> { mainApp.showPage("menu"); });

            return;
        }

        setLayout(new GridLayout(8, 2, 10, 10));

        // creates a dropdown to select the new vehicle type
        // which will replace the current vehicle type
        JLabel vehicleLabel = new JLabel("Vehicle Type:");

        JComboBox<String> vehicleBox =
                new JComboBox<>(new String[]{
                        "Standard",
                        "Executive",
                        "Minivan"
                });

        vehicleBox.setSelectedItem(
                booking.getVehicleType()
        );

        // creates a dropdown box to select a new amount of luggage
        // which will replace the current amount of luggage
        JLabel luggageLabel = new JLabel("Luggage:");

        JComboBox<String> luggageBox =
                new JComboBox<>(new String[]{ "0", "1", "2", "3" });

        luggageBox.setSelectedItem( String.valueOf(booking.getLuggageCount() ) );

        //  Changed the date label field to a dropdown for better UX
        // allows the user to add a new booking time
        // which will replace the current booking time
        JLabel dateLabel =
                new JLabel("Booking DateTime:");

        LocalDateTime currentDateTime = LocalDateTime.parse( booking.getBookingDateTime(), FORMATTER);

        //  Create dropdowns for date and time selection
        JComboBox<String> dayBox = new JComboBox<>();
        JComboBox<String> monthBox = new JComboBox<>();
        JComboBox<String> yearBox = new JComboBox<>();
        JComboBox<String> hourBox = new JComboBox<>();
        JComboBox<String> minuteBox = new JComboBox<>();

        //  Enter values for dropdowns
        for (int i = 1; i <= 31; i++) { dayBox.addItem(String.format("%02d", i)); }
        for (int i = 1; i <= 12; i++) { monthBox.addItem(String.format("%02d", i)); }
        for (int i = 2026; i <= 2030; i++) { yearBox.addItem(String.valueOf(i)); }
        for (int i = 0; i < 24; i++) { hourBox.addItem(String.format("%02d", i)); }
        for (int i = 0; i < 60; i += 5) { minuteBox.addItem(String.format("%02d", i)); }

        // Get values from inputted date/time
        dayBox.setSelectedItem( String.format("%02d", currentDateTime.getDayOfMonth()) );
        monthBox.setSelectedItem( String.format("%02d", currentDateTime.getMonthValue()));
        yearBox.setSelectedItem( String.valueOf(currentDateTime.getYear()));
        hourBox.setSelectedItem( String.format("%02d", currentDateTime.getHour()));
        minuteBox.setSelectedItem( String.format("%02d", currentDateTime.getMinute()));


        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));

        dateTimePanel.add(dayBox);
        dateTimePanel.add(new JLabel("/"));
        dateTimePanel.add(monthBox);
        dateTimePanel.add(new JLabel("/"));
        dateTimePanel.add(yearBox);

        // gap
        dateTimePanel.add(Box.createHorizontalStrut(15));

        dateTimePanel.add(hourBox);
        dateTimePanel.add(new JLabel(":"));
        dateTimePanel.add(minuteBox);

        // allow the user to add the new distance
        // that replaces the currently saved distance
        JLabel distanceLabel = new JLabel("Distance (km):");
        JTextField distanceField = new JTextField( String.valueOf( booking.getDistance() ) );

        // shows original price
        JLabel costLabel = new JLabel( "Original Cost: £" + booking.getCost() );

        // save and back button
        JButton saveButton = new JButton("Save Changes");
        JButton backButton = new JButton("Back");

        // These are all the different add components that you can when amending the booking
        // This includes a dropdown box for the Date and time panel for easier accessibility
        add(vehicleLabel);
        add(vehicleBox);
        add(luggageLabel);
        add(luggageBox);
        add(dateLabel);
        add(dateTimePanel);
        add(distanceLabel);
        add(distanceField);
        add(costLabel);
        add(new JLabel());
        add(saveButton);
        add(backButton);

        // Save Button logic
        saveButton.addActionListener(e -> {
            try {

                // Parse distance
                double distanceVal = Double.parseDouble( distanceField.getText().trim());
                // Parse new date/time
                String newDateTimeText =
                        yearBox.getSelectedItem() + "-" +
                                monthBox.getSelectedItem() + "-" +
                                dayBox.getSelectedItem() + "T" +
                                hourBox.getSelectedItem() + ":" +
                                minuteBox.getSelectedItem();

                LocalDateTime newDT = LocalDateTime.parse( newDateTimeText, FORMATTER );

                String newBookingDateTime = newDT.format( FORMATTER );

                // This Maps the booking times to the tariff period
                //periods: Night (before 6am), Morning (6-11), Afternoon (12-4pm), Evening (5-9pm), Night (10pm+)
                int hour = newDT.getHour();
                String timeOfDay =
                        (hour < 6) ? "Night" :
                                (hour < 12) ? "Morning" :
                                        (hour < 17) ? "Afternoon" :
                                                (hour < 22) ? "Evening" :
                                                        "Night";

                // Capacity check
                String vehicleType = (String)vehicleBox.getSelectedItem();
                int capacity = vehicleType.equals("Minivan") ? 6 : 4;

                List<Booking> targetBookings = BookingManager.getBookingsByDateAndVehicle( newBookingDateTime, vehicleType );

                // Exclude this booking if already in target list
                boolean alreadyInTarget = targetBookings.stream().anyMatch(b -> b.getBookingRef().equals(booking.getBookingRef()));
                int count = targetBookings.size() - (alreadyInTarget ? 1 : 0);
                if (count + 1 > capacity) {
                    JOptionPane.showMessageDialog( this, "Cannot amend — vehicle at capacity for that time." );
                    return;
                }

                String luggageString = luggageBox.getSelectedItem() + " bags";
                double newCost = TariffCalculator.calculateCost( vehicleType, luggageString, distanceVal, timeOfDay);
                double difference = Math.max( 0, newCost - booking.getCost());

                String confirmMessage;

                if (difference > 0) {
                confirmMessage =
                        "This amendment will require an additional payment of £" +
                        String.format("%.2f", difference) +
                        ".\n\nDo you want to continue?";
                } else {
                confirmMessage =
                        "This amendment does not require an additional payment.\n\n" +
                        "Do you want to continue?";
                }

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        confirmMessage,
                        "Confirm Amendment",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice != JOptionPane.YES_OPTION) {
                return;
                }

                booking.setVehicleType(vehicleType);
                booking.setLuggageCount(Integer.parseInt((String) luggageBox.getSelectedItem()));
                booking.setBookingDateTime(newBookingDateTime);
                booking.setCost(newCost);

                mainApp.showCancelAmendPaymentPage(
                        booking.getBookingRef(),
                        booking.getSurname(),
                        difference,
                        CancelAmendPaymentPanel.ActionType.AMEND,
                        booking
                );

                return;

            }

            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog( this,  "Please enter a valid number for distance." );
            }

            catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog( this, "Date/Time invalid" );
            }

            catch (Exception ex) {
                JOptionPane.showMessageDialog( this, "Invalid input." );
            }

        });

        backButton.addActionListener(
                e -> mainApp.showPage("menu")
        );
    }
}