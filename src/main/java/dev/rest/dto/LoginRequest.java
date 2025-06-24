package dev.rest.dto;

public record LoginRequest(
        String username,
        String password
) {}