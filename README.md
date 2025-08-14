# SDK PORTOSYNC

Un SDK Java simple et efficace pour récupérer les jours de fermeture officiels des marchés financiers **Euronext Paris** et **NYSE** via notre API **Portosync**.

## Installation

Ajoutez la dépendance dans votre pom.xml (Maven Central ou votre repository privé) :

```xml
<dependency>
    <groupId>io.github.joachimdi</groupId>
    <artifactId>sdk-portosync</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

Avant utilisation, initialisez une instance du client avec en paramètre une instance de `httpClient` configurée comme vous le souhaitez :

```java
import io.github.joachimdi.clients.PortosyncApiClient;

import java.net.http.HttpClient;

HttpClient httpClient = HttpClient.newHttpClient();
PortosyncApiClient portosyncApiClient = new PortosyncApiClient(httpClient);
```

## Utilisation

1. Récupérer les jours fériés du marché **Euronext Paris**

```java
import io.github.joachimdi.enums.StockMarket;

import java.time.LocalDate;
import java.util.Set;

Set<LocalDate> holidays = portosyncApiClient.getHolidays(StockMarket.EURONEXT_PARIS, 2025);
```
`StockMarket` est une enum présente dans le SDK qui donne la liste des marchés financiers disponibles.

2. Récupérer les jours fériés du marché **NYSE** 

```java
import io.github.joachimdi.enums.StockMarket;

import java.time.LocalDate;
import java.util.Set;

Set<LocalDate> holidays = portosyncApiClient.getHolidays(StockMarket.NYSE, 2025);
```