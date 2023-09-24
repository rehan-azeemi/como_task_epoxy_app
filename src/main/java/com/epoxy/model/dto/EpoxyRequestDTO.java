package com.epoxy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpoxyRequestDTO {
    private String base64ApisEncoded;
    private String errorsType;
    private Integer timeout;
}
