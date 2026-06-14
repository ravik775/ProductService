package org.bgm.productservice.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class WebAuthenticationDetailsMixin {

    @JsonCreator
    public WebAuthenticationDetailsMixin(
            @JsonProperty("remoteAddress") String remoteAddress,
            @JsonProperty("sessionId") String sessionId) {
    }
}