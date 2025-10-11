# SDK PORTOSYNC

A simple and efficient Java SDK to retrieve official market closure dates for **Euronext Paris** and **NYSE** via our API **Portosync**.

## Installation

Add the dependency to your pom.xml (Maven Central ou votre repository privé) :

```xml
<dependency>
    <groupId>io.github.joachimdi</groupId>
    <artifactId>sdk-portosync</artifactId>
    <version>1.0.2</version>
</dependency>
```

## Configuration

Before using the client, you need to initialize it with an instance of your `httpClient` configured as you want and your `api key`:

```java
import io.github.joachimdi.clients.PortosyncApiClient;

import java.net.http.HttpClient;

HttpClient httpClient = HttpClient.newHttpClient();
PortosyncApiClient client = new PortosyncApiClient(httpClient, "apik-key");
```

If you don't have specific configuration for your `httpClient`, you can initialize the api client only with your `api key` :
```java
import io.github.joachimdi.clients.PortosyncApiClient;

PortosyncApiClient portosyncApiClient = PortosyncApiClient.withDefaultHttpClient("api-key");
```

## Usage

1. Get holidays for **Euronext Paris**

```java
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.clients.PortosyncApiClient;

import java.time.LocalDate;
import java.util.Set;

PortosyncApiClient client = PortosyncApiClient.withDefaultHttpClient("api-key");
Set<LocalDate> holidays = client.getHolidaysByYearAndExchange(2025, StockMarket.EURONEXT_PARIS);
```
`StockMarket` is an enum provide by the SDK with the available stock markets.

2. Get the next rebalancing date

```java
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.clients.PortosyncApiClient;
import io.github.joachimdi.enums.RebalancingFrequency;

import java.time.LocalDate;
import java.util.Set;

PortosyncApiClient client = PortosyncApiClient.withDefaultHttpClient("api-key");
LocalDate nextRebalancingDate = client.getNextRebalancingDate(LocalDate.of(2024, 1, 1), RebalancingFrequency.MONTHLY, StockMarket.NYSE);
```
`RebalancingFrequency` is an enum provide by the SDK with the available rebalancing frequency.

3. Get the rebalancing calendar

```java
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.clients.PortosyncApiClient;
import io.github.joachimdi.enums.RebalancingFrequency;

import java.time.LocalDate;
import java.util.Set;

PortosyncApiClient client = PortosyncApiClient.withDefaultHttpClient("api-key");
LocalDate rebalancingCalendar = client.getRebalancingCalendar(LocalDate.of(2024, 1, 1), RebalancingFrequency.MONTHLY, StockMarket.NYSE);
```