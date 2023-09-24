package com.epoxy.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.epoxy.config.exceptions.ApiFailedException;
import com.epoxy.model.EpoxyRequest;
import com.epoxy.model.constants.AppConstants;
import com.epoxy.model.dto.EpoxyRequestDTO;
import com.epoxy.util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EpoxyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Util util;

    @InjectMocks
    private EpoxyService epoxyService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testFetchDataFromApi_givenValidJson_shouldReturnParsedJson() throws Exception {
        String url = "https://jsonplaceholder.typicode.com/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(getJsonData(),headers,HttpStatus.OK);
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
        when(util.parseApiResponse(responseEntity.getBody(),headers.getContentType())).thenReturn(jsonNode);
        CompletableFuture<JsonNode> result = epoxyService.fetchDataFromApi(url, AppConstants.ERROR_FAIL_ANY);
        JsonNode jsonNodeResult = result.get(500, TimeUnit.MILLISECONDS);
        assertNotNull(jsonNodeResult);
        assertEquals("Sincere@april.biz", jsonNode.get("email").asText());
    }

    @Test
    public void testFetchDataFromApi_thrownApiFailedException_shouldNotDoneCompletableFuture() {
        String url = "https://jsonplaceholder.typicode.com/users";
        doThrow(ApiFailedException.class).when(restTemplate).getForEntity(url, String.class);
        doThrow(ApiFailedException.class).when(util).isSuccessFullApiCall(AppConstants.ERROR_FAIL_ANY);
        CompletableFuture<JsonNode> result = epoxyService.fetchDataFromApi(url, AppConstants.ERROR_FAIL_ANY);

        assertFalse(result.isDone());
    }

    @Test
    public void testFetchDataFromApi_givenApiFailedResponse_shouldReturnNull() throws Exception {
        String url = "https://jsonplaceholder.typicode.com/users";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
        CompletableFuture<JsonNode> result = epoxyService.fetchDataFromApi(url, AppConstants.ERROR_REPLACE);

        assertNull(result.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testFetchAllDataFromApis_givenEpoxyRequest_shouldReturnListOfJsonNode() throws Exception {
        String url = "https://jsonplaceholder.typicode.com/users";
        EpoxyRequest epoxyRequest = new EpoxyRequest();
        epoxyRequest.setErrorsType(AppConstants.ERROR_FAIL_ANY);
        List<String> endpoints = Arrays.asList(url);
        epoxyRequest.setEndpoints(endpoints);
        epoxyRequest.setTimeout(AppConstants.TIME_OUT);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(getJsonData(),headers,HttpStatus.OK);
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        doNothing().when(util).setRestTemplateTimeout(AppConstants.TIME_OUT,restTemplate);
        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
        when(util.parseApiResponse(responseEntity.getBody(),headers.getContentType())).thenReturn(jsonNode);

        CompletableFuture<List<JsonNode>> result = epoxyService.fetchAllDataFromApis(epoxyRequest);

        List<JsonNode> resultList = result.get();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("Sincere@april.biz", resultList.get(0).get("email").asText());
    }

    @Test
    public void testCombined_givenEpoxyRequestDto_shouldReturnMapOfJsonNodeAndUrl() throws Exception {
        EpoxyRequestDTO requestDTO = new EpoxyRequestDTO();
        requestDTO.setBase64ApisEncoded(base64Str());
        requestDTO.setTimeout(500);
        requestDTO.setErrorsType("fail_any");

        String url = "https://jsonplaceholder.typicode.com/users";
        EpoxyRequest epoxyRequest = new EpoxyRequest();
        epoxyRequest.setTimeout(AppConstants.TIME_OUT);
        epoxyRequest.setErrorsType(AppConstants.ERROR_FAIL_ANY);
        List<String> endpoints = Arrays.asList(url);
        epoxyRequest.setEndpoints(endpoints);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(getJsonData(),headers,HttpStatus.OK);
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        doNothing().when(util).setRestTemplateTimeout(AppConstants.TIME_OUT,restTemplate);
        when(util.validateRequest(requestDTO)).thenReturn(epoxyRequest);
        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
        when(util.parseApiResponse(responseEntity.getBody(),headers.getContentType())).thenReturn(jsonNode);

        CompletableFuture<Map<String, Object>> result = epoxyService.combined(requestDTO);

        Map<String, Object> resultMap = result.get();
        assertNotNull(resultMap);
        assertEquals(1, resultMap.size());
        assertTrue(resultMap.containsKey(url));
    }

    @Test
    public void testAppended_givenEpoxyRequestDto_shouldReturnListOfMapJsonNodeAndUrl() throws Exception {
        EpoxyRequestDTO requestDTO = new EpoxyRequestDTO();
        requestDTO.setBase64ApisEncoded(base64Str());
        requestDTO.setTimeout(500);
        requestDTO.setErrorsType("fail_any");

        String url = "https://jsonplaceholder.typicode.com/users";
        EpoxyRequest epoxyRequest = new EpoxyRequest();
        epoxyRequest.setErrorsType(AppConstants.ERROR_FAIL_ANY);
        List<String> endpoints = Arrays.asList(url);
        epoxyRequest.setEndpoints(endpoints);
        epoxyRequest.setTimeout(AppConstants.TIME_OUT);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(getJsonData(),headers,HttpStatus.OK);
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());

        doNothing().when(util).setRestTemplateTimeout(AppConstants.TIME_OUT,restTemplate);
        when(util.validateRequest(requestDTO)).thenReturn(epoxyRequest);
        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
        when(util.parseApiResponse(responseEntity.getBody(),headers.getContentType())).thenReturn(jsonNode);

        CompletableFuture<List<Map<String, Object>>> result = epoxyService.appended(requestDTO);

        Map<String, Object> resultMap = result.get().get(0);
        assertNotNull(resultMap);
        assertEquals(1, resultMap.size());
        assertTrue(resultMap.containsKey(url));
    }

    private String getJsonData(){
        String json = "    {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"Leanne Graham\",\n" +
                "        \"username\": \"Bret\",\n" +
                "        \"email\": \"Sincere@april.biz\",\n" +
                "        \"address\": {\n" +
                "            \"street\": \"Kulas Light\",\n" +
                "            \"suite\": \"Apt. 556\",\n" +
                "            \"city\": \"Gwenborough\",\n" +
                "            \"zipcode\": \"92998-3874\",\n" +
                "            \"geo\": {\n" +
                "                \"lat\": \"-37.3159\",\n" +
                "                \"lng\": \"81.1496\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"phone\": \"1-770-736-8031 x56442\",\n" +
                "        \"website\": \"hildegard.org\",\n" +
                "        \"company\": {\n" +
                "            \"name\": \"Romaguera-Crona\",\n" +
                "            \"catchPhrase\": \"Multi-layered client-server neural-net\",\n" +
                "            \"bs\": \"harness real-time e-markets\"\n" +
                "        }\n" +
                "    }";

        return json;
    }

    private String base64Str(){
        return "WydodHRwczovL2pzb25wbGFjZWhvbGRlci50eXBpY29kZS5jb20vdXNlcnMnLCdodHRwczovL2pzb25wbGFjZWhvbGRlci50eXBpY29kZS5jb20vY29tbWVudHMnLCdodHRwOi8vcmVzdGFwaS5hZGVxdWF0ZXNob3AuY29tL2FwaS9UcmF2ZWxlcj9wYWdlPTEnXQ==";
    }
}
