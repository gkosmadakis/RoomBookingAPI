package com.maritime.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.maritime.model.VesselMetrics;

@Service
public class MetricCalculationService {

	/**
     * Calculates the speed difference between the ActualSpeedOverGround and the ProposedSpeedOverGround
     *
     * @param List<VesselMetrics> metrics
     * @return List<Double>
     */
    public List<Double> calculateSpeedDifference(List<VesselMetrics> metrics) {
        return metrics.stream()
                .map(metric -> Math.abs(metric.getActualSpeedOverGround() - metric.getProposedSpeedOverGround()))
                .collect(Collectors.toList());
    }
}

