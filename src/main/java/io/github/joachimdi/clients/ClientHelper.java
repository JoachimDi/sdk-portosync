package io.github.joachimdi.clients;

import io.github.joachimdi.exceptions.PortosyncApiClientException;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;

class ClientHelper {

    private ClientHelper(){}

    static URI buildUri(String base, Map<String, String> params) {
        try {
            URIBuilder builder = new URIBuilder(base);
            params.forEach(builder::addParameter);
            return builder.build();
        } catch (URISyntaxException e) {
            throw new PortosyncApiClientException("Invalid URI: " + base, e);
        }
    }

    static void checkResponseStatus(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new PortosyncApiClientException(
                    "Error calling Portosync API\n" +
                            "Status: " + response.statusCode() + "\n" +
                            "Body: " + response.body()
            );
        }
    }
}
