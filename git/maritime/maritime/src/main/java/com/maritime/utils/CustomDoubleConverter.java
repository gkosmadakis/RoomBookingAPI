package com.maritime.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.bean.AbstractBeanField;

public class CustomDoubleConverter extends AbstractBeanField<Double, String> {
	Logger logger = LoggerFactory.getLogger(CustomDoubleConverter.class);
	
	/**
     * Checks for null values in the fields
     *
     * @param String value
     * @return Double
     */
    @Override
    protected Double convert(String value) {
        if (value == null || value.equalsIgnoreCase("NULL") || value.trim().isEmpty()) {
        	return null;  
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
        	logger.error("Invalid number format for value: " + value);
            return null;  
        }
    }
}

