package com.maritime.controller;

import com.maritime.model.CalculatedVesselMetrics;
import com.maritime.model.ProblemGroup;
import com.maritime.model.VesselMetrics;
import com.maritime.service.*;
import com.maritime.utils.ComplianceUtils;
import com.maritime.utils.ProblemGroupUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vessels")
public class VesselController {
	Logger logger = LoggerFactory.getLogger(VesselController.class);

    @Autowired
    private CSVParserService csvParserService;

    @Autowired
    private DataValidationService dataValidationService;

    @Autowired
    private MetricCalculationService metricCalculationService;

    /**Returns all the metrics data paged
     * @param the page, the page for which the results will be returned, default is 0
     * @param the size, the size of the results, default is 10
     */
    @GetMapping("/metrics")
    public ResponseEntity<List<VesselMetrics>> getAllMetrics(@RequestParam(defaultValue = "0") int page,
    	    @RequestParam(defaultValue = "10") int size) throws Exception {
        List<VesselMetrics> metrics = csvParserService.getVesselMetricsList();
        Pageable paging = PageRequest.of(page, size);
        Page<VesselMetrics> pagedResult = dataValidationService.filterInvalidDataPaged(metrics, paging);

        if(pagedResult.hasContent()) {
            return new ResponseEntity<List<VesselMetrics>>(pagedResult.getContent(), HttpStatus.OK);
        } else {
        	logger.info("pagedResult was empty");
            return new ResponseEntity<List<VesselMetrics>>(new ArrayList<>(), HttpStatus.OK);
        }
    }
    
    /**Returns the difference in speed between the ActualSpeedOverGround and  
     * ProposedSpeedOverGround
     * @param the vesselCode
     */
    @GetMapping("/{vesselCode}/speed-difference")
    public List<Double> getSpeedDifference(@PathVariable String vesselCode) throws Exception {
        List<VesselMetrics> metrics = csvParserService.getMetricsByVesselCode(vesselCode);
        metrics = dataValidationService.filterInvalidData(metrics);
        return metricCalculationService.calculateSpeedDifference(metrics);
    }
    
    /**Returns the frequency or count of how many times each specific type of problem 
     * (or invalid data) occurred in the vesselMetrics list
     * @param the vesselCode
     */
    @GetMapping("/{vesselCode}/invalid-data")
    public ResponseEntity<Map<String, Long>> getInvalidData(@PathVariable String vesselCode) {
        List<VesselMetrics> vesselMetrics = csvParserService.getMetricsByVesselCode(vesselCode);

        Map<String, Long> invalidDataCount = vesselMetrics.stream()
                .flatMap(metric -> ProblemGroupUtils.identifyInvalidData(metric).stream())
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        return new ResponseEntity<>(invalidDataCount, HttpStatus.OK);
    }

    /**Returns the compliance of the two vessels which is based on the calculation of 
     * the calculateCompliance method
     */
    @GetMapping("/compliance")
    public ResponseEntity<Map<Integer, Double>> getVesselCompliance() {
    	List<VesselMetrics> validMetrics = dataValidationService.filterInvalidData(csvParserService.getVesselMetricsList());
    	Map<Integer, Double> complianceMap = validMetrics.stream()
                .collect(Collectors.groupingBy(VesselMetrics::getVesselCode, 
                        Collectors.averagingDouble(metric -> 
                                ComplianceUtils.calculateCompliance(metric.getActualSpeedOverGround(), metric.getProposedSpeedOverGround())
                        )
                ));

        return new ResponseEntity<>(complianceMap, HttpStatus.OK);
    }
    
    /**Returns the raw and calculated data by vesselCode
     * startDate and endDate should be in the format yyyy-MM-dd HH:mm:ss
     * @param the vesselCode
     * @param the startDate
     * @param the endDate
     */
    @GetMapping("/{vesselCode}/metrics")
    public ResponseEntity<List<CalculatedVesselMetrics>> getMergedMetrics(@PathVariable String vesselCode,
                                                                   @RequestParam String startDate,
                                                                   @RequestParam String endDate) {
        List<VesselMetrics> vesselMetrics = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateDateTime = LocalDateTime.parse(endDate, formatter);
		try {
			vesselMetrics = csvParserService.getMetricsByVesselCodeAndDateRange(vesselCode, startDateDateTime, endDateDateTime);
		} catch (Exception e) {
			logger.error("An error occurred while trying to get metrics by vessel code and date range with message " +e.getMessage());
			e.printStackTrace();
		}

        List<CalculatedVesselMetrics> mergedMetrics = vesselMetrics.stream()
                .map(metric -> new CalculatedVesselMetrics(metric.getLatitude(), 
                                                    metric.getLongitude(),
                                                    metric.getActualSpeedOverGround(),
                                                    metric.getProposedSpeedOverGround(),
                                                    metric.getFuelConsumption(),
                                                    metric.getPredictedFuelConsumption(),
                                                    metric.calculateSpeedDifference(metric)))
                .collect(Collectors.toList());

        return new ResponseEntity<>(mergedMetrics, HttpStatus.OK);
    }
    
    /**Returns the problematic waypoints by vesselCode
     * @param the vesselCode
     * @param the problemType can be missing data or negative values or outliers
     */
    @GetMapping("/{vesselCode}/problematic-waypoints")
    public ResponseEntity<List<ProblemGroup>> getProblematicWaypoints(@PathVariable String vesselCode,
                                                                         @RequestParam String problemType) {
        List<VesselMetrics> vesselMetrics = csvParserService.getMetricsByVesselCode(vesselCode);
        List<ProblemGroup> problemGroups = ProblemGroupUtils.findProblemGroups(vesselMetrics, problemType);

        return new ResponseEntity<>(problemGroups, HttpStatus.OK);
    }

	
}

