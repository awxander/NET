package ru.nsu.fit.tsibin.entities;

import java.math.BigDecimal;

public record Location(String data, BigDecimal lat, BigDecimal lng) {
}
