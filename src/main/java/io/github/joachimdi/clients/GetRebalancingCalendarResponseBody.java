package io.github.joachimdi.clients;

import java.time.LocalDate;
import java.util.List;

record GetRebalancingCalendarResponseBody(List<LocalDate> rebalancingDates) {
}
