/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
  * Unit tests for @see com.goeuro.hiring.devtest.busroute.data.BusRouteDataCache method APIs
  *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
public class DataCacheTests {

    private BusRouteDataCache dataCache;

    @Before
    public void setUp() {
        dataCache = new BusRouteDataCache();
    }

    /**
     * Scenario: No routes/stations are added to the cache
     */
    @Test
    public void testUpdateCacheWithNoData() {
        assertThat(dataCache.isDepartureStationIdExists(1)).isFalse();
        assertThat(dataCache.isArrivalStationIdConnected(1, 2)).isFalse();
        assertThat(dataCache.getConnectedRoutedIds(1, 2).size()).isEqualTo(0);
    }

    /**
     * Scenario: Minimal data (Single route with Two stations)
     *
     */
    @Test
    public void testUpdateCacheWithSingleRouteDataWithTwoStations() {
        /*
         * Sample Route Data
         * 1
         * 0 1 2
         */
        dataCache.updateCache(0, 1, Arrays.asList(2));

        assertThat(dataCache.isDepartureStationIdExists(1)).isTrue();
        assertThat(dataCache.isDepartureStationIdExists(2)).isFalse();

        assertThat(dataCache.isArrivalStationIdConnected(1, 2)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(1, 2).size()).isEqualTo(1);
    }

    /**
     * Scenario: Single route with many stations
     */
    @Test
    public void testUpdateCacheWithSingleRouteDataWithManyStations() {
        /*
         * Sample Route Data
         * 1
         * 0 1 2 3
         */
        dataCache.updateCache(0, 1, Arrays.asList(2, 3));
        dataCache.updateCache(0, 2, Arrays.asList(3));

        /* Departure Station should exists for all stations except the last station because it is not connected */
        assertThat(dataCache.isDepartureStationIdExists(1)).isTrue();
        assertThat(dataCache.isDepartureStationIdExists(2)).isTrue();
        assertThat(dataCache.isDepartureStationIdExists(3)).isFalse();

        /* Check the connected stations nearby and far away along with their route IDs */
        assertThat(dataCache.isArrivalStationIdConnected(1, 2)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(1, 2)).isEqualTo(Arrays.asList(0));

        assertThat(dataCache.isArrivalStationIdConnected(1, 3)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(1, 3)).isEqualTo(Arrays.asList(0));

        assertThat(dataCache.isArrivalStationIdConnected(2, 3)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(2, 3)).isEqualTo(Arrays.asList(0));

        /* Stations shouldn't connected in reverse direction */
        assertThat(dataCache.isArrivalStationIdConnected(3, 2)).isFalse();
        assertThat(dataCache.getConnectedRoutedIds(3, 2).size()).isEqualTo(0);

        assertThat(dataCache.isArrivalStationIdConnected(3, 1)).isFalse();
        assertThat(dataCache.getConnectedRoutedIds(3, 1).size()).isEqualTo(0);
    }


    /**
     * Scenario: Two routes with two stations
     */
    @Test
    public void testUpdateCacheWithTwoRoutesDataWithTwoStations() {
        /*
         * Sample Route Data
         * 2
         * 0 1 2
         * 1 3 4
         */
        dataCache.updateCache(0, 1, Arrays.asList(2));
        dataCache.updateCache(1, 3, Arrays.asList(4));

        /* Departure Station should exists for all stations except the last station because it is not connected */
        assertThat(dataCache.isDepartureStationIdExists(1)).isTrue();
        assertThat(dataCache.isDepartureStationIdExists(2)).isFalse();
        assertThat(dataCache.isDepartureStationIdExists(3)).isTrue();
        assertThat(dataCache.isDepartureStationIdExists(4)).isFalse();

        /* Check the connected stations in both the routes */
        assertThat(dataCache.isArrivalStationIdConnected(1, 2)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(1, 2)).isEqualTo(Arrays.asList(0));

        assertThat(dataCache.isArrivalStationIdConnected(3, 4)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(3, 4)).isEqualTo(Arrays.asList(1));

        /* Check the unconnected stations in both the routes */
        assertThat(dataCache.isArrivalStationIdConnected(1, 3)).isFalse();
        assertThat(dataCache.getConnectedRoutedIds(1, 3).size()).isEqualTo(0);

        assertThat(dataCache.isArrivalStationIdConnected(2, 4)).isFalse();
        assertThat(dataCache.getConnectedRoutedIds(2, 4).size()).isEqualTo(0);

    }

   /**
    * Scenario: Multiple routes with more stations
    */
    @Test
    public void testUpdateCacheWithMultipleRoutesDataWithManyStations() {
        /*
         * Sample Route Data
         * 2
         * 0 1 2 3 4
         * 1 4 5 6 7
         * 2 6 5 1 7
         */
        /* First Route */
        dataCache.updateCache(0, 1, Arrays.asList(2, 3, 4));
        dataCache.updateCache(0, 2, Arrays.asList(3, 4));
        dataCache.updateCache(0, 3, Arrays.asList(4));

        /* Second Route */
        dataCache.updateCache(1, 4, Arrays.asList(5, 6, 7));
        dataCache.updateCache(1, 5, Arrays.asList(6, 7));
        dataCache.updateCache(1, 6, Arrays.asList(7));

        /* Third Route */
        dataCache.updateCache(2, 6, Arrays.asList(5, 1, 7));
        dataCache.updateCache(2, 5, Arrays.asList(1, 7));
        dataCache.updateCache(2, 1, Arrays.asList(7));

        /* Same departure and arrival stations  are connected in different route */
        assertThat(dataCache.isArrivalStationIdConnected(5, 7)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(5, 7)).isEqualTo(Arrays.asList(1, 2));

        /* Same departure and arrival stations are connected in both the directions in different routes */
        assertThat(dataCache.isArrivalStationIdConnected(5, 6)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(5, 6)).isEqualTo(Arrays.asList(1));

        assertThat(dataCache.isArrivalStationIdConnected(6, 5)).isTrue();
        assertThat(dataCache.getConnectedRoutedIds(6, 5)).isEqualTo(Arrays.asList(2));
    }

}
