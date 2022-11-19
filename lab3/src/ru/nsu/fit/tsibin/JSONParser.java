package ru.nsu.fit.tsibin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.nsu.fit.tsibin.Exceptions.IncorrectResponseException;

public class JSONParser {

    private static String getDataInStr(JSONObject jsonObject, String arg) {
        if (jsonObject.has(arg)) {
            String name = jsonObject.getString(arg);
            return arg + ": " + name + "; ";
        }
        return null;
    }

    public static List<Location> getLocations(String respBody) {

        List<Location> locationsList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(respBody);


        JSONArray arr = jsonObject.getJSONArray("hits"); // notice that `"posts": [...]`
        for (int i = 0; i < arr.length(); i++) {
            String locationData = "";

            JSONObject obj = arr.getJSONObject(i);
            locationData += getDataInStr(obj, "name");
            locationData += getDataInStr(obj, "country");
            locationData += getDataInStr(obj, "city");


            JSONObject point = obj.getJSONObject("point");
            BigDecimal latitude = point.getBigDecimal("lat");
            BigDecimal longitude = point.getBigDecimal("lng");
            Location location = new Location(locationData, latitude, longitude);
            locationsList.add(location);
            System.out.println();
            System.out.println("latitude: " + latitude + ", longitude: " + longitude);
        }
        return locationsList;
    }

    public static WeatherData getWeatherData(String respBody) {

        JSONObject jsonObject = new JSONObject(respBody);
        String weatherDescription = "no description";
        float temp = 0;
        float minTemp = 0;
        float maxTemp = 0;
        float feelsLikeTemp = 0;
        int pressure = 0;
        float windSpeed = 0;

        if (jsonObject.has("weather")) {
            JSONArray arr = jsonObject.getJSONArray("weather");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.has("description")) {
                    weatherDescription = obj.getString("description");
                }
            }
        }

        if (jsonObject.has("main")) {
            JSONObject mainBlock = jsonObject.getJSONObject("main");
            if(mainBlock.has("temp"))   temp = mainBlock.getFloat("temp");
            if(mainBlock.has("feels_like"))   feelsLikeTemp = mainBlock.getFloat("feels_like");
            if(mainBlock.has("temp_min"))   minTemp = mainBlock.getFloat("temp_min");
            if(mainBlock.has("temp_max"))   maxTemp = mainBlock.getFloat("temp_max");
            if(mainBlock.has("pressure"))   pressure = mainBlock.getInt("pressure");
        }
        if(jsonObject.has("wind")){//get wind speed
            if(jsonObject.getJSONObject("wind").has("speed"))
                windSpeed = jsonObject.getJSONObject("wind").getFloat("speed");
        }



        return new WeatherData(temp,minTemp,maxTemp,feelsLikeTemp,pressure,windSpeed,weatherDescription);
    }

}
