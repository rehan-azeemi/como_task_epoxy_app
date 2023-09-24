package com.epoxy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpoxyRequest {
    private List<String> endpoints;
    private Integer timeout;
    private String errorsType;
}
