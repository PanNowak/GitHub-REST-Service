package com.github.pannowak.githubrestservice.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class RepoRequestParams {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final String owner;
    private final String repositoryName;
    private final String token;

    public RepoRequestParams(String owner, String repositoryName, String token) {
        this.owner = owner;
        this.repositoryName = repositoryName;
        this.token = TOKEN_PREFIX + token;
    }
}