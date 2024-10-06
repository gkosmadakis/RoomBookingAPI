package com.maritime.utils;

public class ComplianceUtils {

	/**
     * Calculates the compliance based on how far from the proposed speed the vessel’s actual speed was
     *
     * @param double actualSpeed
     * @param double proposedSpeed
     * @return double
     */
	 public static double calculateCompliance(double actualSpeed, double proposedSpeed) {
	        // Example compliance calculation
	        return 1 - Math.abs(actualSpeed - proposedSpeed) / proposedSpeed;
	    }
}
