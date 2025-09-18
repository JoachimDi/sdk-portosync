package io.github.joachimdi.clients;

import io.github.joachimdi.enums.RebalancingFrequency;
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.exceptions.PortosyncApiClientException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Set;

import static io.github.joachimdi.clients.Constants.HEADER_API_KEY;
import static io.github.joachimdi.clients.Constants.MARKET_CALENDAR_API_URL;
import static io.github.joachimdi.clients.Constants.REBALANCING_DATE_API_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PortosyncApiClientTest {

    private HttpClient httpClient;

    private HttpResponse response;

    private PortosyncApiClient portosyncApiClient;

    private ArgumentCaptor<HttpRequest> requestCaptor;

    @BeforeEach
    void setUp() {
        requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        httpClient = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        portosyncApiClient = new PortosyncApiClient(httpClient, "api-key");
    }

    @Test
    void givenYearAndStockMarket_whenGetHolidaysByYearAndExchange_thenReturnHolidaysByYearAndExchange() throws IOException, InterruptedException {
        String url = MARKET_CALENDAR_API_URL + "NYSE/holidays/2025";
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

        Set<LocalDate> holidays = portosyncApiClient.getHolidaysByYearAndExchange(2025, StockMarket.NYSE);

        verify(httpClient).send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString()));

        assertThat(requestCaptor.getValue().uri()).hasToString(url);
        assertThat(requestCaptor.getValue().headers().firstValue(HEADER_API_KEY).orElse(null)).hasToString("api-key");
        assertThat(holidays).containsAll(
                Set.of(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 26),
                        LocalDate.of(2025, 12, 25)
                )
        );
    }

    @Test
    void givenYearAndStockMarket_whenGetHolidaysByYearAndExchange_ByYearAndExchange_shouldThrowException() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        Throwable thrown = Assertions.catchThrowable(() -> portosyncApiClient.getHolidaysByYearAndExchange(2025, StockMarket.NYSE));

        assertThat(thrown).isInstanceOf(PortosyncApiClientException.class).hasMessageContaining("400");
    }

    @Test
    void givenYearAndStockMarket_whenGetHolidaysForCurrentYearByExchange_thenReturnHolidaysOfExchangeForCurrentYear() throws IOException, InterruptedException {
        String url = MARKET_CALENDAR_API_URL + "NYSE/holidays";
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

        Set<LocalDate> holidays = portosyncApiClient.getHolidaysForCurrentYearByExchange(StockMarket.NYSE);

        verify(httpClient).send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString()));

        assertThat(requestCaptor.getValue().uri()).hasToString(url);
        assertThat(requestCaptor.getValue().headers().firstValue(HEADER_API_KEY).orElse(null)).hasToString("api-key");

        assertThat(holidays).containsAll(
                Set.of(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 26),
                        LocalDate.of(2025, 12, 25)
                )
        );
    }

    @Test
    void givenYearAndStockMarket_whenGetHolidaysForCurrentYearByExchange_ByYearAndExchange_shouldThrowException() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        Throwable thrown = Assertions.catchThrowable(() -> portosyncApiClient.getHolidaysForCurrentYearByExchange(StockMarket.NYSE));

        assertThat(thrown).isInstanceOf(PortosyncApiClientException.class).hasMessageContaining("400");
    }

    @Test
    void givenLastRebalancingDateAndFrequency_whenGetNextRebalancingDate_thenReturnNextRebalancingDate() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                        {
                          "nextRebalancingDate": "2025-12-26"
                        }
                        """);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        LocalDate nextRebalancingDate = portosyncApiClient.getNextRebalancingDate(LocalDate.of(2025, 11, 26), RebalancingFrequency.MONTHLY);

        verify(httpClient).send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString()));
        HttpRequest request = requestCaptor.getValue();
        assertThat(nextRebalancingDate).isEqualTo(LocalDate.of(2025, 12, 26));
        assertThat(request.uri().toString()).contains(REBALANCING_DATE_API_URL + "next");
        assertThat(request.uri())
                .hasParameter("previousRebalancingDate", "2025-11-26")
                .hasParameter("frequency", "MENSUEL");
        assertThat(request.headers().firstValue(HEADER_API_KEY).orElse(null)).isEqualTo("api-key");
    }

    @Test
    void givenStartRebalancingDateAndFrequency_whenGetRebalancingCalendar_thenReturnNextYearRebalancingDates() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                        {
                          "rebalancingDates": [
                            "2025-01-26",
                            "2025-04-26",
                            "2025-07-26",
                            "2025-10-26"
                          ]
                        }
                        """);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        Set<LocalDate> calendar = portosyncApiClient
                .getRebalancingCalendar(LocalDate.of(2025, 1, 26), RebalancingFrequency.QUARTERLY);

        verify(httpClient).send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString()));
        HttpRequest request = requestCaptor.getValue();
        assertThat(calendar).containsAll(
                Set.of(
                        LocalDate.of(2025, 1, 26),
                        LocalDate.of(2025, 4, 26),
                        LocalDate.of(2025, 7, 26),
                        LocalDate.of(2025, 10, 26)
                )
        );
        assertThat(request.uri().toString()).contains(REBALANCING_DATE_API_URL + "calendar");
        assertThat(request.uri())
                .hasParameter("startRebalancingDate", "2025-01-26")
                .hasParameter("frequency", "TRIMESTRIEL");
        assertThat(request.headers().firstValue(HEADER_API_KEY).orElse(null)).isEqualTo("api-key");
    }
}
