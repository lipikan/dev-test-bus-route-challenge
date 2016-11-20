/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class representation of JSON response to be sent.
 * 
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@AllArgsConstructor
@Getter
public class DirectBusRouteSearchResponse {

    @JsonProperty("dep_sid")
    private final Integer departureStationId;

    @JsonProperty("arr_sid")
    private final Integer arrivalStationId;

    @JsonProperty("direct_bus_route")
    private final Boolean directBusRouteExists;

}
