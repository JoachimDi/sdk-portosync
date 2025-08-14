package io.github.joachimdi.clients;

import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.exceptions.PortosyncApiClientException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortosyncApiClientTest {

    private HttpClient httpClient;

    private HttpResponse response;

    private PortosyncApiClient portosyncApiClient;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        portosyncApiClient = new PortosyncApiClient(httpClient);
    }

    @Test
    void givenYearAndStockMarket_whenGetHolidays_thenReturnHolidays() throws IOException, InterruptedException, URISyntaxException {
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                        {
                          "closingDates": [
                            "2025-12-26",
                            "2025-12-25",
                            "2025-01-01"
                          ]
                        }
                        """);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        Set<LocalDate> holidays = portosyncApiClient.getHolidays(2025, StockMarket.NYSE);

        Assertions.assertThat(holidays).containsAll(
                Set.of(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 26),
                        LocalDate.of(2025, 12, 25)
                )
        );
    }

    @Test
    void givenYearAndStockMarket_whenGetHolidays_shouldThrowException() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        Throwable thrown = Assertions.catchThrowable(() -> portosyncApiClient.getHolidays(2025, StockMarket.NYSE));

        Assertions.assertThat(thrown).isInstanceOf(PortosyncApiClientException.class).hasMessageContaining("400");
    }
}
