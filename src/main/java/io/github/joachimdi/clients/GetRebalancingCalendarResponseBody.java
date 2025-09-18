package io.github.joachimdi.clients;

import java.time.LocalDate;
import java.util.Set;

record GetRebalancingCalendarResponseBody(Set<LocalDate> rebalancingDates) {
}
