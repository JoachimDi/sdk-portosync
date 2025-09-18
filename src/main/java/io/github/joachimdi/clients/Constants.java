package io.github.joachimdi.clients;

import java.time.format.DateTimeFormatter;

class Constants {

    public static final String BASE_URL = "https://portosync.ovh/api/";

    public static final String MARKET_CALENDAR_API_URL = BASE_URL + "market-calendar/";

    public static final String REBALANCING_DATE_API_URL = BASE_URL + "rebalancing-dates/";

    public static final String HEADER_API_KEY = "X-API-KEY";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Constants(){}

}
