package org.ezhitkevich.authorization_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestRenameFileDto {

    @JsonProperty("name")
    private String newFileName;
}
