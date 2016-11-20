/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.utils.exceptions;

import com.goeuro.hiring.devtest.busroute.utils.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom validation error codes, error messages and potential corrective actions 
 * to be used while validating the Bus Route Data File Format.
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
@AllArgsConstructor
@Getter
public enum FileContentInvalidErrorType {

    /** Route Specific Validation Scenario Messages */
    ROUTES_COUNT_MISMATCH(
            1000,
            "Mismatch in specifed routes count in the header and actual route information available in the data file.",
            "Make sure that specifed routes count matches to the actual route information available."),

    DUPLICATE_ROUTE_IDS_FOUND(
            1001,
            "Duplicate route IDs found in the data file.",
            "Make sure that each route has an unique Id."),

    MAX_ROUTES_EXCEEDED(
            1002,
            "Maximum number of routes exceeded the allowed limit.",
            String.format("Make sure that maximum number of shouldn't exceeds %s.", Constants.MAX_ROUTES)),

    /** Station Specific Validation Scenario Messages */
    MAX_UNIQUE_STATIONS_OVERALL_EXCEEDED(
            1003,
            "Maximum number of overall unique stations exceeded the allowed limit.",
            String.format("Make sure that maximum number of unique stations overall shouldn't exceeds %s.", Constants.MAX_OVERALL_UNIQUE_STATIONS)),

    /** Routes with Station Validation Scenario Messages */
    INSUFFICIENT_STATIONS_PER_ROUTE(
            1004,
            "Insufficient number of stations provided per route in the data file.",
            "Make sure that atleast 2 stations per route is provided."),

    DUPLICATE_STATIONS_FOUND_IN_SINGLE_ROUTE(
            1005,
            "Route informaton has duplicate stations provided in the data file.",
            "Make sure that all the stations provided per route is unique."),

    MAX_STATIONS_PER_ROUTE_EXCEEDED(
            1006,
            "Maximum number of stations per route exceeded the allowed limit.",
            String.format("Make sure that maximum number of stations per route shouldn't exceeds %s.", Constants.MAX_STATIONS_PER_ROUTE));

    private final Integer errorCode;
    private final String errorMessage;
    private final String correctiveAction;

    /**
     * @return
     */
    public String getConsolidatedMessage() {
        return String.format("Error Code: %s, Message: %s, Corrective Action: %s.",
                this.errorCode,
                this.errorMessage,
                this.correctiveAction);
    }

}
