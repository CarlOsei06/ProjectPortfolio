import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class BookingManagerTest {

    @BeforeEach
    public void setup() throws IOException {
        try (FileWriter writer = new FileWriter("bookings.csv", false)) {
            writer.write("bookingRef,surname,placementDateTime,vehicleType,luggageCount,bookingDateTime,cost,isCancelled\n");
        }
    }

    private void addTestBooking(String bookingRef, double cost, LocalDateTime bookingDateTime) throws IOException {
        try (FileWriter writer = new FileWriter("bookings.csv", true)) {
            writer.write(
                bookingRef + "," +
                "TestSurname," +
                "2026-01-01T10:00," +
                "Car," +
                "1," +
                bookingDateTime + "," +
                cost + "," +
                "false\n"
            );
        }
    }

    @Test
    public void bookingMoreThan36HoursHasNoFee() throws IOException {
        addTestBooking("BR001", 100, LocalDateTime.now().plusHours(48));

        double fee = BookingManager.getCancellationFee("BR001");

        assertEquals(0.0, fee, 0.01);
    }

    @Test
    public void bookingExactly36HoursHasNoFee() throws IOException {
        addTestBooking("BR002", 100, LocalDateTime.now().plusHours(36).plusMinutes(1));

        double fee = BookingManager.getCancellationFee("BR002");

        assertEquals(0.0, fee, 0.01);
    }

    @Test
    public void booking35HoursHas10PercentFee() throws IOException {
        addTestBooking("BR003", 100, LocalDateTime.now().plusHours(35));

        double fee = BookingManager.getCancellationFee("BR003");

        assertEquals(10.0, fee, 0.01);
    }

    @Test
    public void bookingOneHourHas10PercentFee() throws IOException {
        addTestBooking("BR004", 50, LocalDateTime.now().plusHours(1));

        double fee = BookingManager.getCancellationFee("BR004");

        assertEquals(5.0, fee, 0.01);
    }

    @Test
    public void passedBookingStillReturnsFee() throws IOException {
        addTestBooking("BR005", 80, LocalDateTime.now().minusHours(2));

        double fee = BookingManager.getCancellationFee("BR005");

        assertEquals(8.0, fee, 0.01);
    }
}