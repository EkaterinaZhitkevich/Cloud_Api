package org.ezhitkevich.authorization_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListFileResponseDto {

    private String filename;

    private long size;
}
