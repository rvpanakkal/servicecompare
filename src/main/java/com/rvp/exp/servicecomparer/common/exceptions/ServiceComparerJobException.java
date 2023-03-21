/**
 * 
 */
package com.rvp.exp.servicecomparer.common.exceptions;

/**
 * @author U12044
 *
 */
public class ServiceComparerJobException extends Exception {

	/**
	 * 
	 */
	public ServiceComparerJobException() {
		super();
	}

	/**
	 * @param message
	 */
	public ServiceComparerJobException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ServiceComparerJobException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceComparerJobException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ServiceComparerJobException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
