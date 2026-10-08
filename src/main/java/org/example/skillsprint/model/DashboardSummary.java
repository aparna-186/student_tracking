package org.example.skillsprint.model;

public class DashboardSummary {
    private final int totalApplications;
    private final int totalInterviews;
    private final int totalOffers;
    private final int totalRejected;
    private final int totalCoding;
    private final int totalCertificates;
    private final int completedGoals;
    private final int pendingGoals;

    public DashboardSummary(int totalApplications, int totalInterviews, int totalOffers,
                            int totalRejected, int totalCoding, int totalCertificates,
                            int completedGoals, int pendingGoals) {
        this.totalApplications = totalApplications;
        this.totalInterviews = totalInterviews;
        this.totalOffers = totalOffers;
        this.totalRejected = totalRejected;
        this.totalCoding = totalCoding;
        this.totalCertificates = totalCertificates;
        this.completedGoals = completedGoals;
        this.pendingGoals = pendingGoals;
    }

    public int getTotalApplications() { return totalApplications; }
    public int getTotalInterviews() { return totalInterviews; }
    public int getTotalOffers() { return totalOffers; }
    public int getTotalRejected() { return totalRejected; }
    public int getTotalCoding() { return totalCoding; }
    public int getTotalCertificates() { return totalCertificates; }
    public int getCompletedGoals() { return completedGoals; }
    public int getPendingGoals() { return pendingGoals; }
}
