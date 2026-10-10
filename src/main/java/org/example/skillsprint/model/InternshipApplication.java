package org.example.skillsprint.model;

import java.sql.Date;

public class InternshipApplication {
    private int applicationId;
    private int userId;
    private String companyName;
    private String role;
    private String applicationReference;
    private Date appliedDate;
    private String status;
    private String notes;

    public InternshipApplication() { }

    public InternshipApplication(int applicationId, int userId, String companyName, String role,
                                 String applicationReference, Date appliedDate, String status, String notes) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.companyName = companyName;
        this.role = role;
        this.applicationReference = applicationReference;
        this.appliedDate = appliedDate;
        this.status = status;
        this.notes = notes;
    }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getApplicationReference() { return applicationReference; }
    public void setApplicationReference(String applicationReference) { this.applicationReference = applicationReference; }
    public Date getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Date appliedDate) { this.appliedDate = appliedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
