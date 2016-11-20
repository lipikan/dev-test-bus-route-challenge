/**
 * Created on 2016-11-20.
 */
package com.goeuro.hiring.devtest.busroute.utils.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Exception for the Bus Route Data File Format.
 *
 * @author Sivasubramaniam Arunachalam (siva@sivaa.in)
 */
public class InvalidDataFileException extends IllegalArgumentException {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidDataFileException.class.getName());

    private static final long serialVersionUID = -3546896367105734915L;

    final FileContentInvalidErrorType errorType;

    /**
     * The consolidated message with Error Code, Error Message and Corrective Action will be printed if getMessage() is accessed.
     *
     * @param invalidType
     */
    public InvalidDataFileException(final FileContentInvalidErrorType errorType) {
        super(errorType.getConsolidatedMessage());

        this.errorType = errorType;

        LOGGER.debug(errorType.getConsolidatedMessage());
    }

    /**
     * @return the errorType
     */
    public FileContentInvalidErrorType getErrorType() {
        return errorType;
    }

}
