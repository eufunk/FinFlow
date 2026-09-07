package com.finflow.scenario.api;

import java.math.BigDecimal;

public record YearlySnapshotResponse(int year, BigDecimal capital) {
}
