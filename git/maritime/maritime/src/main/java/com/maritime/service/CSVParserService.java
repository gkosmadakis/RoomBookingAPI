package com.maritime.service;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.maritime.model.VesselMetrics;
import com.opencsv.bean.CsvToBeanBuilder;

import jakarta.annotation.PostConstruct;

@Service
public class CSVParserService {
	private List<VesselMetrics> vesselMetricsList;
	
	/** Load the data from the CSV file initially when the application starts
	 * 
	 * @throws Exception
	 */
    @PostConstruct
    public void loadData() throws Exception {
        ClassPathResource resource = new ClassPathResource("vessel_data.csv");
        this.vesselMetricsList = new CsvToBeanBuilder<VesselMetrics>(new FileReader(resource.getFile()))
                .withType(VesselMetrics.class)
                .build()
                .parse();
    }
    
    /**
     * Get metrics by vessel code.
     *
     * @param vesselCode the vessel code
     * @return list of vessel metrics for the given vessel
     */
    public List<VesselMetrics> getMetricsByVesselCode(String vesselCode) {
        // Filtering the metrics list based on the provided vessel code
        return this.vesselMetricsList.stream()
                .filter(metric -> vesselCode.equals(String.valueOf(metric.getVesselCode())))
                .collect(Collectors.toList());
    }
    
    
    
    /**
     * Gets the metrics by vessel code and in a date range.
     * 
     * @param vesselCode the vessel code, 
     * LocalDateTime startDate the start date of the range, 
     * LocalDateTime endDate the end date of the range
     * @return a list of metrics for the vessel code in the date range 
     */
    public List<VesselMetrics> getMetricsByVesselCodeAndDateRange(String vesselCode, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        // Filter the data by vessel code and date range
        return this.vesselMetricsList.stream()
                .filter(metric -> String.valueOf(metric.getVesselCode()).equals(vesselCode))
                .filter(metric -> {
                    LocalDateTime dateTime = metric.getDatetime();
                    return dateTime != null && !dateTime.isBefore(startDate) && !dateTime.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**Returns the vessel metrics list
     * */
	public List<VesselMetrics> getVesselMetricsList() {
		return this.vesselMetricsList;
	}
    

}

