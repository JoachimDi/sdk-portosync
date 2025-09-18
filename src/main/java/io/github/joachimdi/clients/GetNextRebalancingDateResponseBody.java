package io.github.joachimdi.clients;

import java.time.LocalDate;

record GetNextRebalancingDateResponseBody(LocalDate nextRebalancingDate) {}
