/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.goeuro.hiring.devtest.busroute.utils.exceptions.InvalidDataFileException;

/**
 * Unit tests for @see com.goeuro.hiring.devtest.busroute.data.BusRouteDataCacheManager method APIs
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
public class DataCacheManagerTests {

    final String TEST_DATA_FILE_DIR = "src/test/resources/bus-route-data/"; /* Base directory where all the test bus route data files are present */

    @InjectMocks
    private BusRouteDataCacheManager dataCacheManager;

    @Mock
    private BusRouteDataCache dataCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Scenario: Given input file doesn't exists.
     *
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testDataFileNotExists() throws IOException {
        dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "00.does-not-exists");
    }

    /**
     * Scenario: Given input file path is a directory.
     * 
     * @throws IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testDataFileIsDirectory() throws IOException {
        dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR);
    }

    /**
     * Scenario: Mismatch in the given total routes in the header and the actual routes present.
     * Test the error code for this scenario along with the expected Exception Type.
     * 
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithRoutesCountMismatch() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "01.mismatch-given-and-actual-routes-count");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1000);
            throw e;
        }
    }

    /**
     * Scenario: Duplicate route IDs present.
     * Test the error code for this scenario along with the expected Exception Type.
     *
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithDuplicateRouteIds() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "02.duplicate-route-ids");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1001);
            throw e;
        }
    }

    /**
     * Scenario: Total number of given routes exceeds the allowed limit.
     * Test the error code for this scenario along with the expected Exception Type.
     *
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithMaxRoutesExceeded() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "03.above-maximum-routes");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1002);
            throw e;
        }
    }

    /**
     * Scenario: Total number of given unique stations across all the routes exceed the allowed limit.
     * Test the error code for this scenario along with the expected Exception Type.
     *
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithMaxUniqueStationsOverallExceeded() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "04.above-maximum-unique-stations");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1003);
            throw e;
        }
    }

    /**
     * Scenario: One or more route information doesn't contain the enough stations
     * Test the error code for this scenario along with the expected Exception Type.
     *
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithInsufficientStationsPerRoute() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "05.insufficient-stations-per-route");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1004);
            throw e;
        }
    }

    /**
     * Scenario: Duplicate stations are provided in the same route.
     * Test the error code for this scenario along with the expected Exception Type.
     *
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithDuplicateStationsInSingleRoute() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "06.duplicate-stations-per-route");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1005);
            throw e;
        }
    }

    /**
     * Scenario: Total number of given stations in one or more routes exceeds the allowed limit.
     * Test the error code for this scenario along with the expected Exception Type.
     * 
     * @throws IOException
     */
    @Test(expected = InvalidDataFileException.class)
    public void testDataFileWithMaxStationsPerRouteExceeds() throws IOException {
        try {
            dataCacheManager.validateDataFileAndInitializeCache(TEST_DATA_FILE_DIR + "07.above-maximum-stations-per-route");
        } catch (InvalidDataFileException e) {
            assertThat(e.getErrorType().getErrorCode()).isEqualTo(1006);
            throw e;
        }
    }

    /**
     * Scenario: Valid input data file. No validation related issues to be thrown.
     *
     * @throws IOException
     */
    @Test
    public void testCorrectReferenceData() throws IOException {
        Mockito.doNothing().when(dataCache).updateCache(anyObject(), anyObject(), anyObject());
        dataCacheManager.validateDataFileAndInitializeCache("src/test/resources/bus-route-data/reference-data");
    }

}
