package ru.nsu.fit.tsibin;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

public record WeatherData(float temp, float minTemp, float maxTemp, float feelsLikeTemp,
                          int pressure, float windSpeed, String description) {

    public void printWeatherData(PrintStream outStream){
        outStream.println("description: " + description);
        outStream.println("temp: " + temp);
        outStream.println("min temp: " + minTemp);
        outStream.println("max temp: " + maxTemp);
        outStream.println("feels like temp: " + feelsLikeTemp);
        outStream.println("pressure: " + pressure);
        outStream.println("wind speed: " + windSpeed);
    }
}
