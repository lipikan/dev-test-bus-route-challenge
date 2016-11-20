/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.goeuro.hiring.devtest.busroute.data.BusRouteDataCache;
import com.goeuro.hiring.devtest.busroute.data.BusRouteDataCacheManager;

/**
 * Unit tests for @see com.goeuro.hiring.devtest.busroute.service.DirectBusRouteSearchService method APIs
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
public class RouteSearchServiceTests {

    @InjectMocks
    private DirectBusRouteSearchService searchService;

    @Mock
    private BusRouteDataCache dataCache;

    @Mock
    private BusRouteDataCacheManager dataCacheManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Given Departure and Arrival Stations are directly connected.
     */
    @Test
    public void testSearchDirectBusRouteExists() {
        when(dataCacheManager.getDataCache()).thenReturn(dataCache);

        when(dataCache.isDepartureStationIdExists(1)).thenReturn(true);
        when(dataCache.isArrivalStationIdConnected(1, 2)).thenReturn(true);
        when(dataCache.getConnectedRoutedIds(1, 2)).thenReturn(Arrays.asList(0, 1)); /* To log all the routes connect these 2 stations */

        /* Check for the bus stations where direct connection exists */
        assertThat(searchService.isDirectBusRouteExists(1, 2)).isTrue();
    }

    /**
     * (1) Given Arrival Station ID doesn't present in the bus route data file.
     * (2) The given Departure and Arrival Stations are not directly connected.
     */
    @Test
    public void testSearchWhenArrivalStationIdDoesNotExists() {
        when(dataCacheManager.getDataCache()).thenReturn(dataCache);

        when(dataCache.isDepartureStationIdExists(1)).thenReturn(true);
        when(dataCache.isArrivalStationIdConnected(1, 2)).thenReturn(false);

        assertThat(searchService.isDirectBusRouteExists(1, 2)).isFalse();
    }

    /**
     * Given Departure Station ID doesn't present in the bus route data file.
     */
    @Test
    public void testSearchWhenDepatureStationIdDoesNotExists() {
        when(dataCacheManager.getDataCache()).thenReturn(dataCache);

        when(dataCache.isDepartureStationIdExists(1)).thenReturn(false);

        assertThat(searchService.isDirectBusRouteExists(1, 2)).isFalse();
    }

}
