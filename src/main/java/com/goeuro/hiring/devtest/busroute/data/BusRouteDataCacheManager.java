/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.goeuro.hiring.devtest.busroute.utils.Constants;
import com.goeuro.hiring.devtest.busroute.utils.exceptions.FileContentInvalidErrorType;
import com.goeuro.hiring.devtest.busroute.utils.exceptions.InvalidDataFileException;


/**
 * This class checks the sanity of the given input file and manages its content in the in-memory data cache
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@Component
public class BusRouteDataCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusRouteDataCacheManager.class.getName());

    private static final Pattern WHITE_SPACE_REGEX_PATTERN = Pattern.compile("\\s+");   /* To separate routes & stations */

    @Autowired
    private BusRouteDataCache dataCache;

    /**
     * The following tasks is being performed in sequence
     * (1) Verify the given data file path
     * (2) Validate the content against the specification
     * (3) Initialize the in-memory data cache
     *
     * @param dataFilePath
     * @throws IOException
     */
    public void validateDataFileAndInitializeCache(final String dataFilePath)
        throws IOException {

        /* Checks to verify if the file is available/readable */
        verifyDataFilePath(dataFilePath);

        /* Input data file will be read from disk only once and kept in the memory for further use.
         * The input file with 100K routes with 1000 stations per route can fit into the memory */
        final List<String> lines = Files.lines(Paths.get(dataFilePath)).map(line -> line.trim()).collect(Collectors.toList());

        /* Checks to validate if the file has content as per the specification */
        validateDataFileContent(lines);

        /* Parse the file content and load it to the in-memory cache */
        initializeDataCache(lines);
    }

    /**
     * @param lines List of lines present in the input data file
     */
    private void validateDataFileContent(final List<String> lines) {

        final long totalRoutesGiven = lines.stream().findFirst().map(Long::valueOf).get(); /* Total routes specified in header */

        /* Route Specific Validations */
        validateRoutesCountMismatch(lines, totalRoutesGiven);       /* Total routes specified in header line vs Actual routes available check */
        validateDuplicateRouteIds(lines, totalRoutesGiven);         /* Duplicate route IDs check */
        validateMaxRoutesExceeded(totalRoutesGiven);                /* Maximum number of routes allowed check */

        /* Station Specific Validations */
        validateMaxUniqueStationsOverallExceeded(lines);            /* Maximum unique stations allowed across all routes check  */

        /* Stations associated with Routes Validations */
        validateInsufficientStationsPerRoute(lines);                /* Route information with sufficient stations check */
        validateRoutesWithDuplicateStations(lines);                 /* Duplicate station IDs in the same route check */
        validateMaxStationsPerRouteExceeded(lines);                 /* Max stations allowed per route check */
    }

    /**
     * Check if there is any mismatch in the given total routes in the header and the actual routes present.
     * 
     * @param lines            List of lines present in the input data file
     * @param totalRoutesGiven Total routes specified in the header line
     * @return
     */
    private void validateRoutesCountMismatch(final List<String> lines,
                                             final long totalRoutesGiven) {
        final long totalRoutesActual = lines.stream().filter(line -> !StringUtils.isEmpty(line)).skip(1).count();

        if (totalRoutesActual != totalRoutesGiven) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.ROUTES_COUNT_MISMATCH);
        }
    }

    /**
     * Check if duplicate route IDs are present.
     * 
     * @param lines             List of lines present in the input data file
     * @param totalRoutesGiven  Total routes specified in the header line
     */
    private void validateDuplicateRouteIds(final List<String> lines,
                                           final long totalRoutesGiven) {
        final Set<Integer> uniqueRouteIds = lines.stream()
                .filter(line -> !StringUtils.isEmpty(line))
                .skip(1)
                .map(line -> WHITE_SPACE_REGEX_PATTERN.split(line)[0])
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        if (uniqueRouteIds.size() != totalRoutesGiven) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.DUPLICATE_ROUTE_IDS_FOUND);
        }
    }

    /**
     * Check if the total number of given routes exceeds the allowed limit.
     * 
     * @param totalRoutesGiven  Total routes specified in the header line
     */
    private void validateMaxRoutesExceeded(final long totalRoutesGiven) {
        if (totalRoutesGiven > Constants.MAX_ROUTES) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.MAX_ROUTES_EXCEEDED);
        }
    }

    /**
     * Check if total number of given unique stations across all the routes exceed the allowed limit.
     * 
     * @param lines List of lines present in the input data file
     */
    private void validateMaxUniqueStationsOverallExceeded(final List<String> lines) {
        final Set<Integer> uniqueStationIds = lines.stream()
                .filter(line -> !StringUtils.isEmpty(line))
                .skip(1)
                .map(line -> WHITE_SPACE_REGEX_PATTERN.splitAsStream(line).skip(1).map(Integer::valueOf).collect(Collectors.toList()))
                .flatMap(x -> x.stream())
                .collect(Collectors.toSet());

        if (uniqueStationIds.size() > Constants.MAX_OVERALL_UNIQUE_STATIONS) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.MAX_UNIQUE_STATIONS_OVERALL_EXCEEDED);
        }
    }

    /**
     * Check if any route information doesn't contain the enough stations
     *
     * @param lines List of lines present in the input data file
     */
    private void validateInsufficientStationsPerRoute(final List<String> lines) {
        boolean routeInfoFormatIncorrect = lines.stream()
                .filter(line -> !StringUtils.isEmpty(line))
                .skip(1)
                .map(line -> WHITE_SPACE_REGEX_PATTERN.splitAsStream(line).map(Integer::valueOf).collect(Collectors.toList()))
                .anyMatch(routeInfo -> (routeInfo.size() < 3));

        if (routeInfoFormatIncorrect) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.INSUFFICIENT_STATIONS_PER_ROUTE);
        }
    }

    /**
     *  Check if any duplicate stations are provided in the same route.
     *
     * @param lines List of lines present in the input data file
     */
    private void validateRoutesWithDuplicateStations(final List<String> lines) {
        boolean duplicateStationIdsPresent = lines.stream()
                .filter(line -> !StringUtils.isEmpty(line))
                .skip(1)
                .map(line -> WHITE_SPACE_REGEX_PATTERN.splitAsStream(line).skip(1).map(Integer::valueOf).collect(Collectors.toList()))
                .anyMatch(stationIds -> (stationIds.size() != (new HashSet<Integer>(stationIds)).size()));

        if (duplicateStationIdsPresent) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.DUPLICATE_STATIONS_FOUND_IN_SINGLE_ROUTE);
        }
    }

    /**
     * Check if total number of given stations in one or more routes exceeds the allowed limit.
     *
     * @param lines List of lines present in the input data file
     */
    private void validateMaxStationsPerRouteExceeded(final List<String> lines) {
        boolean maxStationsPerRouteExceeds = lines.stream()
                .filter(line -> !StringUtils.isEmpty(line))
                .skip(1)
                .map(line -> WHITE_SPACE_REGEX_PATTERN.splitAsStream(line).skip(1).map(Integer::valueOf).collect(Collectors.toList()))
                .anyMatch(routeInfo -> (routeInfo.size() > Constants.MAX_STATIONS_PER_ROUTE));

        if (maxStationsPerRouteExceeds) {
            throw new InvalidDataFileException(FileContentInvalidErrorType.MAX_STATIONS_PER_ROUTE_EXCEEDED);
        }
    }

    /**
     * @param lines List of lines present in the input data file
     */
    private void initializeDataCache(final List<String> lines) {
        lines.stream()
                .map(line -> WHITE_SPACE_REGEX_PATTERN.split(line))
                .map(BusRouteDataCacheManager::convertAndCollectIntRouteInfo)
                .forEach(routeInfo -> populateDataCahce(routeInfo, dataCache));
    }

    /**
     * Convert each element given in the route information line into Integer and collect it into a list
     *
     * @param routeElements Array of elements present in the route information line
     * @return List of Integers parsed from route information line
     */
    private static List<Integer> convertAndCollectIntRouteInfo(final String[] routeElements) {
        return Arrays.stream(routeElements)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * Populate the Data Cache with the stations and its connections.
     *
     * @param routeInfo Route ID + Stations List
     * @param dataCache
     */
    private static void populateDataCahce(final List<Integer> routeInfo,
                                          final BusRouteDataCache dataCache) {

        /* Fetch the route ID from the first element of the route information line */
        final Integer routeId = Integer.valueOf(routeInfo.get(0));

        /* Fetch the station IDs from second element onwards till the end of the line */
        final List<Integer> routeStationIds = routeInfo.subList(1, routeInfo.size());

        /* Using IntStream to have the index which requires to fetch the directly connected stations for an iterated station */
        IntStream.range(0, routeStationIds.size()).forEach(
                index -> updateConnectedStationsForEachStationInCache(index, routeStationIds, routeId, dataCache)
                );
    }

    /**
     * For each station, the connected stations will be determined and updated in the data cache along with the route ID.
     *
     * @param index
     * @param routeStationIds
     * @param routeId
     * @param dataCache
     */
    private static void updateConnectedStationsForEachStationInCache(final int index,
                                                                     final List<Integer> routeStationIds,
                                                                     final Integer routeId,
                                                                     final BusRouteDataCache dataCache) {

        final Integer currentStationId = routeStationIds.get(index);

        /* Fetch the connected stations for the current station */
        final List<Integer> connectedStationIds = routeStationIds.subList(index + 1, routeStationIds.size());

        /* Update all the extracted information into the data cache */
        dataCache.updateCache(routeId, currentStationId, connectedStationIds);
    }

    /**
     * Checks to verify if the file is available/readable.
     *
     * @param busRouteDataFilePath
     * @return
     * @throws IOException
     */
    private void verifyDataFilePath(final String busRouteDataFilePath)
        throws IOException {

        final File busRouteDataFile = new File(busRouteDataFilePath);

        /* Checks if the given path exists and its a file */
        if (!busRouteDataFile.exists() || busRouteDataFile.isDirectory()) {
            final String errorMessage = String.format("Provided bus route data file '%s' doesn't exists.",
                    busRouteDataFile.getCanonicalPath());
            LOGGER.error(errorMessage);
            throw new FileNotFoundException(errorMessage);
        }

        /* Checks if the given file is readable */
        if (!busRouteDataFile.canRead()) {
            final String errorMessage = String.format("Provided bus route data file '%s' is not readable.",
                    busRouteDataFile.getCanonicalPath());
            LOGGER.error(errorMessage);
            throw new SecurityException(errorMessage);
        }

    }

    /**
     * @return
     */
    public BusRouteDataCache getDataCache() {
        return dataCache;
    }

}

