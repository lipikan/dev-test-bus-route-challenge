/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.integration;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration test for the implemented REST End points.
 * It will load data file given in the test environment properties and use it for serving data.
 * File content is given below:
 * ----------------------------
 * 3
 * 0 0 1 2 3 4
 * 1 3 1 6 5
 * 2 0 6 4
 * ----------------------------
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"bus-route-data-file-path=src/test/resources/bus-route-data/reference-data"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DirectBusRouteSearchEndPointTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Scenario: Given Departure and Arrival Stations are directly connected.
     * Test the complete response body structure along with the HTTP Status Code.
     */
    @Test
    public void testDirectlyConnectedRoutes() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=6&arr_sid=4", port);
        final ResponseEntity<Map> response = testRestTemplate.getForEntity(apiUrl, Map.class);

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        then(response.getBody().get("dep_sid")).isEqualTo(6);
        then(response.getBody().get("arr_sid")).isEqualTo(4);
        then(response.getBody().get("direct_bus_route")).isEqualTo(true);
    }

    /**
     * Scenario: Given Departure and Arrival Stations are not directly connected.
     * Test the complete response body structure along with the HTTP Status Code.
     */
    @Test
    public void testRoutesNotDirectlyConnected() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=2&arr_sid=5", port);
        final ResponseEntity<Map> response = testRestTemplate.getForEntity(apiUrl, Map.class);

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        then(response.getBody().get("dep_sid")).isEqualTo(2);
        then(response.getBody().get("arr_sid")).isEqualTo(5);
        then(response.getBody().get("direct_bus_route")).isEqualTo(false);
    }

    /**
     * Scenario: Given Departure and Arrival Station IDs are not provided in the input data file.
     * Test the complete response body structure along with the HTTP Status Code.
     */
    @Test
    public void testDepartureAndArrivalStationIdsNotExists() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=7&arr_sid=8", port);
        final ResponseEntity<Map> response = testRestTemplate.getForEntity(apiUrl, Map.class);

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        then(response.getBody().get("dep_sid")).isEqualTo(7);
        then(response.getBody().get("arr_sid")).isEqualTo(8);
        then(response.getBody().get("direct_bus_route")).isEqualTo(false);
    }

    /**
     * Scenario: Given Departure Station IDs is not provided in the input data file.
     * Test the complete response body structure along with the HTTP Status Code.
     */
    @Test
    public void testDepartureStationIdNotExists() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=7&arr_sid=6", port);
        final ResponseEntity<Map> response = testRestTemplate.getForEntity(apiUrl, Map.class);

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        then(response.getBody().get("dep_sid")).isEqualTo(7);
        then(response.getBody().get("arr_sid")).isEqualTo(6);
        then(response.getBody().get("direct_bus_route")).isEqualTo(false);
    }

    /**
     * Scenario: Given Arrival Station ID is not provided in the input data file.
     * Test the complete response body structure along with the HTTP Status Code.
     */
    @Test
    public void testArrivalStationIdNotExists() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=2&arr_sid=8", port);
        final ResponseEntity<Map> response = testRestTemplate.getForEntity(apiUrl, Map.class);

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        then(response.getBody().get("dep_sid")).isEqualTo(2);
        then(response.getBody().get("arr_sid")).isEqualTo(8);
        then(response.getBody().get("direct_bus_route")).isEqualTo(false);
    }

    /**
     * Scenario: Only HTTP GET and HEAD should be allowed on the implemented REST end point
     * Test the HTTP Status Methods.
     */
    @Test
    public void testOnlyGetAndHeadHttpMethodAllowed() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=6&arr_sid=4", port);
        final Set<HttpMethod> allowedHttpMethods = testRestTemplate.optionsForAllow(apiUrl);
        then(allowedHttpMethods.size()).isEqualTo(2);
        then(allowedHttpMethods).contains(HttpMethod.GET);
        then(allowedHttpMethods).contains(HttpMethod.HEAD);
    }

    /**
     * Scenario: Given Departure and Arrival Station IDs are empty.
     * Test the HTTP Status Code.
     */
    @Test
    public void testQueryStringInputWithEmptyData() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=&arr_sid=", port);
        then(testRestTemplate.getForEntity(apiUrl, Map.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Scenario: Given Departure Station ID is empty.
     * Test the HTTP Status Code.
     */
    @Test
    public void testEmptyDepartureId() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=&arr_sid=5", port);
        then(testRestTemplate.getForEntity(apiUrl, Map.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Scenario: Given Arrival Station ID is empty.
     * Test the HTTP Status Code.
     */
    @Test
    public void testEmptyArrivalId() {
        final String apiUrl = String.format("http://localhost:%s/api/direct?dep_sid=&arr_sid=5", port);
        then(testRestTemplate.getForEntity(apiUrl, Map.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Scenario: Both Departure and Arrival Station IDs are not provided.
     * Test the HTTP Status Code.
     */
    @Test
    public void testNoQueryStringInput() {
        final String apiUrl = String.format("http://localhost:%s/api/direct", port);
        then(testRestTemplate.getForEntity(apiUrl, Map.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
