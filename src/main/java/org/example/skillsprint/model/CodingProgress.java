package org.example.skillsprint.model;

import java.sql.Date;

public class CodingProgress {
    private int codingId;
    private int userId;
    private String platform;
    private String problemName;
    private String difficulty;
    private String problemStatus;
    private Date solvedDate;
    private String problemUrl;

    public CodingProgress() { }

    public CodingProgress(int codingId, int userId, String platform, String problemName,
                          String difficulty, String problemStatus, Date solvedDate, String problemUrl) {
        this.codingId = codingId;
        this.userId = userId;
        this.platform = platform;
        this.problemName = problemName;
        this.difficulty = difficulty;
        this.problemStatus = problemStatus;
        this.solvedDate = solvedDate;
        this.problemUrl = problemUrl;
    }

    public int getCodingId() { return codingId; }
    public void setCodingId(int codingId) { this.codingId = codingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getProblemName() { return problemName; }
    public void setProblemName(String problemName) { this.problemName = problemName; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getProblemStatus() { return problemStatus; }
    public void setProblemStatus(String problemStatus) { this.problemStatus = problemStatus; }
    public Date getSolvedDate() { return solvedDate; }
    public void setSolvedDate(Date solvedDate) { this.solvedDate = solvedDate; }
    public String getProblemUrl() { return problemUrl; }
    public void setProblemUrl(String problemUrl) { this.problemUrl = problemUrl; }
}
