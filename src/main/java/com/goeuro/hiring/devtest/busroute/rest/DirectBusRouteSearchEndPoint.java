/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.goeuro.hiring.devtest.busroute.service.DirectBusRouteSearchService;

/**
 * REST End Point for Bus Route Search API.
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@RestController
@RequestMapping("/api")
public class DirectBusRouteSearchEndPoint {

    @Autowired
    private DirectBusRouteSearchService searchService;

    /**
     * API to check if the given Departure Station ID and Arrival Station ID are directly connected.
     * It will accepts only GET/HEAD HTTP methods and 
     * It will return 200 OK if the input validation succeeds else 400 Bad Request will be sent.
     *
     * @param departureStationId
     * @param arrivalStationId
     * @return JSON response with the given Departure Station ID, Arrival Station ID and a flag to indicate if they are connected.
     */
    @RequestMapping(value = "/direct", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody DirectBusRouteSearchResponse isDirectBusRouteExists(
            @RequestParam(value = "dep_sid", required = true) final int departureStationId,
            @RequestParam(value = "arr_sid", required = true) final int arrivalStationId) {

        /* Call the Search Service to check if the stations are connected */
        final boolean directBusRouteExists = searchService.isDirectBusRouteExists(departureStationId, arrivalStationId);

        return new DirectBusRouteSearchResponse(departureStationId, arrivalStationId, directBusRouteExists);
    }
}
