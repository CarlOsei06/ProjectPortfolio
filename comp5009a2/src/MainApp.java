
import java.awt.*;
import javax.swing.*;

public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    public MainApp() {
        setTitle("Booking System");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        container.add(new MenuPanel(this), "menu");
        container.add(new CancelBookingPanel(this), "cancel");
        container.add(new BookingConfirmationPanel(this), "confirmation");
        container.add(new BookingPanel(this, "Standard", "0 bags", 0.0), "booking");
        container.add(new BrowseServicesUI(this), "browse");
        container.add(new BookingPaymentPanel(this, null, null), "payment");


        add(container);
        setVisible(true);
    }

    public void showPage(String name) {
        cardLayout.show(container, name);
    }

    public void showPaymentPage(String bookingRef, String surname) {
        BookingPaymentPanel paymentPanel = new BookingPaymentPanel(this, bookingRef, surname);
        container.add(paymentPanel, "payment");
        cardLayout.show(container, "payment");
    }

    // Show the Cancel/Amend Payment UI for cancellations and amendments
    public void showCancelAmendPaymentPage(String bookingID,
                                           String surname,
                                           double cost,
                                           CancelAmendPaymentPanel.ActionType actionType,
                                           Booking updatedBooking) {

        CancelAmendPaymentPanel panel = new CancelAmendPaymentPanel(this, bookingID, surname, cost, actionType, updatedBooking);
        container.add(panel, "cancelAmendPayment");
        cardLayout.show(container, "cancelAmendPayment");
    }

    public void showBookingPage(String vehicle, String luggage, double distance) {
        BookingPanel bookingPanel = new BookingPanel(this, vehicle, luggage, distance);
        container.add(bookingPanel, "booking");
        cardLayout.show(container, "booking");
    }

    // Show the AmendBooking UI for a specific booking
    public void showAmendPage(Booking booking) {
        // AmendBookingPanel is the panel implementation present in the project
        AmendBookingPanel amendPanel = new AmendBookingPanel(this, booking);
        container.add(amendPanel, "amend");
        cardLayout.show(container, "amend");
    }

    public static void main(String[] args) {
        new MainApp();
    }
}