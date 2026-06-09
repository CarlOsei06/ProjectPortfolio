public class Booking {

    String bookingRef;
    String surname;
    String placementDateTime;
    String vehicleType;
    int luggageCount;
    String bookingDateTime;
    double cost;
    double distance;
    boolean isCancelled;

    public Booking(String bookingRef,
                   String surname,
                   String placementDateTime,
                   String vehicleType,
                   int luggageCount,
                   String bookingDateTime,
                   double cost,
                   boolean isCancelled) {

        this(bookingRef, surname, placementDateTime, vehicleType, luggageCount, bookingDateTime, cost, 0.0, isCancelled);
    }

    public Booking(String bookingRef,
                   String surname,
                   String placementDateTime,
                   String vehicleType,
                   int luggageCount,
                   String bookingDateTime,
                   double cost,
                   double distance,   // had to include for the AmendBookingUI
                   boolean isCancelled) {

        this.bookingRef = bookingRef;
        this.surname = surname;
        this.placementDateTime = placementDateTime;
        this.vehicleType = vehicleType;
        this.luggageCount = luggageCount;
        this.bookingDateTime = bookingDateTime;
        this.cost = cost;
        this.distance = distance;
        this.isCancelled = isCancelled;
    }

    // needed to add this section for the AmendBookingUI
    public String getBookingRef() {
        return bookingRef;
    }

    public String getSurname() {
        return surname;
    }

    public String getPlacementDateTime() {
        return placementDateTime;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getLuggageCount() {
        return luggageCount;
    }

    public String getBookingDateTime() {
        return bookingDateTime;
    }

    public double getCost() {
        return cost;
    }

    public double getDistance() {
        return distance;
    }

    public void setVehicleType(String selectedItem) {
        this.vehicleType = selectedItem;
    }

    public void setLuggageCount(int i) {
        this.luggageCount = i;
    }

    public void setBookingDateTime(String text) {
        this.bookingDateTime = text;
    }

    public void setCost(double newCost) {
        this.cost = newCost;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
    public void setDistance(double distance){
        this.distance = distance;
    }



}
