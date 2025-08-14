package io.github.joachimdi.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.joachimdi.dto.GetHolidaysResponse;
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.exceptions.PortosyncApiClientException;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Set;

public class PortosyncApiClient {

    private static final String PORTOSYNC_API_URL = "https://portosync.ovh/api/market-calendar/";

    private static final String PARAM_STOCK_MARKET = "stockMarket";

    private static final String PARAM_YEAR = "year";

    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;

    /**
     * Constructor with an httpClient
     * @param httpClient your httpClient initialized and configured as you want
     */
    public PortosyncApiClient(HttpClient httpClient) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.httpClient = httpClient;
    }

    /**
     * Get holidays of the stock market for a given year
     * @param year targetting year
     * @param stockMarket targetting stock market
     * @return Set of closing dates for the stock market
     */
    public Set<LocalDate> getHolidays(Integer year, StockMarket stockMarket) throws URISyntaxException, IOException, InterruptedException {
        checkMandatoryParameters(year, stockMarket);
        URI getHolidaysURI = new URIBuilder(PORTOSYNC_API_URL + "holidays")
                .addParameter(PARAM_STOCK_MARKET, stockMarket.name())
                .addParameter(PARAM_YEAR, year.toString())
                .build();
        HttpRequest request = HttpRequest.newBuilder(getHolidaysURI).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        checkResponseStatus(response);
        GetHolidaysResponse holidays = objectMapper.readValue(response.body(), GetHolidaysResponse.class);
        return holidays.closingDates();
    }

    private static void checkResponseStatus(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            String message = "An error occurred while calling portosync api" + "\n" +
                    "Status: " + response.statusCode() + "\n" +
                    "Body: " + response.body() + "\n";
            throw new PortosyncApiClientException(message);
        }
    }

    private static void checkMandatoryParameters(Object... parameters) {
        for (Object parameter : parameters) {
            if (parameter == null) {
                throw new PortosyncApiClientException("Missing required parameter");
            }
        }
    }
}
