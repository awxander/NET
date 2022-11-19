package ru.nsu.fit.tsibin;

public record WeatherData(float temp, float minTemp, float maxTemp, float feelsLikeTemp,
                          int pressure, float windSpeed, String description) {
}
