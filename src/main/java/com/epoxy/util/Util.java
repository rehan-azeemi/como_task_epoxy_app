package com.epoxy.util;

import com.epoxy.config.exceptions.ApiFailedException;
import com.epoxy.config.exceptions.EpoxyBadRequestException;
import com.epoxy.model.EpoxyRequest;
import com.epoxy.model.constants.AppConstants;
import com.epoxy.model.dto.EpoxyRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import static com.epoxy.model.constants.AppConstants.*;

@Component
@Slf4j
public class Util {

    private XmlMapper xmlMapper;
    private ObjectMapper objectMapper;

    public Util(){
        xmlMapper = new XmlMapper();
        objectMapper = new ObjectMapper();
    }
    public List<String> fromJsonArrayToList(String jsonArray) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> endpoints = null;

        try {
            endpoints = gson.fromJson(jsonArray,type);
        }
        catch (Exception e){
           log.error("Exception in Util::fromJsonArrayToList() : {}", e.getMessage());
        }
        return endpoints;
    }

    public JsonNode fromXmlToJson(String response){
        JsonNode jsonNode = null;
        try{
            jsonNode = xmlMapper.readTree(response);
        }
        catch (Exception e){
            log.error("Exception in Util::fromXmlToJson() : {}", e.getMessage());
        }
        return jsonNode;
    }

    public JsonNode fromStringToJsonNode(String response){
        JsonNode jsonNode = null;
        try{
            jsonNode = objectMapper.readTree(response);
        }
        catch (Exception e){
            log.error("Exception in Util::fromStringToJsonNode() : {}", e.getMessage());
        }
        return jsonNode;
    }

    public String decodeBase64(String base64Uri){
        byte[] decodedBytes = Base64.getDecoder().decode(base64Uri);
        String jsonArrayString = new String(decodedBytes, StandardCharsets.UTF_8);
        return jsonArrayString;
    }
    public JsonNode parseApiResponse(String response, MediaType mediaType) {
        if(MediaType.APPLICATION_XML.isCompatibleWith(mediaType)){
            return fromXmlToJson(response);
        }
        return fromStringToJsonNode(response);
    }
    public JsonNode isSuccessFullApiCall(String errorsType){
        if(ERROR_FAIL_ANY.equals(errorsType)){
            throw new ApiFailedException(API_FAILED);
        }
        return null;
    }
    public EpoxyRequest validateRequest(EpoxyRequestDTO epoxyRequestDTO){
        EpoxyRequest request = new EpoxyRequest();

        if(epoxyRequestDTO.getBase64ApisEncoded() != null){
            String jsonArrayString = decodeBase64(epoxyRequestDTO.getBase64ApisEncoded());
            List<String> endpoints = fromJsonArrayToList(jsonArrayString);
            if(endpoints == null){
                throw new EpoxyBadRequestException(AppConstants.INCORRECT_BASE64);
            }
            request.setEndpoints(endpoints);
        }

        if(epoxyRequestDTO.getErrorsType() != null){
            if(!ERROR_FAIL_ANY.equals(epoxyRequestDTO.getErrorsType()) && !ERROR_REPLACE.equals(epoxyRequestDTO.getErrorsType())) {
                throw new EpoxyBadRequestException(AppConstants.INVALID_ERROR_TYPE);
            }
            request.setErrorsType(epoxyRequestDTO.getErrorsType());
        }

        request.setTimeout(epoxyRequestDTO.getTimeout()==null?TIME_OUT:epoxyRequestDTO.getTimeout());

        return request;
    }

    public void setRestTemplateTimeout(int timeoutInMilliseconds, RestTemplate restTemplate) {
        ClientHttpRequestFactory factory = restTemplate.getRequestFactory();

        if (factory instanceof SimpleClientHttpRequestFactory) {
            SimpleClientHttpRequestFactory simpleFactory = (SimpleClientHttpRequestFactory) factory;
            simpleFactory.setConnectTimeout(timeoutInMilliseconds);
            simpleFactory.setReadTimeout(timeoutInMilliseconds);
        } else {
            throw new UnsupportedOperationException("Cannot set timeouts on this type of ClientHttpRequestFactory");
        }
    }
}
