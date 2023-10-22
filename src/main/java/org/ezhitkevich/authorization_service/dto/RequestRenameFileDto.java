package org.ezhitkevich.authorization_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRenameFileDto {

    @JsonProperty("name")
    private String newFileName;
}
