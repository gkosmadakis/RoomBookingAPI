package com.maritime.model;

import java.util.List;

public class ProblemGroup {
    
    private String problemType;  
    private long occurrences;    
    private List<VesselMetrics> affectedMetrics;
    private int listSize;
    private int problemCount;
    
    // Constructor
    public ProblemGroup() {
    }

    public ProblemGroup(String problemType, long occurrences, List<VesselMetrics> affectedMetrics) {
        this.problemType = problemType;
        this.occurrences = occurrences;
        this.affectedMetrics = affectedMetrics;
        this.problemCount = affectedMetrics != null ? affectedMetrics.size() : 0;
    }

	// Getters and Setters
    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public long getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(long occurrences) {
        this.occurrences = occurrences;
    }

    public List<VesselMetrics> getAffectedMetrics() {
        return affectedMetrics;
    }

    public void setAffectedMetrics(List<VesselMetrics> affectedMetrics) {
        this.affectedMetrics = affectedMetrics;
        this.problemCount = affectedMetrics != null ? affectedMetrics.size() : 0;
    }

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}
	
    public int getProblemCount() {
        return problemCount;
    }
    
    
}
