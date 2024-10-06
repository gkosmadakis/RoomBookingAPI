package com.maritime.service;

import com.maritime.model.VesselMetrics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataValidationService {

	/**
     * Filters invalid data and returns paged results.
     *
     * @param List<VesselMetrics> metrics
     * @param Pageable pageable
     * @return Page<VesselMetrics> paged result
     */
	public Page<VesselMetrics> filterInvalidDataPaged(List<VesselMetrics> metrics, Pageable pageable) {
	    // Filter the list to only include valid metrics
	    List<VesselMetrics> filteredMetrics = metrics.stream()
	            .filter(this::isValid)
	            .collect(Collectors.toList());

	    // Calculate start and end indices for the current page
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageable.getPageSize()), filteredMetrics.size());

	    // Return the filtered metrics as a Page
	    return new PageImpl<>(filteredMetrics.subList(start, end), pageable, filteredMetrics.size());
	}
	
	/**
     * Filters invalid data and returns List<VesselMetrics>.
     *
     * @param List<VesselMetrics> metrics
     * @return List<VesselMetrics>
     */
	public List<VesselMetrics> filterInvalidData(List<VesselMetrics> metrics) {
        return metrics.stream()
                .filter(metric -> isValid(metric))
                .collect(Collectors.toList());
    }

	/**
     * Checks for valid data
     *
     * @param VesselMetrics metric
     * @return boolean
     */
	private boolean isValid(VesselMetrics metric) {
		if (metric.getActualSpeedOverGround() == null || metric.getProposedSpeedOverGround() == null
				|| metric.getActualSpeedOverGround() < 0 || metric.getProposedSpeedOverGround() < 0) {
			return false;
		}
		return true;
	}
}

