package com.arul.complaint_management.dtos;

public class DashboardResponse {

    private long totalComplaints;

    private long openComplaints;

    private long inProgressComplaints;

    private long escalatedComplaints;

    private long resolvedComplaints;

    private long closedComplaints;

    private long lowPriority;

    private long mediumPriority;

    private long highPriority;

    private long criticalPriority;

	public long getTotalComplaints() {
		return totalComplaints;
	}

	public void setTotalComplaints(long totalComplaints) {
		this.totalComplaints = totalComplaints;
	}

	public long getOpenComplaints() {
		return openComplaints;
	}

	public void setOpenComplaints(long openComplaints) {
		this.openComplaints = openComplaints;
	}

	public long getInProgressComplaints() {
		return inProgressComplaints;
	}

	public void setInProgressComplaints(long inProgressComplaints) {
		this.inProgressComplaints = inProgressComplaints;
	}

	public long getEscalatedComplaints() {
		return escalatedComplaints;
	}

	public void setEscalatedComplaints(long escalatedComplaints) {
		this.escalatedComplaints = escalatedComplaints;
	}

	public long getResolvedComplaints() {
		return resolvedComplaints;
	}

	public void setResolvedComplaints(long resolvedComplaints) {
		this.resolvedComplaints = resolvedComplaints;
	}

	public long getClosedComplaints() {
		return closedComplaints;
	}

	public void setClosedComplaints(long closedComplaints) {
		this.closedComplaints = closedComplaints;
	}

	public long getLowPriority() {
		return lowPriority;
	}

	public void setLowPriority(long lowPriority) {
		this.lowPriority = lowPriority;
	}

	public long getMediumPriority() {
		return mediumPriority;
	}

	public void setMediumPriority(long mediumPriority) {
		this.mediumPriority = mediumPriority;
	}

	public long getHighPriority() {
		return highPriority;
	}

	public void setHighPriority(long highPriority) {
		this.highPriority = highPriority;
	}

	public long getCriticalPriority() {
		return criticalPriority;
	}

	public void setCriticalPriority(long criticalPriority) {
		this.criticalPriority = criticalPriority;
	}

    // Generate Getters and Setters
    
    
}