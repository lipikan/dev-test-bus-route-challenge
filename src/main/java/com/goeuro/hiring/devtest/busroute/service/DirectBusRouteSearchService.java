/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goeuro.hiring.devtest.busroute.data.BusRouteDataCache;
import com.goeuro.hiring.devtest.busroute.data.BusRouteDataCacheManager;

/**
 * Service implementation to check if the two given stations are directly connected or not.
 * It uses the data loaded during the application/server start.
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@Service
public class DirectBusRouteSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectBusRouteSearchService.class.getName());

    @Autowired
    private BusRouteDataCacheManager cacheManager;

    /**
     * Checks if the given Departure Station and Arrival Station are directly connected.
     * If it is connected, it will log the route IDs which connects these two stations(Not provided in the requirement.
     * However implemented for possible requirements extensions and troubleshooting.)
     *
     * @param departureStationId
     * @param arrivalStationId
     * @return true if the stations are directly connected.
     */
    public boolean isDirectBusRouteExists(final Integer departureStationId,
                                          final Integer arrivalStationId) {

        final BusRouteDataCache dataCache = cacheManager.getDataCache();

        /* Check if the Departure Station is available */
        if (dataCache.isDepartureStationIdExists(departureStationId)) {

            /* Check if both Departure and Arrival Stations are directly connected in at lease one of the routes */
            if (dataCache.isArrivalStationIdConnected(departureStationId, arrivalStationId)) {

                /* log the route IDs which connects these two stations */
                LOGGER.info("Departure Station ID {} is connected to the Arrival Station ID {} in {} Routes.",
                        departureStationId,
                        arrivalStationId,
                        dataCache.getConnectedRoutedIds(departureStationId, arrivalStationId));

                return true;
            }
        }

        LOGGER.info("None of the routes directly connect Departure Station ID {} with Arrival Station ID {}.",
                departureStationId,
                arrivalStationId);

        return false; /* Given Stations either doesn't exists of not directly connected */
    }

}
