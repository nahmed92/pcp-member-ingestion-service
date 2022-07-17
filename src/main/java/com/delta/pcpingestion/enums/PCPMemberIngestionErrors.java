package com.delta.pcpingestion.enums;

import org.springframework.http.HttpStatus;

import com.deltadental.platform.common.exception.ServiceException;

import lombok.Getter;

@Getter
public enum PCPMemberIngestionErrors {
	PCP_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TIMESTAMP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    PROVIDER_NOT_VALIDATED(HttpStatus.NOT_FOUND),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED);

	    private final HttpStatus statusCode;

	    PCPMemberIngestionErrors(HttpStatus statusCode) {
	        this.statusCode = statusCode;
	    }

	    public ServiceException createException(Object... objects) {
	        return new ServiceException(this.name(), this.statusCode, objects);
	    }

	    public HttpStatus httpStatus() {
	        return this.statusCode;
	    }
}
