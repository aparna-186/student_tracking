package org.example.skillsprint.model;

import java.sql.Date;

public class Goal {
    private int goalId;
    private int userId;
    private String goalTitle;
    private Date deadline;
    private String status;

    public Goal() { }

    public Goal(int goalId, int userId, String goalTitle, Date deadline, String status) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalTitle = goalTitle;
        this.deadline = deadline;
        this.status = status;
    }

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getGoalTitle() { return goalTitle; }
    public void setGoalTitle(String goalTitle) { this.goalTitle = goalTitle; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
