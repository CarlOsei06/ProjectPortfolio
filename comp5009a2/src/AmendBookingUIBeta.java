import javax.swing.*;

public class AmendBookingUIBeta {

    public static void main(String[] args) {

        // Create a sample booking to test AmendBookingPanel in isolation
        Booking sample = new Booking(
                "BR999",
                "TestUser",
                "2026-05-01T10:00",
                "Standard",
                2,
                "2026-05-25T08:30",
                20.0,
                10.0,
                false
        );

        // Launch the AmendBookingPanel on the EDT
        SwingUtilities.invokeLater(() -> {

            // Create a temporary frame for testing
            JFrame frame = new JFrame("Test Amend Booking Panel");

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(500, 350);

            // Use null because this test runs independently
            MainApp dummyMainApp = null;

            // Add the panel to the frame
            frame.add(
                    new AmendBookingPanel(
                            dummyMainApp,
                            sample
                    )
            );

            // Centre window
            frame.setLocationRelativeTo(null);

            // Display window
            frame.setVisible(true);
        });
    }
}