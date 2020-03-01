package com.github.pannowak.githubrestservice.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RepositoryDetails {

    private String fullName;
    private String description;
    private String cloneUrl;
    private int stars;
    private LocalDate createdAt;

    @JsonGetter("fullName")
    public String getFullName() {
        return fullName;
    }

    @JsonSetter("full_name")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonGetter("description")
    public String getDescription() {
        return description;
    }

    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter("cloneUrl")
    public String getCloneUrl() {
        return cloneUrl;
    }

    @JsonSetter("clone_url")
    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    @JsonGetter("stars")
    public int getStars() {
        return stars;
    }

    @JsonSetter("stargazers_count")
    public void setStars(int stars) {
        this.stars = stars;
    }

    @JsonGetter("createdAt")
    public String getCreatedAt() {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(createdAt);
    }

    @JsonSetter("created_at")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt.toLocalDate();
    }
}