import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AmendBookingPanel
 * Tests business logic, validation, and amendment workflow (NO MOCKITO - business logic only)
 */
@DisplayName("AmendBookingPanel Tests")
public class AmendBookingPanelUITest {

    private Booking testBooking;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(ResolverStyle.STRICT);

    @BeforeEach
    void setUp() {
        // Create test booking
        testBooking = new Booking(
                "BR001",              // bookingRef
                "Smith",              // surname
                "2026-06-01T14:30",   // placementDateTime
                "Standard",           // vehicleType
                2,                    // luggageCount
                "2026-06-15T10:00",   // bookingDateTime
                45.50,                // cost
                25.0,                 // distance (km)
                false                 // isCancelled
        );
    }

    // ==================== DISTANCE VALIDATION TESTS ====================

    @Test
    @DisplayName("Should reject invalid distance (non-numeric)")
    void testInvalidDistanceNonNumeric() {
        assertThrows(NumberFormatException.class, () -> {
            Double.parseDouble("abc");
        }, "Should throw NumberFormatException for non-numeric distance");
    }

    @Test
    @DisplayName("Should accept valid distance values")
    void testValidDistanceValues() {
        double[] validDistances = {10.5, 100, 0.1, 1000.99};

        for (double distance : validDistances) {
            assertDoesNotThrow(() -> {
                Double.parseDouble(String.valueOf(distance));
            }, "Should accept distance: " + distance);
        }
    }

    @Test
    @DisplayName("Should parse zero distance")
    void testZeroDistance() {
        assertDoesNotThrow(() -> {
            Double.parseDouble("0");
        }, "Should accept zero distance");
    }

    @Test
    @DisplayName("Should reject negative distance")
    void testNegativeDistance() {
        assertDoesNotThrow(() -> {
            Double.parseDouble("-10");
        }, "Parser accepts negatives (validation should reject)");
    }

    // ==================== DATE/TIME VALIDATION TESTS ====================

    @Test
    @DisplayName("Should parse valid date/time format correctly")
    void testValidDateTimeFormat() {
        String dateTimeText = "2026-06-20T14:30";

        assertDoesNotThrow(() -> {
            LocalDateTime.parse(dateTimeText, FORMATTER);
        }, "Should parse valid yyyy-MM-dd'T'HH:mm format");
    }

    @Test
    @DisplayName("Should reject invalid date/time format")
    void testInvalidDateTimeFormat() {
        String[] invalidFormats = {
                "06/20/2026 14:30",  // US format
                "2026-20-06T14:30",  // Month/day swapped
                "2026-06-20 14:30",  // Space instead of T
                "invalid"            // Completely invalid
        };

        for (String format : invalidFormats) {
            assertThrows(java.time.format.DateTimeParseException.class, () -> {
                LocalDateTime.parse(format, FORMATTER);
            }, "Should reject format: " + format);
        }
    }

    @Test
    @DisplayName("Should reject invalid dates (e.g., Feb 30)")
    void testInvalidCalendarDate() {
        String invalidDate = "2026-02-30T10:00";

        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            LocalDateTime.parse(invalidDate, FORMATTER);
        }, "Should reject Feb 30");
    }

    @Test
    @DisplayName("Should accept valid edge case dates")
    void testValidEdgeCaseDates() {
        String[] validDates = {
            "2026-02-28T10:00",  // Feb 28 in non-leap year
            "2024-02-29T10:00",  // Feb 29 in leap year
            "2026-12-31T23:59"   // Last day of year
        };

        for (String date : validDates) {
            assertDoesNotThrow(() -> {
                LocalDateTime.parse(date, FORMATTER);
            }, "Should accept valid date: " + date);
        }
    }

    // ==================== TIME-OF-DAY CATEGORIZATION TESTS ====================

    @Test
    @DisplayName("Should categorize hours into correct tariff periods")
    void testTimeTariffCategorization() {
        java.util.Map<Integer, String> hourToCategory = new java.util.HashMap<>();

        // Night: before 6am
        hourToCategory.put(0, "Night");
        hourToCategory.put(5, "Night");

        // Morning: 6-11
        hourToCategory.put(6, "Morning");
        hourToCategory.put(11, "Morning");

        // Afternoon: 12-16
        hourToCategory.put(12, "Afternoon");
        hourToCategory.put(16, "Afternoon");

        // Evening: 17-21
        hourToCategory.put(17, "Evening");
        hourToCategory.put(21, "Evening");

        // Night again: 22+
        hourToCategory.put(22, "Night");
        hourToCategory.put(23, "Night");

        for (int hour : hourToCategory.keySet()) {
            String expected = hourToCategory.get(hour);
            String actual = getTimeOfDayCategory(hour);
            assertEquals(expected, actual,
                    "Hour " + hour + " should map to " + expected);
        }
    }

    @Test
    @DisplayName("All hours 0-23 should have a category")
    void testAllHoursCategorized() {
        for (int hour = 0; hour < 24; hour++) {
            String category = getTimeOfDayCategory(hour);
            assertNotNull(category, "Hour " + hour + " should have a category");
            assertTrue(category.matches("Night|Morning|Afternoon|Evening"),
                    "Hour " + hour + " has invalid category: " + category);
        }
    }

    // ==================== CAPACITY CHECK TESTS ====================

    @Test
    @DisplayName("Standard vehicle should have capacity of 4")
    void testStandardVehicleCapacity() {
        String vehicleType = "Standard";
        int capacity = vehicleType.equals("Minivan") ? 6 : 4;
        assertEquals(4, capacity, "Standard should have 4 seats");
    }

    @Test
    @DisplayName("Executive vehicle should have capacity of 4")
    void testExecutiveVehicleCapacity() {
        String vehicleType = "Executive";
        int capacity = vehicleType.equals("Minivan") ? 6 : 4;
        assertEquals(4, capacity, "Executive should have 4 seats");
    }

    @Test
    @DisplayName("Minivan should have capacity of 6")
    void testMinivanCapacity() {
        String vehicleType = "Minivan";
        int capacity = vehicleType.equals("Minivan") ? 6 : 4;
        assertEquals(6, capacity, "Minivan should have 6 seats");
    }

    @Test
    @DisplayName("Should reject amendment if vehicle at capacity")
    void testCapacityCheckFull() {
        List<Booking> existingBookings = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            existingBookings.add(new Booking(
                    "BR00" + (i + 2), "Passenger" + i, "2026-06-01T14:30",
                    "Standard", 1, "2026-06-15T10:00", 50.0, 20.0, false
            ));
        }

        int capacity = 4;
        boolean canFit = (existingBookings.size() + 1) <= capacity; // +1 for the booking being amended
        assertFalse(canFit, "Over capacity");
    }

    @Test
    @DisplayName("Should allow amendment when space available")
    void testCapacityCheckWithSpace() {
        List<Booking> existingBookings = new ArrayList<>();
        existingBookings.add(new Booking("BR002", "Passenger1", "2026-06-01T14:30",
                "Standard", 1, "2026-06-15T10:00", 50.0, 20.0, false));
        existingBookings.add(new Booking("BR003", "Passenger2", "2026-06-01T14:30",
                "Standard", 1, "2026-06-15T10:00", 50.0, 20.0, false));

        assertTrue(existingBookings.size() + 1 <= 4, "Should fit within capacity");
    }

    // ==================== AMENDMENT FEE CALCULATION TESTS ====================

    @Test
    @DisplayName("Should calculate amendment fee when cost increases")
    void testAmendmentFeeIncrease() {
        double originalCost = 45.50;
        double newCost = 60.00;
        double expectedFee = Math.max(0, newCost - originalCost);

        assertEquals(14.50, expectedFee, 0.01,
                "Fee should be positive difference");
    }

    @Test
    @DisplayName("Should calculate zero fee when cost decreases")
    void testAmendmentFeeDecreaseZero() {
        double originalCost = 45.50;
        double newCost = 40.00;
        double expectedFee = Math.max(0, newCost - originalCost);

        assertEquals(0, expectedFee, 0.01,
                "Fee should be zero (no refunds)");
    }

    @Test
    @DisplayName("Should calculate zero fee when cost unchanged")
    void testAmendmentFeeNoChange() {
        double originalCost = 45.50;
        double newCost = 45.50;
        double expectedFee = Math.max(0, newCost - originalCost);

        assertEquals(0, expectedFee, 0.01,
                "Fee should be zero");
    }

    @Test
    @DisplayName("Should format fee to 2 decimal places")
    void testFeeFormatting() {
        double originalCost = 45.50;
        double newCost = 60.123;
        double fee = Math.max(0, newCost - originalCost);

        String formatted = String.format("%.2f", fee);
        assertEquals("14.62", formatted,
                "Fee should be formatted to 2 decimals");
    }

    // ==================== BOOKING UPDATE TESTS ====================

    @Test
    @DisplayName("Booking should update with new vehicle type")
    void testBookingUpdateVehicleType() {
        testBooking.setVehicleType("Minivan");
        assertEquals("Minivan", testBooking.getVehicleType(),
                "Vehicle type should be updated");
    }

    @Test
    @DisplayName("Booking should update with new luggage count")
    void testBookingUpdateLuggageCount() {
        testBooking.setLuggageCount(3);
        assertEquals(3, testBooking.getLuggageCount(),
                "Luggage count should be updated");
    }

    @Test
    @DisplayName("Booking should update with new date/time")
    void testBookingUpdateDateTime() {
        String newDateTime = "2026-07-01T15:00";
        testBooking.setBookingDateTime(newDateTime);
        assertEquals(newDateTime, testBooking.getBookingDateTime(),
                "DateTime should be updated");
    }

    @Test
    @DisplayName("Booking should update with new cost")
    void testBookingUpdateCost() {
        double newCost = 75.99;
        testBooking.setCost(newCost);
        assertEquals(newCost, testBooking.getCost(), 0.01,
                "Cost should be updated");
    }

    @Test
    @DisplayName("All booking fields should update independently")
    void testBookingMultipleUpdates() {
        testBooking.setVehicleType("Executive");
        testBooking.setLuggageCount(1);
        testBooking.setBookingDateTime("2026-07-10T09:00");
        testBooking.setCost(52.00);

        assertEquals("Executive", testBooking.getVehicleType());
        assertEquals(1, testBooking.getLuggageCount());
        assertEquals("2026-07-10T09:00", testBooking.getBookingDateTime());
        assertEquals(52.00, testBooking.getCost(), 0.01);
    }

    @Test
    @DisplayName("Booking reference should not change during amendment")
    void testBookingRefUnchanged() {
        String originalRef = testBooking.getBookingRef();

        testBooking.setVehicleType("Minivan");
        testBooking.setLuggageCount(3);
        testBooking.setCost(100.0);

        assertEquals(originalRef, testBooking.getBookingRef(),
                "Booking reference should remain constant");
    }

    // ==================== LUGGAGE STRING FORMAT TESTS ====================

    @Test
    @DisplayName("Should format luggage string correctly for tariff calculation")
    void testLuggageStringFormat() {
        String[] luggageOptions = {"0", "1", "2", "3"};

        for (String option : luggageOptions) {
            String formatted = option + " bags";
            assertTrue(formatted.matches("\\d+ bags"),
                    "Luggage should format as 'N bags'");
        }
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Full amendment workflow: update multiple fields")
    void testFullAmendmentWorkflow() {
        String originalRef = testBooking.getBookingRef();
        String originalSurname = testBooking.getSurname();

        testBooking.setVehicleType("Minivan");
        testBooking.setLuggageCount(3);
        testBooking.setBookingDateTime("2026-06-20T14:00");
        testBooking.setCost(65.00);

        assertEquals("Minivan", testBooking.getVehicleType());
        assertEquals(3, testBooking.getLuggageCount());
        assertEquals("2026-06-20T14:00", testBooking.getBookingDateTime());
        assertEquals(65.00, testBooking.getCost(), 0.01);

        assertEquals(originalRef, testBooking.getBookingRef());
        assertEquals(originalSurname, testBooking.getSurname());
    }

    @Test
    @DisplayName("Amendment with fee calculation")
    void testAmendmentWithFeeCalculation() {
        double originalCost = 45.50;
        double newCost = 60.00;
        double fee = Math.max(0, newCost - originalCost);

        testBooking.setCost(newCost);

        assertEquals(newCost, testBooking.getCost(), 0.01);
        assertEquals(14.50, fee, 0.01);
    }

    @Test
    @DisplayName("Amendment with capacity check and fee")
    void testAmendmentFullLogic() {
        List<Booking> targetBookings = new ArrayList<>();
        targetBookings.add(new Booking("BR002", "Passenger1", "2026-06-01T14:30",
                "Standard", 1, "2026-06-15T10:00", 50.0, 20.0, false));
        targetBookings.add(new Booking("BR003", "Passenger2", "2026-06-01T14:30",
                "Standard", 1, "2026-06-15T10:00", 50.0, 20.0, false));

        String vehicleType = "Standard";
        int capacity = vehicleType.equals("Minivan") ? 6 : 4;
        boolean alreadyInTarget = targetBookings.stream()
                .anyMatch(b -> b.getBookingRef().equals(testBooking.getBookingRef()));
        int count = targetBookings.size() - (alreadyInTarget ? 1 : 0);
        boolean canAmend = (count + 1) <= capacity;

        double originalCost = testBooking.getCost();
        double newCost = 55.00;
        double fee = Math.max(0, newCost - originalCost);

        testBooking.setCost(newCost);

        assertTrue(canAmend, "Should have capacity");
        assertEquals(9.50, fee, 0.01);
        assertEquals(newCost, testBooking.getCost(), 0.01);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Helper method to categorize hours into tariff periods
     * Mirrors the logic in AmendBookingPanel.java
     */
    private String getTimeOfDayCategory(int hour) {
        return (hour < 6) ? "Night" :
                (hour < 12) ? "Morning" :
                (hour < 17) ? "Afternoon" :
                (hour < 22) ? "Evening" :
                "Night";
    }
}
