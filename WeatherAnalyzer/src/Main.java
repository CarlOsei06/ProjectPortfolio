import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;

    public class Main {
        private static String resolveWeatherDataFile(String fileName) {
            Path directPath = Paths.get(fileName);
            if (Files.exists(directPath)) {
                return directPath.toString();
            }

            Path srcPath = Paths.get("src").resolve(fileName);
            if (Files.exists(srcPath)) {
                return srcPath.toString();
            }

            return fileName;
        }

        public static void main(String[] args) throws IOException {
            String weatherDataFile;
            if (args.length == 0) {
                weatherDataFile = resolveWeatherDataFile("data/new-york.csv");
            } else {
                weatherDataFile = resolveWeatherDataFile(args[0]);
            }
            WeatherAnalyzer weatherAnalyzer = new WeatherAnalyzer(weatherDataFile);
            weatherAnalyzer.printWeatherConditionDays("overcast");
            double totalRainfall = weatherAnalyzer.getTotalRainfall();
            System.out.println("Total Rainfall: " + totalRainfall);

            weatherAnalyzer.printFirstThreeSnowfallDays();
            weatherAnalyzer.printFirstSnowfallDays(3);
            weatherAnalyzer.printMaxWindSpeedFirst31Days();

            // For Athens data
            weatherDataFile = resolveWeatherDataFile("data/athens.csv");
            weatherAnalyzer = new WeatherAnalyzer(weatherDataFile);
            double totalRainfallApril = weatherAnalyzer.getTotalRainfallForMonth(Month.APRIL);
            System.out.println("Total Rainfall for Athens in April: " + totalRainfallApril);

            // For New York data in October
            weatherDataFile = resolveWeatherDataFile("data/new-york.csv");
            weatherAnalyzer = new WeatherAnalyzer(weatherDataFile);
            double totalRainfallOctober = weatherAnalyzer.getTotalRainfallForMonth(Month.OCTOBER);
            System.out.println("Total Rainfall for New York in October: " + totalRainfallOctober);
        }
    }