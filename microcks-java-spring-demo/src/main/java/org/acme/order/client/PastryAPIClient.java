package org.acme.order.client;

import org.acme.order.client.model.Pastry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PastryAPIClient {
    private final RestTemplate restTemplate;

    @Autowired
    public PastryAPIClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .rootUri("http://localhost:8585/rest/API+Pastry+-+2.0/2.0.0")
                .build();
    }

    public List<Pastry> listPastries() {
        ResponseEntity<List<Pastry>> response = restTemplate.exchange(
                "/pastry",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Pastry>>() {}
        );
        return response.getBody();
    }

    public Pastry getPastryByName(String name) {
        return restTemplate.getForObject("/pastry/{name}", Pastry.class, name);
    }
}
