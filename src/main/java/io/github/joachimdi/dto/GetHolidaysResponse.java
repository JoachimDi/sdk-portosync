package io.github.joachimdi.dto;

import java.time.LocalDate;
import java.util.Set;

public record GetHolidaysResponse(Set<LocalDate> closingDates) {
}
