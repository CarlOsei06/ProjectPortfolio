import java.io.IOException;
import java.util.List;
import java.time.Month;

public class WeatherAnalyzer {
    private static final WeatherDataReader reader = new WeatherDataReader();
    private final List<WeatherData> weatherData;

    /**
     * Create a new weather analyser.
     *
     * @param weatherDataFile The file of weather data.
     * @throws IOException On any IO issue.
     */
    public WeatherAnalyzer(String weatherDataFile)
            throws IOException {
        this.weatherData = reader.readWeatherData(weatherDataFile);

    }

    /**
     * Print out each day's weather data.
     */
    public void printWeatherData() {
       weatherData.forEach(System.out::println);
    }

    /**
     * Print out the days with clear sky.
     */
    public void printWeatherConditionDays(String condition) {
                weatherData.stream()
                        .filter(data -> data.weatherCondition().contains(condition))
                        .forEach(System.out::println);


            }

    /**
     *
     * @param condition
     */
    public void printDatesForWeatherCondition(String condition) {
                weatherData.stream()
                        .filter(data -> data.weatherCondition().contains(condition))
                        .map(WeatherData::date)
                        .forEach(System.out::println);
            }
            public double getTotalRainfall() {
                return weatherData.stream()
                        .mapToDouble(WeatherData::rainSum)
                        .sum();
            }
            // In WeatherAnalyzer.java
            // In WeatherAnalyzer.java
            public void printFirstSnowfallDays(int numberOfDays) {
                weatherData.stream()
                        .filter(data -> data.snowfallSum() > 0)
                        .limit(numberOfDays)
                        .forEach(System.out::println);
            }
            // In WeatherAnalyzer.java
            public void printFirstThreeSnowfallDays() {
                weatherData.stream()
                        .filter(data -> data.snowfallSum() > 0)
                        .limit(3)
                        .forEach(System.out::println);
            }
            public void printMaxWindSpeedFirst31Days() {
                weatherData.stream()
                        .limit(31)
                        .mapToDouble(WeatherData::maxWindSpeed)
                        .max()
                        .ifPresent(maxWindSpeed -> System.out.println("Max Wind Speed in First 31 Days: " + maxWindSpeed));
            }
            public double getTotalRainfallAfterSkipping31Days() {
                return weatherData.stream()
                        .skip(31)
                        .mapToDouble(WeatherData::rainSum)
                        .sum();
            }
            public double getTotalRainfallForNext28DaysAfterSkipping31() {
                return weatherData.stream()
                        .skip(31)
                        .limit(28)
                        .mapToDouble(WeatherData::rainSum)
                        .sum();
            }


            public double getTotalRainfallForMonth(Month month) {
                return weatherData.stream()
                        .filter(data -> data.date().getMonth() == month)
                        .mapToDouble(WeatherData::rainSum)
                        .sum();
            }
        }


