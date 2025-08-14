# SDK PORTOSYNC

A simple and efficient Java SDK to retrieve official market closure dates for **Euronext Paris** and **NYSE** via our API **Portosync**.

## Installation

Add the dependency to your pom.xml (Maven Central ou votre repository privé) :

```xml
<dependency>
    <groupId>io.github.joachimdi</groupId>
    <artifactId>sdk-portosync</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

Before using the client, you need to initialize it with an instance of your `httpClient` configured as you want :

```java
import io.github.joachimdi.clients.PortosyncApiClient;

import java.net.http.HttpClient;

HttpClient httpClient = HttpClient.newHttpClient();
PortosyncApiClient client = new PortosyncApiClient(httpClient);
```

## Usage

1. Get holidays for **Euronext Paris**

```java
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.clients.PortosyncApiClient;
import java.net.http.HttpClient;

import java.time.LocalDate;
import java.util.Set;

HttpClient httpClient = HttpClient.newHttpClient();
PortosyncApiClient client = new PortosyncApiClient(httpClient);
Set<LocalDate> holidays = client.getHolidays(StockMarket.EURONEXT_PARIS, 2025);
```
`StockMarket` is an enum provide by the SDK with the available stock markets.

2. Get holidays for **NYSE** 

```java
import io.github.joachimdi.enums.StockMarket;
import io.github.joachimdi.clients.PortosyncApiClient;
import java.net.http.HttpClient;

import java.time.LocalDate;
import java.util.Set;

HttpClient httpClient = HttpClient.newHttpClient();
PortosyncApiClient client = new PortosyncApiClient(httpClient);
Set<LocalDate> holidays = client.getHolidays(StockMarket.NYSE, 2025);
```