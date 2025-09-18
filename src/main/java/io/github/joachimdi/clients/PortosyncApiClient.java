package io.github.joachimdi.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.joachimdi.enums.RebalancingFrequency;
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.exceptions.PortosyncApiClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.github.joachimdi.clients.ClientHelper.buildUri;
import static io.github.joachimdi.clients.ClientHelper.checkResponseStatus;
import static io.github.joachimdi.clients.Constants.DATE_FORMATTER;
import static io.github.joachimdi.clients.Constants.HEADER_API_KEY;
import static io.github.joachimdi.clients.Constants.MARKET_CALENDAR_API_URL;
import static io.github.joachimdi.clients.Constants.REBALANCING_DATE_API_URL;

public final class PortosyncApiClient {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;

    public PortosyncApiClient(HttpClient httpClient, String apiKey) {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.httpClient = Objects.requireNonNull(httpClient, "HttpClient must not be null");
        this.apiKey = Objects.requireNonNull(apiKey, "API key must not be null");
    }

    /**
     * Initialize client only with apiKey
     * @param apiKey apiKey
     * @return Instance of client
     */
    public static PortosyncApiClient withDefaultHttpClient(String apiKey) {
        return new PortosyncApiClient(HttpClient.newHttpClient(), apiKey);
    }

    /**
     * Get the holidays of the stock market for the current year
     * @param stockMarket stock market targeted
     * @return the closing dates
     */
    public Set<LocalDate> getHolidaysForCurrentYearByExchange(StockMarket stockMarket) {
        Objects.requireNonNull(stockMarket, "StockMarket must not be null");
        URI uri = buildUri(MARKET_CALENDAR_API_URL + stockMarket.name() + "/holidays", Map.of());
        return sendRequest(uri, GetHolidaysResponseBody.class).closingDates();
    }

    /**
     * Get the holidays of the stock market on the wanted year
     * @param year wanted year
     * @param stockMarket targeted stock market
     * @return the closing dates
     */
    public Set<LocalDate> getHolidaysByYearAndExchange(Integer year, StockMarket stockMarket) {
        Objects.requireNonNull(year, "Year must not be null");
        Objects.requireNonNull(stockMarket, "StockMarket must not be null");
        URI uri = buildUri(MARKET_CALENDAR_API_URL + stockMarket.name() + "/holidays/" + year, Map.of());
        return sendRequest(uri, GetHolidaysResponseBody.class).closingDates();
    }

    /**
     * Get the next rebalancing date
     * @param lastRebalancingDate the last rebalancing date
     * @param frequency the frequency of your rebalancing
     * @return The date of the next rebalancing
     */
    public LocalDate getNextRebalancingDate(LocalDate lastRebalancingDate, RebalancingFrequency frequency) {
        Objects.requireNonNull(lastRebalancingDate, "Last rebalancing date must not be null");
        Objects.requireNonNull(frequency, "Frequency must not be null");

        URI uri = buildUri(
                REBALANCING_DATE_API_URL + "next",
                Map.of(
                        "previousRebalancingDate", lastRebalancingDate.format(DATE_FORMATTER),
                        "frequency", frequency.getValue()
                )
        );

        return sendRequest(uri, GetNextRebalancingDateResponseBody.class).nextRebalancingDate();
    }

    /**
     * Get the rebalancing dates for one year
     * @param startRebalancingDate the start date of your rebalancing
     * @param frequency the frequency
     * @return The dates of rebalancing
     */
    public Set<LocalDate> getRebalancingCalendar(LocalDate startRebalancingDate, RebalancingFrequency frequency) {
        Objects.requireNonNull(startRebalancingDate, "Start rebalancing date must not be null");
        Objects.requireNonNull(frequency, "Frequency must not be null");

        URI uri = buildUri(
                REBALANCING_DATE_API_URL + "calendar",
                Map.of(
                        "startRebalancingDate", startRebalancingDate.format(DATE_FORMATTER),
                        "frequency", frequency.getValue()
                )
        );

        return sendRequest(uri, GetRebalancingCalendarResponseBody.class).rebalancingDates();
    }

    private <T> T sendRequest(URI uri, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header(HEADER_API_KEY, apiKey)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponseStatus(response);

            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PortosyncApiClientException("Request failed: " + e.getMessage(), e);
        }
    }
}
