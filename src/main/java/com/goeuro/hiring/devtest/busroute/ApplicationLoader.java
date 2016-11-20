/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.goeuro.hiring.devtest.busroute.data.BusRouteDataCacheManager;

/**
 * Application Loader(Spring Boot) for the GoEuro Java Developer Test(Bus Route Challenge).
 * It will fail to start if the given bus route data file is not conform to the specification
 * along with the corresponding error details.
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 *
 * @see https://github.com/goeuro/challenges/tree/master/bus_route_challenge
 */

@SpringBootApplication
public class ApplicationLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLoader.class.getName());

    @Autowired
    private BusRouteDataCacheManager cacheManager;

    /**
     * This will be used during the integration tests since there is no direct
     * way to send the command line arguments in Spring Boot at this moment. This
     * This property will be configured during the normal application/server run
     * with the given first command line argument using '--bus-route-data-file-path' option
     */
    @Value("${bus-route-data-file-path}")
    private String busRouteDataFilePath;

    /**
     * @param args
     */
    public static void main(String[] args) {
        LOGGER.info("Attempting to load the bus route challenge application with '{}' args.", args.length);

        if (args.length != 1) {
            LOGGER.error("Required number arguments are not provided. Args Count is: {}", args.length);
            throw new IllegalArgumentException("This application requires bus route data file path as an argument.");
        }

        args[0] = String.format("--bus-route-data-file-path=%s", args[0]);
        SpringApplication.run(ApplicationLoader.class, args);
    }

    /**
     * @throws IOException
     */
    public void run(String... args) throws IOException {
        /* Verify, Validate the given input file path and initialize the data cache */
        cacheManager.validateDataFileAndInitializeCache(busRouteDataFilePath);
    }
}
