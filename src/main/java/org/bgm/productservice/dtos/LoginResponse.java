package org.bgm.productservice.dtos;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {
}
