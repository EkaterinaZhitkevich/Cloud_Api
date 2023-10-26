package org.ezhitkevich.cloud_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinioFile {

    private String hash;

    //TODO delete this
    private String binaryStringFile;


    private Resource resource;

}
