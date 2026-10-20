package org.example.skillsprint.model;

import java.sql.Date;

public class Certificate {
    private int certificateId;
    private int userId;
    private String title;
    private String issuer;
    private Date issueDate;
    private String credentialLink;

    public Certificate() { }

    public Certificate(int certificateId, int userId, String title, String issuer,
                       Date issueDate, String credentialLink) {
        this.certificateId = certificateId;
        this.userId = userId;
        this.title = title;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.credentialLink = credentialLink;
    }

    public int getCertificateId() { return certificateId; }
    public void setCertificateId(int certificateId) { this.certificateId = certificateId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public String getCredentialLink() { return credentialLink; }
    public void setCredentialLink(String credentialLink) { this.credentialLink = credentialLink; }
}
