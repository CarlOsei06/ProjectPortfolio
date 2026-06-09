import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    private static final String FILE_NAME = "bookings.csv";

    // ================= add booking =================
    public static String addBooking(String surname,
                                    String placementDateTime,
                                    String vehicleType,
                                    int luggageCount,
                                    String bookingDateTime,
                                    double cost) {

        int nextId = getNextBookingNumber(); // Generate next booking number
        String bookingRef = String.format("BR%03d", nextId); // Format as BR001, BR002, etc.

        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            // Write new booking to file in format: bookingRef,surname,placementDateTime,vehicleType,luggageCount,bookingDateTime,cost,isCancelled
            // is celled defaults to false for new bookings
            writer.write(
                    bookingRef + "," +
                    surname + "," +
                    placementDateTime + "," +
                    vehicleType + "," +
                    luggageCount + "," +
                    bookingDateTime + "," +
                    cost + ",false\n"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookingRef;
    }
    
    public static boolean deleteBooking(String bookingRef) {
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length > 0 && data[0].equals(bookingRef)) {
                    found = true;
                    continue;
                }

                updatedLines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return found;
    }
    public static void addExistingBooking(Booking booking) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(
                booking.bookingRef + "," +
                booking.surname + "," +
                booking.placementDateTime + "," +
                booking.vehicleType + "," +
                booking.luggageCount + "," +
                booking.bookingDateTime + "," +
                booking.cost + "," +
                booking.isCancelled + "\n"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =============== get booking ===============
    public static Booking getBooking(String bookingRef, String surname) {

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {

                // Split line into fields and check if bookingRef and surname match
                String[] data = line.split(",");

                String ref = data[0];
                String fileSurname = data[1];

                if (ref.equals(bookingRef) &&
                        fileSurname.equalsIgnoreCase(surname)) {
                    // If booking matches, create and return Booking object
                    return new Booking(
                            ref, fileSurname, data[2], data[3], Integer.parseInt(data[4]), data[5], Double.parseDouble(data[6]), Boolean.parseBoolean(data[7])
                    );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ============== get booking status ================
    public static boolean hasBookingPassed(String bookingID) {

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                // Check if booking ID matches, then parse booking time and compare to current time to determine if booking has passed
                if (data[0].equals(bookingID)) {

                    LocalDateTime bookingTime = LocalDateTime.parse(data[5]);

                    return LocalDateTime.now().isAfter(bookingTime);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================ get cancellation fee ================
    public static double getCancellationFee(String bookingID) {

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data[0].equals(bookingID)) {

                    LocalDateTime bookingTime =
                            LocalDateTime.parse(data[5]);

                    double cost =
                            Double.parseDouble(data[6]);
                    // Calculate minutes until booking time, used minutes and not hours to avoid rounding issues with partial hours (e.g. 35.5 hours should still incur fee, but 36 hours should not)
                    long minutesUntilBooking =
                            java.time.Duration.between(
                                    LocalDateTime.now(),
                                    bookingTime
                            ).toMinutes();

                    // Fee only applies if booking is under 36 hours away
                    if (minutesUntilBooking < (36 * 60)) {
                        return cost * 0.1;
                    }

                    return 0.0;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    // ================ cancel booking ===============
    public static String cancelBooking(String bookingID, String surname) {

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;
        boolean alreadyCancelled = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            String header = reader.readLine();
            updatedLines.add(header);

            while ((line = reader.readLine()) != null) {
                // Locate booking record
                String[] data = line.split(",");

                String ref = data[0];
                String fileSurname = data[1];
                boolean isCancelled = Boolean.parseBoolean(data[7]);

                if (ref.equals(bookingID) &&
                        fileSurname.equalsIgnoreCase(surname)) {

                    found = true;

                    if (isCancelled) {
                        alreadyCancelled = true;
                        updatedLines.add(line);
                    } else {
                        data[7] = "true"; // Mark as cancelled
                        updatedLines.add(String.join(",", data));
                    }
                } else {
                    updatedLines.add(line);
                }
            }
            // ============== handle errors to display ==============
        } catch (IOException e) {
            e.printStackTrace();
            return "error"; // Return error if file read fails
        }

        if (!found) return "not found"; // Return not found if booking record doesn't exist
        if (alreadyCancelled) return "already cancelled"; // Return already cancelled if booking is already cancelled

        // Write updated booking data back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            for (String l : updatedLines) {
                writer.write(l);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "error"; // Return error if file write fails
        }

        return "success"; // Return success if cancellation was successful
    }

    // ================= get status =================
    public static String getStatus(String bookingID) {

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            // Locate Booking record
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data[0].equals(bookingID)) {
                    return Boolean.parseBoolean(data[7]) ? "CANCELLED" : "ACTIVE"; // Return CANCELLED if isCancelled is true, otherwise return ACTIVE
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "not found"; // Return not found if booking record doesn't exist
    }

    // ================= next id =================
    private static int getNextBookingNumber() {
        // Helper method to generate next number for addBooking
        int max = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {

                String ref = line.split(",")[0];

                int num = Integer.parseInt(ref.substring(2));

                if (num > max) {
                    max = num;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return max + 1;
    }

    //  return list of bookings matching bookingDateTime and vehicleType (excluding cancelled)
    public static List<Booking> getBookingsByDateAndVehicle(String bookingDateTime, String vehicleType) {
        List<Booking> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                String ref = data[0];
                String fileSurname = data[1];
                String fileVehicle = data[3];
                String fileBookingDateTime = data[5];
                boolean isCancelled = Boolean.parseBoolean(data[7]);

                if (!isCancelled && fileVehicle.equals(vehicleType) && fileBookingDateTime.equals(bookingDateTime)) {
                    // Create booking object and add
                    Booking b = new Booking(ref, fileSurname, data[2], fileVehicle, Integer.parseInt(data[4]), fileBookingDateTime, Double.parseDouble(data[6]), isCancelled);
                    results.add(b);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
    // update booking (for amendments)
    public static boolean updateBooking(Booking updatedBooking) {

        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String header = reader.readLine();
            updatedLines.add(header);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(updatedBooking.getBookingRef())) {

                    // Replace with updated booking details
                    updatedLines.add(
                            updatedBooking.getBookingRef() + "," +
                                    updatedBooking.getSurname() + "," +
                                    updatedBooking.getPlacementDateTime() + "," +
                                    updatedBooking.getVehicleType() + "," +
                                    updatedBooking.getLuggageCount() + "," +
                                    updatedBooking.getBookingDateTime() + "," +
                                    updatedBooking.getCost() + "," +
                                    updatedBooking.isCancelled()
                    );

                } else {
                    updatedLines.add(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String l : updatedLines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // get past ride count for user (for loyalty discount)
    public static int getPastRideCountForUser(String surname) {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length >= 8) {
                    String fileSurname = data[1];
                    boolean isCancelled = Boolean.parseBoolean(data[7]);
                    if (fileSurname.equalsIgnoreCase(surname) && !isCancelled) {
                        count++;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }   


}