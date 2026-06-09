import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;

public class BookingPanelTest {

    @BeforeEach
    public void setUp() throws IOException {
        try (FileWriter writer = new FileWriter("bookings.csv", false)) {
            writer.write("bookingRef,surname,placementDateTime,vehicleType,luggageCount,bookingDateTime,cost,isCancelled\n");
        }
    }

    @Test
    public void testAddBooking() {
        String ref = BookingManager.addBooking(
                "Smith",
                "2026-05-20T10:00",
                "Standard",
                2,
                "2026-05-21T08:00",
                21.0
        );

        assertNotNull(ref);
        assertTrue(ref.startsWith("BR"));
    }

    @Test
    public void testStandardCost() {
        double cost = TariffCalculator.calculateCost("Standard", "2 bags", 10.0, "Morning");
        assertEquals(21.0, cost, 0.01);
    }

    @Test
    public void testExecutiveCost() {
        double cost = TariffCalculator.calculateCost("Executive", "0 bags", 10.0, "Morning");
        assertEquals(25.5, cost, 0.01);
    }

    @Test
    public void testMinivanCost() {
        double cost = TariffCalculator.calculateCost("Minivan", "0 bags", 10.0, "Morning");
        assertEquals(22.1, cost, 0.01);
    }

    @Test
    public void testNightIsMoreExpensiveThanMorning() {
        double morning = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Morning");
        double night = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Night");

        assertTrue(night > morning);
    }
}