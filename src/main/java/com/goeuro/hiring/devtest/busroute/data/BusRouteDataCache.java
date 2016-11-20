/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * In-memory data cache to store the stations with its connected stations.
 * It also contains basic set of APIs to query the data cache.
 * 
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@Component
public class BusRouteDataCache {

    /**
     * DATA STRUCTURE DESIGN CHOICES
     * -----------------------------
     * REQUIREMENT: 
     * -----------
     *      (1) "Quickly answer if two given stations are connected by a bus route"
     *      (2) The input data file
     *           - Loaded during the application/server start only and any changes to this file will requires a restart.
     *           - Changes weekly (which is not too often)
     *
     * GOAL:
     * -----
     *      - The search query should return the response which has the time complexity
     *        close to O(1) to provide a better UX and future scaling needs.
     * 
     * TRADE-OFFs:
     * ----------
     *      (1) Data cache population time - It should good since it is being populated only once.
     *      (2) Memory to cache data       - As per the requirement, In worst case scenario,
     *                                       it can't go beyond, 100K routes with 1000 stations each.
     *                                       And Hardware is relatively cheap.
     * 
     * OPTION (1): Map<DEPARTURE-STATION, Set<CONNECTED-STATIONS>>
     * ---------------------------------------------------------
     *              - For each station, the directly connected stations in all the routes will be added in a hash based set.
     *              - Search Time Complexity is O(1) + O(1)
     *                  - (1) Search if the Departure Station exists
     *                  - (2) Search if the Arrival Station is connected
     *
     *      - This data structure works for the current requirement. 
     *      - However, this option will not scale for potential future requirement extension.
     *      - If the connected routes are required, this data structure will not help.
     * 
     * OPTION (2): Map<DEPARTURE-STATION, Map<CONNECTED-STATION, List<CONNECTED-ROUTES>>>
     * ----------------------------------------------------------------------------------
     *               - Same as the OPTION (1) and in addition to that, all routes which connect these two stations
     *                 will be stored in a list.
     *               - Search Time Complexity is similar to OPTION (1).
     *
     * OPTION (2) is being used in this implementation to store the data in-memory
     */
    private Map<Integer, Map<Integer, List<Integer>>> dataCache = new HashMap<>();

    /**
     * Update the cache with the given station ID along with its connected stations and route Id.
     * 
     * @param routeId
     * @param stationId
     * @param connectedStationIds
     */
    public void updateCache(final Integer routeId,
                            final Integer stationId,
                            final List<Integer> connectedStationIds) {

        /* Fetch the connected stations for the given origin station ID. */
        final Map<Integer, List<Integer>> connecteStationsInfo = getConnectedStationsInfo(stationId);

        /* Add the new connected stations for the given origin station ID along with route ID */
        connectedStationIds.stream().forEach(connectedStationId -> updateConnectedStationInfo(connectedStationId, routeId, connecteStationsInfo));
    }

    /**
     * Fetch the existing connected station information for the ginen station ID.
     * 
     * @param stationId
     * @return
     */
    private Map<Integer, List<Integer>> getConnectedStationsInfo(final Integer stationId) {

        if (!dataCache.containsKey(stationId)) {
            dataCache.put(stationId, new HashMap<>());
        }

        return dataCache.get(stationId);
    }

    /**
     * Update the cache with the given station ID along with its connected stations and route Id.
     *
     * @param connectedStationId
     * @param routeId
     * @param connectedStationsInfo
     */
    private void updateConnectedStationInfo(final Integer connectedStationId,
                                            final Integer routeId,
                                            final Map<Integer, List<Integer>> connectedStationsInfo) {

        if (!connectedStationsInfo.containsKey(connectedStationId)) {
            connectedStationsInfo.put(connectedStationId, new ArrayList<>());
        }

        connectedStationsInfo.get(connectedStationId).add(routeId);
    }

    /**
     * Checks if the departure station is present in the cache
     * 
     * @param departureStationId
     * @return true if it is present.
     */
    public boolean isDepartureStationIdExists(final Integer departureStationId) {
        return dataCache.containsKey(departureStationId);
    }

    /**
     * Checks if the arrival station is added to the connection details of the departure station in the cache
     *
     * @param departureStationId
     * @param arrivalStationId
     * @return true if arrival station is present in the value of departure station key
     */
    public boolean isArrivalStationIdConnected(final Integer departureStationId,
                                               final Integer arrivalStationId) {
        if (dataCache.containsKey(departureStationId)) {
            return dataCache.get(departureStationId).containsKey(arrivalStationId);
        }

        return false;
    }

    /**
     * Returns the list of connected routes for the given departure and arrival station IDs.
     *
     * @param departureStationId
     * @param arrivalStationId
     * @return List of route IDs
     */
    public List<Integer> getConnectedRoutedIds(final Integer departureStationId,
                                               final Integer arrivalStationId) {
        if (dataCache.containsKey(departureStationId)
                && dataCache.get(departureStationId).containsKey(arrivalStationId)) {

            return dataCache.get(departureStationId).get(arrivalStationId);
        }

        return Collections.emptyList(); /* No direct route connects the given stations. */
    }

}
