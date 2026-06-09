import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BrowseServicesUITest {

    @Test
    public void testBasicCalculation() {
        double cost = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Morning");
        assertEquals(17.0, cost, 0.01);
    }

    @Test
    public void testExecutiveVehicleMultiplier() {
        double cost = TariffCalculator.calculateCost("Executive", "0 bags", 10.0, "Morning");
        assertEquals(25.5, cost, 0.01);
    }

    @Test
    public void testMinivanVehicleMultiplier() {
        double cost = TariffCalculator.calculateCost("Minivan", "0 bags", 10.0, "Morning");
        assertEquals(22.1, cost, 0.01);
    }

    @Test
    public void testLuggageOneBag() {
        double cost = TariffCalculator.calculateCost("Standard", "1 bag", 10.0, "Morning");
        assertEquals(19.0, cost, 0.01);
    }

    @Test
    public void testLuggageTwoBags() {
        double cost = TariffCalculator.calculateCost("Standard", "2 bags", 10.0, "Morning");
        assertEquals(21.0, cost, 0.01);
    }

    @Test
    public void testLuggageThreePlusBags() {
        double cost = TariffCalculator.calculateCost("Standard", "3+ bags", 10.0, "Morning");
        assertEquals(23.0, cost, 0.01);
    }

    @Test
    public void testEveningMultiplier() {
        double cost = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Evening");
        assertEquals(20.4, cost, 0.01);
    }

    @Test
    public void testNightMultiplier() {
        double cost = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Night");
        assertEquals(23.8, cost, 0.01);
    }

    @Test
    public void testZeroDistance() {
        double cost = TariffCalculator.calculateCost("Standard", "0 bags", 0.0, "Morning");
        assertEquals(5.0, cost, 0.01);
    }

    @Test
    public void testRepeatableCalculation() {
        double cost1 = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Morning");
        double cost2 = TariffCalculator.calculateCost("Standard", "0 bags", 10.0, "Morning");

        assertEquals(cost1, cost2, 0.01);
    }
}