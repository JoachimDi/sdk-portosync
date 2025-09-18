package io.github.joachimdi.clients;

import java.time.LocalDate;
import java.util.Set;

record GetHolidaysResponseBody(Set<LocalDate> closingDates) {
}
