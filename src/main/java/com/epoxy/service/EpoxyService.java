package com.epoxy.service;

import com.epoxy.model.EpoxyRequest;
import com.epoxy.model.dto.EpoxyRequestDTO;
import com.epoxy.util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.IntStream;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import static com.epoxy.model.constants.AppConstants.API_FAILED;

@Service
public class EpoxyService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Util util;

    public CompletableFuture<JsonNode> fetchDataFromApi(String url, String errorsType) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = null;

            try {
                response = restTemplate.getForEntity(url, String.class);

                if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null){
                    return util.isSuccessFullApiCall(errorsType);
                }
            }
            catch (Exception e){
                return util.isSuccessFullApiCall(errorsType);
            }

            return util.parseApiResponse(response.getBody(),response.getHeaders().getContentType());
        });
    }

    public CompletableFuture<List<JsonNode>> fetchAllDataFromApis(EpoxyRequest epoxyRequest) {
        util.setRestTemplateTimeout(epoxyRequest.getTimeout(),restTemplate);

        List<CompletableFuture<JsonNode>> futures = epoxyRequest.getEndpoints().stream()
                .map(endpoint -> fetchDataFromApi(endpoint, epoxyRequest.getErrorsType()))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())).thenApply(list ->list);
    }

    public CompletableFuture<Map<String, Object>> combined(EpoxyRequestDTO requestDTO) {
        EpoxyRequest request = util.validateRequest(requestDTO);
        return fetchAllDataFromApis(request).thenApply(jsonNodeList -> {
                Map<String, Object> combinedJsonObjects = IntStream.range(0, jsonNodeList.size())
                        .boxed()
                        .collect(Collectors.toMap(
                                i -> request.getEndpoints().get(i),
                                i -> jsonNodeList.get(i) == null ? API_FAILED : jsonNodeList.get(i)
                        ));
                return combinedJsonObjects;
            });
    }

    public CompletableFuture<List<Map<String,Object>>> appended(EpoxyRequestDTO requestDTO) {
        EpoxyRequest request = util.validateRequest(requestDTO);

        return fetchAllDataFromApis(request).thenApply(jsonNodeList->{
            List<Map<String,Object>> appendedJsonObjects = IntStream.range(0, jsonNodeList.size())
                    .mapToObj(i -> {
                        Map<String, Object> jsonNodeMap = new LinkedHashMap<>();
                        JsonNode apiJsonNode = jsonNodeList.get(i);
                        jsonNodeMap.put(request.getEndpoints().get(i), apiJsonNode == null ? API_FAILED : apiJsonNode);
                        return jsonNodeMap;
                    })
                    .collect(Collectors.toList());

            return appendedJsonObjects;
        });
    }
}
