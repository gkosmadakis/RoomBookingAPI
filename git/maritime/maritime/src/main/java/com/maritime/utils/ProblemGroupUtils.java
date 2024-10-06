package com.maritime.utils;

import java.util.ArrayList;
import java.util.List;

import com.maritime.model.ProblemGroup;
import com.maritime.model.VesselMetrics;

public class ProblemGroupUtils {
	private static final double MIN_VALID_SPEED = 0.0;
	private static final double MAX_VALID_SPEED = 50.0; // Example threshold for speed outliers
	private static final double MIN_VALID_FUEL_CONSUMPTION = 0.0;
	private static final double MAX_VALID_FUEL_CONSUMPTION = 1000.0; // Example threshold for fuel outliers
	
	/**
     * Identifies the invalid data within a VesselMetrics object.
     * 
     * @param metric the VesselMetrics object
     * @return a list of strings containing information about the invalid fields
     */
    public static List<String> identifyInvalidData(VesselMetrics metric) {
        List<String> invalidDataIssues = new ArrayList<>();

        // Check for missing or null values
        if (metric.getLatitude() == null || metric.getLongitude() == null) {
            invalidDataIssues.add("Missing coordinates");
        }
        
        if (metric.getActualSpeedOverGround() == null) {
            invalidDataIssues.add("Missing actual speed over ground");
        }
        
        if (metric.getProposedSpeedOverGround() == null) {
            invalidDataIssues.add("Missing proposed speed over ground");
        }

        if (metric.getFuelConsumption() == null) {
            invalidDataIssues.add("Missing fuel consumption");
        }

        // Check for negative values
        if (metric.getActualSpeedOverGround() != null && metric.getActualSpeedOverGround() < MIN_VALID_SPEED) {
            invalidDataIssues.add("Negative actual speed over ground");
        }
        
        if (metric.getProposedSpeedOverGround() != null && metric.getProposedSpeedOverGround() < MIN_VALID_SPEED) {
            invalidDataIssues.add("Negative proposed speed over ground");
        }

        if (metric.getFuelConsumption() != null && metric.getFuelConsumption() < MIN_VALID_FUEL_CONSUMPTION) {
            invalidDataIssues.add("Negative fuel consumption");
        }

        // Check for outliers
        if (metric.getActualSpeedOverGround() != null && metric.getActualSpeedOverGround() > MAX_VALID_SPEED) {
            invalidDataIssues.add("Outlier: Actual speed over ground too high");
        }
        
        if (metric.getProposedSpeedOverGround() != null && metric.getProposedSpeedOverGround() > MAX_VALID_SPEED) {
            invalidDataIssues.add("Outlier: Proposed speed over ground too high");
        }

        if (metric.getFuelConsumption() != null && metric.getFuelConsumption() > MAX_VALID_FUEL_CONSUMPTION) {
            invalidDataIssues.add("Outlier: Fuel consumption too high");
        }

        return invalidDataIssues;
    }
    
    /**
     * Identifies problem groups and sorts them by problem count.
     * 
     * @param List<VesselMetrics> metrics
     * @param String problemType can be missing data or negative values or outliers
     * @return List<ProblemGroup>
     */
	public static List<ProblemGroup> findProblemGroups(List<VesselMetrics> metrics, String problemType) {
		List<ProblemGroup> groups = new ArrayList<>();
		List<VesselMetrics> currentGroup = null;
		for (VesselMetrics metric : metrics) {
			currentGroup = new ArrayList<>();
			if (hasProblem(metric, problemType)) {
				currentGroup.add(metric);
				groups.add(new ProblemGroup(problemType, currentGroup.size(), currentGroup));
			} 
		}
		// Sort groups by size
		groups.sort((g1, g2) -> Integer.compare(g2.getProblemCount(), g1.getProblemCount()));

		return groups;
	}
    
	/**
     * Checks if a metric has a problem.
     * 
     * @param VesselMetrics metric
     * @param String problemType can be missing data or negative values or outliers
     * @return boolean 
     */
    public static boolean hasProblem(VesselMetrics metric, String problemType) {
        switch (problemType.toLowerCase()) {
            case "missing data":
                return isMissingData(metric);
            case "negative values":
                return hasNegativeValues(metric);
            case "outliers":
                return isOutlier(metric);
            default:
                return false; // If the problem type doesn't match any known case
        }
    }
    
    /**
     * Checks if a metric is missing data.
     * 
     * @param VesselMetrics metric
     * @return boolean 
     */
    private static boolean isMissingData(VesselMetrics metric) {
        return metric.getLatitude() == null || metric.getLongitude() == null ||
               metric.getActualSpeedOverGround() == null || metric.getProposedSpeedOverGround() == null ||
               metric.getFuelConsumption() == null || metric.getPower() == null;
    }

    /**
     * Checks if a metric has negative values.
     * 
     * @param VesselMetrics metric
     * @return boolean 
     */
    private static boolean hasNegativeValues(VesselMetrics metric) {
        return (metric.getActualSpeedOverGround() != null && metric.getActualSpeedOverGround() < 0) ||
               (metric.getProposedSpeedOverGround() != null && metric.getProposedSpeedOverGround() < 0) ||
               (metric.getFuelConsumption() != null && metric.getFuelConsumption() < 0) ||
               (metric.getPower() != null && metric.getPower() < 0);
    }

    /**
     * Checks if a metric is outlier.
     * 
     * @param VesselMetrics metric
     * @return boolean 
     */
    private static boolean isOutlier(VesselMetrics metric) {
        Double actualSpeed = metric.getActualSpeedOverGround();
        Double proposedSpeed = metric.getProposedSpeedOverGround();

        // Simple outlier check: if the difference between actual and proposed speed is too large
        if (actualSpeed != null && proposedSpeed != null) {
            double difference = Math.abs(actualSpeed - proposedSpeed);
            return difference > 1.0;  // Example threshold for detecting outliers
        }
        return false;
    }

}
