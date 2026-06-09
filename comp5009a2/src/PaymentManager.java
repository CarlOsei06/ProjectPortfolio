import java.io.*;
import java.util.Scanner;

public class PaymentManager {

    private static final String FILE_NAME = "payments.csv";

    public static void savePayment(
            String bookingRef,
            String email,
            String paymentMethod,
            String cardNumber,
            String csv,
            String expiry,
            boolean emailConfirmation,
            boolean saveDetails) {

        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {

            if (new File(FILE_NAME).length() == 0) {
                writer.write("bookingRef,email,paymentMethod,cardNumber,csv,expiry,emailConfirmation,saveDetails\n");
            }

            writer.write(
                bookingRef + "," +
                email + "," +
                paymentMethod + "," +
                cardNumber + "," +
                csv + "," +
                expiry + "," +
                emailConfirmation + "," +
                saveDetails + "\n"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getSavedPayment(String bookingRef) {

        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {

            if (scanner.hasNextLine()) {
                scanner.nextLine(); // skip header
            }

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");

                if (data.length >= 8 && data[0].equals(bookingRef) && Boolean.parseBoolean(data[7])) {
                    return data;
                }
            }

        } catch (IOException e) {
            return null;
        }

        return null;
    }
}