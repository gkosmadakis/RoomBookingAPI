package com.maritime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.maritime.controller.VesselController;
import com.maritime.model.CalculatedVesselMetrics;
import com.maritime.model.ProblemGroup;
import com.maritime.model.VesselMetrics;
import com.maritime.service.CSVParserService;
import com.maritime.service.DataValidationService;
import com.maritime.service.MetricCalculationService;
import com.maritime.utils.ProblemGroupUtils;

public class VesselControllerTest {

    @InjectMocks
    private VesselController vesselController;

    @Mock
    private CSVParserService csvParserService;

    @Mock
    private DataValidationService dataValidationService;

    @Mock
    private MetricCalculationService metricCalculationService;
    
    private List<VesselMetrics> metrics;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        metrics = new ArrayList<>();
        VesselMetrics metric = new VesselMetrics();
        metric.setVesselCode(3001);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        metric.setDatetime(LocalDateTime.parse("2023-06-01 00:00:00", formatter));
        metric.setLatitude(10.2894458770752);
        metric.setLongitude(-14.788875579834);
        metric.setPower(0.0);
        metric.setFuelConsumption(0.0);
        metric.setActualSpeedOverGround(0.039996); 
        metric.setProposedSpeedOverGround(-0.189904262498021); 
        metric.setPredictedFuelConsumption(0.0);
        metrics.add(metric);
    }

    @Test
    public void testGetAllMetrics() throws Exception {
        Page<VesselMetrics> pagedResult = new PageImpl<>(metrics);
        
        when(csvParserService.getVesselMetricsList()).thenReturn(metrics);
        when(dataValidationService.filterInvalidDataPaged(any(), any())).thenReturn(pagedResult);

        ResponseEntity<List<VesselMetrics>> response = vesselController.getAllMetrics(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetSpeedDifference() throws Exception {
        String vesselCode = "3001";
  
        when(csvParserService.getMetricsByVesselCode(vesselCode)).thenReturn(metrics);
        when(dataValidationService.filterInvalidData(metrics)).thenReturn(metrics);
        when(metricCalculationService.calculateSpeedDifference(metrics)).thenReturn(Arrays.asList(1.0, 2.0));

        List<Double> speedDifferences = vesselController.getSpeedDifference(vesselCode);

        assertNotNull(speedDifferences);
        assertEquals(2, speedDifferences.size());
    }

    @Test
    public void testGetInvalidData() {
        String vesselCode = "3001";

        when(csvParserService.getMetricsByVesselCode(vesselCode)).thenReturn(metrics);
        try (MockedStatic<ProblemGroupUtils> mockedStatic = mockStatic(ProblemGroupUtils.class)) {
            mockedStatic.when(() -> ProblemGroupUtils.identifyInvalidData(any())).thenReturn(Arrays.asList("Missing proposed speed over ground"));
        }
        ResponseEntity<Map<String, Long>> response = vesselController.getInvalidData(vesselCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetVesselCompliance() {
    
        when(dataValidationService.filterInvalidData(any())).thenReturn(metrics);
        when(csvParserService.getVesselMetricsList()).thenReturn(metrics);

        ResponseEntity<Map<Integer, Double>> response = vesselController.getVesselCompliance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetMergedMetrics() throws Exception {
        String vesselCode = "3001";
        String startDate = "2023-01-01 00:00:00";
        String endDate = "2023-01-02 00:00:00";

        when(csvParserService.getMetricsByVesselCodeAndDateRange(vesselCode, 
                        LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                        LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .thenReturn(metrics);

        ResponseEntity<List<CalculatedVesselMetrics>> response = vesselController.getMergedMetrics(vesselCode, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetProblematicWaypoints() {
        String vesselCode = "3001";
        String problemType = "missing data";

        when(csvParserService.getMetricsByVesselCode(vesselCode)).thenReturn(metrics);
        
        try (MockedStatic<ProblemGroupUtils> mockedStatic = mockStatic(ProblemGroupUtils.class)) {
            mockedStatic.when(() -> ProblemGroupUtils.findProblemGroups(metrics, problemType)).thenReturn(new ArrayList<>());
            
            ResponseEntity<List<ProblemGroup>> response = vesselController.getProblematicWaypoints(vesselCode, problemType);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }
}


