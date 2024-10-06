package com.maritime.model;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maritime.utils.CustomDoubleConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;

public class VesselMetrics {
	Logger logger = LoggerFactory.getLogger(VesselMetrics.class);
	
	@CsvBindByName(column = "vessel_code")
    private int vesselCode;
	@CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "datetime")
    private LocalDateTime datetime;
	@CsvCustomBindByName(column = "latitude", converter = CustomDoubleConverter.class)
    private Double latitude; 
    @CsvCustomBindByName(column = "longitude", converter = CustomDoubleConverter.class)
    private Double longitude;
    @CsvCustomBindByName(column = "power", converter = CustomDoubleConverter.class)
    private Double power;
    @CsvCustomBindByName(column = "fuel_consumption", converter = CustomDoubleConverter.class)
    private Double fuelConsumption;
    @CsvCustomBindByName(column = "actual_speed_overground", converter = CustomDoubleConverter.class)
    private Double actualSpeedOverGround;
    @CsvCustomBindByName(column = "proposed_speed_overground", converter = CustomDoubleConverter.class)
    private Double proposedSpeedOverGround;
    @CsvBindByName(column = "predicted_fuel_consumption")
    private Double predictedFuelConsumption;
    
    // No-argument constructor (required by OpenCSV)
    public VesselMetrics() {
    }
	
	public int getVesselCode() {
		return vesselCode;
	}
	
	public void setVesselCode(int vesselCode) {
		this.vesselCode = vesselCode;
	}
	
	public LocalDateTime getDatetime() {
		return datetime;
	}
	
	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public Double getPower() {
		return power;
	}
	
	public void setPower(Double power) {
		this.power = power;
	}
	
	public Double getFuelConsumption() {
		return fuelConsumption;
	}
	
	public void setFuelConsumption(Double fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Double getActualSpeedOverGround() {
		return actualSpeedOverGround;
	}
	
	public void setActualSpeedOverGround(Double actualSpeedOverGround) {
		this.actualSpeedOverGround = actualSpeedOverGround;
	}
	
	public Double getProposedSpeedOverGround() {
		return proposedSpeedOverGround;
	}
	
	public void setProposedSpeedOverGround(Double proposedSpeedOverGround) {
		this.proposedSpeedOverGround = proposedSpeedOverGround;
	}
	
	public Double getPredictedFuelConsumption() {
		return predictedFuelConsumption;
	}
	
	public void setPredictedFuelConsumption(Double predictedFuelConsumption) {
		this.predictedFuelConsumption = predictedFuelConsumption;
	}

	/**
     * Calculates the speed difference between the ActualSpeedOverGround and the ProposedSpeedOverGround
     *
     * @param VesselMetrics metric
     * @return Double
     */
	public Double calculateSpeedDifference(VesselMetrics metric) {
        Double actualSpeed = metric.getActualSpeedOverGround();
        Double proposedSpeed = metric.getProposedSpeedOverGround();
        // If either speed is null, treat it as invalid
        if (actualSpeed == null || proposedSpeed == null) {
            logger.error("Actual Speed or proposedSpeed is null");
        	return null; 
        }
        // Calculate the speed difference
        return actualSpeed - proposedSpeed;
    }
	
    
}

