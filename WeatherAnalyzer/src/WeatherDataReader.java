import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeatherDataReader {
    private static final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int EXPECTED_NUMBER_OF_FIELDS = 6;

    public WeatherDataReader()
    {
    }

    /**
     * Read a CSV file of weather data.
     * @param filePath The source of the data.
     * @return The list of weather data objects.
     * @throws IOException On any IO issue.
     */
    public List<WeatherData> readWeatherData(String filePath)
            throws IOException {
        List<WeatherData> weatherDataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header line
            br.readLine();
            while ((line = br.readLine()) != null) {
                // Skip empty or whitespace-only lines which may be present at the end
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == EXPECTED_NUMBER_OF_FIELDS) {
                    WeatherData weatherData = getWeatherData(parts);
                    weatherDataList.add(weatherData);
                } else {
                    throw new IOException("Expected " + EXPECTED_NUMBER_OF_FIELDS + " fields in the data.");
                }
            }
        }
        return weatherDataList;
    }

    /**
     * Extract the data values from parts.
     * @param parts The fields of the input file.
     * @return The weather data object containing the data.
     */
    private WeatherData getWeatherData(String[] parts) {
        String date = parts[0].trim();
        String weatherCondition = parts[1].trim().toLowerCase();
        Double maxTemp = Double.parseDouble(parts[2].trim());
        Double rainSum = Double.parseDouble(parts[3].trim());
        Double maxWindSpeed = Double.parseDouble(parts[4].trim());
        Double snowfallSum = Double.parseDouble(parts[5].trim());

        return
                new WeatherData(LocalDate.parse(date, dateFormatter),
                        weatherCondition, maxTemp, rainSum, maxWindSpeed, snowfallSum);
    }


}
