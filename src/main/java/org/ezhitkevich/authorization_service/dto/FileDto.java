package org.ezhitkevich.authorization_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto implements Serializable {

    private static final long serialVersionUID = 232836038145089522L;

    private String title;

    private String description;

    @SuppressWarnings("java:S1948")
    private MultipartFile file;

    @NotEmpty
    private String url;

    private Long size;

    @NotEmpty
    private String filename;
}
