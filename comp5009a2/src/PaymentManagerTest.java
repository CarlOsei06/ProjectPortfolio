import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PaymentManagerTest {

    @BeforeEach
    public void setup() {
        File file = new File("payments.csv");

        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void savePaymentCreatesCsvFile() {
        PaymentManager.savePayment(
                "BR001",
                "test@email.com",
                "Credit Card",
                "1234567812345678",
                "123",
                "12/30",
                true,
                true
        );

        File file = new File("payments.csv");

        assertTrue(file.exists());
    }

    @Test
    public void savePaymentWritesHeaderAndPaymentDetails() throws IOException {
        PaymentManager.savePayment(
                "BR002",
                "josh@email.com",
                "Debit Card",
                "1111222233334444",
                "456",
                "11/29",
                true,
                true
        );

        String content = Files.readString(new File("payments.csv").toPath());

        assertTrue(content.contains("bookingRef,email,paymentMethod,cardNumber,csv,expiry,emailConfirmation,saveDetails"));
        assertTrue(content.contains("BR002,josh@email.com,Debit Card,1111222233334444,456,11/29,true,true"));
    }

    @Test
    public void getSavedPaymentReturnsSavedDetailsWhenSaveDetailsIsTrue() {
        PaymentManager.savePayment(
                "BR003",
                "saved@email.com",
                "Credit Card",
                "9999888877776666",
                "999",
                "10/28",
                true,
                true
        );

        String[] saved = PaymentManager.getSavedPayment("BR003");

        assertNotNull(saved);
        assertEquals("BR003", saved[0]);
        assertEquals("saved@email.com", saved[1]);
        assertEquals("Credit Card", saved[2]);
        assertEquals("9999888877776666", saved[3]);
        assertEquals("999", saved[4]);
        assertEquals("10/28", saved[5]);
        assertEquals("true", saved[6]);
        assertEquals("true", saved[7]);
    }

    @Test
    public void getSavedPaymentReturnsNullWhenSaveDetailsIsFalse() {
        PaymentManager.savePayment(
                "BR004",
                "nosave@email.com",
                "Stripe",
                "5555444433332222",
                "321",
                "09/27",
                true,
                false
        );

        String[] saved = PaymentManager.getSavedPayment("BR004");

        assertNull(saved);
    }

    @Test
    public void getSavedPaymentReturnsNullForUnknownBooking() {
        PaymentManager.savePayment(
                "BR005",
                "known@email.com",
                "PayPal",
                "0000111122223333",
                "111",
                "08/26",
                false,
                true
        );

        String[] saved = PaymentManager.getSavedPayment("BR999");

        assertNull(saved);
    }
}