package ru.nsu.fit.tsibin;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import ru.nsu.fit.tsibin.Exceptions.IncorrectResponseException;
import ru.nsu.fit.tsibin.Exceptions.LocationNotFoundException;

import static java.lang.System.in;

public class APIWorker {

    private static final Properties properties = new Properties();
    private static final String CONFIG_PATH = "src/resources/config.properties";

    static {
        try {
            properties.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private final int SUCCESS_ANSWER_CODE_BEGIN = 2;
    private final String GEOCODING_PKEY = properties.getProperty("geocoding_pkey");
    private final String OPENWEATHER_PKEY = properties.getProperty("openweather_pkey");

    private final HttpClient client = HttpClient.newHttpClient();

    private final int ATTEMPTS_AMOUNT = 3;

    private final int DEFAULT_LOCATION_NUM = 0;

    private String buildGeocodingURI(String locationName) {
        return "https://graphhopper.com/api/1/geocode?q=" + locationName + "&key=" + GEOCODING_PKEY;
    }

    private String buildOpenWeatherURI(BigDecimal lat, BigDecimal lng) {
        String strLng = lng.setScale(2, RoundingMode.DOWN).toString();
        String strLat = lat.setScale(2, RoundingMode.DOWN).toString();

        return "http://api.openweathermap.org/data/2.5/weather?lat=" + strLat + "&lon="
                + strLng + "&appid=" + OPENWEATHER_PKEY;
    }


    public Location getUserChooseLocation(List<Location> locationsList) throws NullPointerException, LocationNotFoundException {
        if (locationsList == null) {
            throw new NullPointerException("empty locations list");
        }

        if(locationsList.size() == 0){
            throw new LocationNotFoundException();
        }

        System.out.println("choose location:");
        for (int i = 0; i < locationsList.size(); i++) {
            System.out.println((i + 1) + ") " + locationsList.get(i).data());
        }

        Scanner scanner = new Scanner(in);
        int locationNum = 0;

        for (int i = 0; i < ATTEMPTS_AMOUNT; i++) {
            int ans = 0;
            try {
                ans = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("try again, wrong input");
                continue;
            }
            if (ans > locationsList.size() || ans < 1) {
                System.out.println("try again, no such chose: " + ans + ")");
            } else {
                locationNum = ans;
                break;
            }
        }

        if (locationNum == 0)//it means all attempts was lost
            return locationsList.get(DEFAULT_LOCATION_NUM);

        return locationsList.get(locationNum - 1);// -1 so as our list start with 0
    }

    private boolean isOk(int code) {//check first digit in response code number
        if (code / 100 != SUCCESS_ANSWER_CODE_BEGIN)
            return false;
        return true;
    }

    private Location findLocation(String locationName) {

        String strURI = buildGeocodingURI(locationName);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(strURI))
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (isOk(response.statusCode())) {

                String jsonStr = response.body();

                return getUserChooseLocation(JSONParser.getLocations(jsonStr));
            } else {
                throw new IncorrectResponseException(response.statusCode());
            }


        } catch (URISyntaxException | IOException | InterruptedException | LocationNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void showWeather(Location location) {
        String strURI = buildOpenWeatherURI(location.lat(), location.lng());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(strURI))
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (isOk(response.statusCode())) {

                String jsonStr = response.body();
                WeatherData currentWeather = JSONParser.getWeatherData(jsonStr);
                currentWeather.printWeatherData(System.out);
            } else {
                throw new IncorrectResponseException(response.statusCode());
            }

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void start() throws ExecutionException, InterruptedException {
        Scanner consoleReader = new Scanner(in);
        System.out.println("enter location name:");
        String locationName = consoleReader.next();
        System.out.println("processing...");

        CompletableFuture<Location> future = CompletableFuture.supplyAsync(() -> findLocation(locationName));

        showWeather(future.get());
    }
}
