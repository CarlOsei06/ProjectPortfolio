import java.time.LocalDate;

/**
 * Store weather data.
 *
 * @param date             The date of the data.
 * @param weatherCondition A description of the weather.
 * @param maxTemp          The maximum temperature.
 * @param rainSum          The total rainfall.
 * @param maxWindSpeed     The maximum wind speed.
 * @param snowfallSum      The total snowfall.
 */
public record WeatherData(LocalDate date, String weatherCondition, Double maxTemp,
                          Double rainSum, Double maxWindSpeed, Double snowfallSum) {
    public static final String CSV_HEADER = "Date,WMO name,maxTemp,rainSum,maxWindSpeed,snowFallSum";

    /**
     * Format the data.
     *
     * @return
     */
    @Override
    public String toString() {
        return String.format("%s, %s, maxTemp=%.1f, rain=%.1f, maxWindSpeed=%.1f, snowfall=%.1f",
                date, weatherCondition, maxTemp, rainSum, maxWindSpeed, snowfallSum);
    }

    /**
     * Format the data in CSV format.
     *
     * @return
     */
    public String formatAsCSV() {
        return String.format("%s,%s,%.2f,%.2f,%.2f,%.2f",
                date, weatherCondition, maxTemp, rainSum, maxWindSpeed, snowfallSum);
    }
}