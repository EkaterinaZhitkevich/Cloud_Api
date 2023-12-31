package org.ezhitkevich.cloud_api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotEmpty
    private String login;

    @NotEmpty
    private String password;
}
