import java.awt.*;
import javax.swing.*;


public class LoyaltyDiscountPanel extends JPanel {

    private String surname;
    private double basePrice;

    // ui

    private JLabel discountLabel;
    private JLabel ridesLabel;
    private JLabel finalPriceLabel;

    public LoyaltyDiscountPanel(String surname, double basePrice) {
        this.surname = surname;
        this.basePrice = basePrice;

        setupUI();
        updateDiscountDisplay();
    }

    public void setupUI() {
        setLayout(new GridLayout(3, 1, 5, 5));

        ridesLabel = new JLabel("Calculating past rides...");
        discountLabel = new JLabel("Calculating discount...");
        finalPriceLabel = new JLabel("Calculating final price...");

        finalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(ridesLabel);
        add(discountLabel);
        add(finalPriceLabel);
    }

    private void updateDiscountDisplay() {
        int pastRides = BookingManager.getPastRideCountForUser(surname);
        double discountMultiplier = TariffCalculator.getLoyaltyDiscountMultiplier(pastRides);
        int discountPercentage = (int) (discountMultiplier * 100);
        double finalPrice = TariffCalculator.calculateDiscountedPrice(basePrice, pastRides);

        ridesLabel.setText("Eligible Past Rides: " + pastRides);

        if (discountPercentage > 0) {
            discountLabel.setText("Loyalty Discount: " + discountPercentage + "%");
        } else {
            discountLabel.setText("No Loyalty Discount");
            discountLabel.setForeground(Color.RED);
        }

        finalPriceLabel.setText("Final Price: £" + String.format("%.2f", finalPrice));

    }

    public double getFinalCalculatedPrice() {
        int pastRides = BookingManager.getPastRideCountForUser(surname);
        return TariffCalculator.calculateDiscountedPrice(basePrice, pastRides);
    }
}

