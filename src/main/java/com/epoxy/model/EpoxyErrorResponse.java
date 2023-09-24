package com.epoxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EpoxyErrorResponse {
    private int statusCode;
    private String message;
}
