package org.ezhitkevich.authorization_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse {

    @JsonProperty("time_stamp")
    private Instant instant = Instant.now();

    @JsonProperty("message")
    private String message;
}
