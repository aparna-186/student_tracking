package org.example.skillsprint.model;

public class User {
    private final int userId;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final String college;
    private final String branch;
    private final String githubUrl;
    private final String linkedinUrl;

    public User(String fullName, String email, String passwordHash, String college,
                String branch, String githubUrl, String linkedinUrl) {
        this(0, fullName, email, passwordHash, college, branch, githubUrl, linkedinUrl);
    }

    public User(int userId, String fullName, String email, String passwordHash, String college,
                String branch, String githubUrl, String linkedinUrl) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.college = college;
        this.branch = branch;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getCollege() {
        return college;
    }

    public String getBranch() {
        return branch;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }
}
