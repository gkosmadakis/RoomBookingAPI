package com.maritime.model;

import com.maritime.utils.CustomDoubleConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class CalculatedVesselMetrics {
	@CsvCustomBindByName(column = "latitude", converter = CustomDoubleConverter.class)
	private Double latitude; 
    @CsvCustomBindByName(column = "longitude", converter = CustomDoubleConverter.class)
    private Double longitude;
    @CsvCustomBindByName(column = "fuel_consumption", converter = CustomDoubleConverter.class)
    private Double fuelConsumption;
    @CsvCustomBindByName(column = "actual_speed_overground", converter = CustomDoubleConverter.class)
    private Double actualSpeedOverGround;
    @CsvCustomBindByName(column = "proposed_speed_overground", converter = CustomDoubleConverter.class)
    private Double proposedSpeedOverGround;
    @CsvBindByName(column = "predicted_fuel_consumption")
    private Double predictedFuelConsumption;
    private Double speedDifference;
    
    public CalculatedVesselMetrics(Double latitude, Double longitude, Double actualSpeedOverGround,
			Double proposedSpeedOverGround, Double fuelConsumption, Double predictedFuelConsumption,
			Double speedDifference) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.actualSpeedOverGround = actualSpeedOverGround;
		this.proposedSpeedOverGround = proposedSpeedOverGround;
		this.fuelConsumption = fuelConsumption;
		this.predictedFuelConsumption = predictedFuelConsumption;
		this.speedDifference = speedDifference;
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

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getFuelConsumption() {
		return fuelConsumption;
	}

	public void setFuelConsumption(Double fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
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

	public Double getSpeedDifference() {
		return speedDifference;
	}

	public void setSpeedDifference(Double speedDifference) {
		this.speedDifference = speedDifference;
	}
	
	
}
