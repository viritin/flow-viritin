package org.vaadin.firitin.geolocation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * A class to configure options for geolocation requests.
 * 
 * @author mstahv
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeolocationOptions implements Serializable {

    private Boolean enableHighAccuracy;
    private Integer timeout;
    private Integer maximumAge;

    public GeolocationOptions() {
    }

    public GeolocationOptions(Boolean enableHighAccuracy, Integer timeout, Integer maximumAge) {
        this.enableHighAccuracy = enableHighAccuracy;
        this.timeout = timeout;
        this.maximumAge = maximumAge;
    }

    /**
     * The enableHighAccuracy member provides a hint that the application would like to receive the most accurate location data. The intended purpose of this member is to allow applications to inform the implementation that they do not require high accuracy geolocation fixes and, therefore, the implementation MAY avoid using geolocation providers that consume a significant amount of power (e.g., GPS).
     *<p>
     * NOTE: A word of warning about enableHighAccuracy
     * The enableHighAccuracy member can result in slower response times or increased power consumption. The user might also disable this capability, or the device might not be able to provide more accurate results than if the flag wasn't specified.
     *</p>
     * @return true if high accuracy is requested
     */
    public Boolean getEnableHighAccuracy() {
        return enableHighAccuracy;
    }

    public void setEnableHighAccuracy(Boolean enableHighAccuracy) {
        this.enableHighAccuracy = enableHighAccuracy;
    }

    /**
     * The timeout member denotes the maximum length of time, expressed in milliseconds, before acquiring a position expires.
     * <p>
     * NOTE: When is the timeout calculated?
     * The time spent waiting for the document to become visible and for obtaining permission to use the API is not included in the period covered by the timeout member. The timeout member only applies when acquiring a position begins.
     * </p>
     * <p>
     * NOTE: Immediate cancellation
     * An options.timeout value 0 can cause immediate failures.
     * </p>
     *
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * @return The maximumAge member indicates that the web application is willing to accept a cached position whose age is no greater than the specified time in milliseconds.
     */
    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }
}
