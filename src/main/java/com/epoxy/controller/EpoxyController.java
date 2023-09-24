package com.epoxy.controller;

import com.epoxy.model.dto.EpoxyRequestDTO;
import com.epoxy.model.constants.AppConstants;
import com.epoxy.service.EpoxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(AppConstants.EPOXY_V1_BASE_API)
public class EpoxyController {

    @Autowired
    private EpoxyService epoxyService;

    @GetMapping(AppConstants.EPOXY_COMBINED_API)
    public CompletableFuture<Map<String, Object>> combined(
            @PathVariable(AppConstants.REQUEST_PARAM_APIS) String apis,
            @RequestParam(AppConstants.REQUEST_PARAM_ERRORS) String errors,
            @RequestParam(value = AppConstants.REQUEST_PARAM_TIMEOUT, required = false) Integer timeout) {

        EpoxyRequestDTO requestWrapper = EpoxyRequestDTO
                .builder()
                .base64ApisEncoded(apis)
                .errorsType(errors)
                .timeout(timeout)
                .build();

        return epoxyService.combined(requestWrapper);
    }

    @GetMapping(AppConstants.EPOXY_APPENDED_API)
    public CompletableFuture<List<Map<String,Object>>> appended(
            @PathVariable(AppConstants.REQUEST_PARAM_APIS) String apis,
            @RequestParam(AppConstants.REQUEST_PARAM_ERRORS) String errors) {

        EpoxyRequestDTO requestWrapper = EpoxyRequestDTO
                .builder()
                .base64ApisEncoded(apis)
                .errorsType(errors)
                .build();

        return epoxyService.appended(requestWrapper);
    }
}
