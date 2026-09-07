package com.finflow.scenario.domain;

import com.finflow.shared.Money;

/** Kapitalstand am Ende eines Simulationsjahres (year 0 = Startkapital), für die Chart-Anzeige. */
public record YearlySnapshot(int year, Money capital) {
}
