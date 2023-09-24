package com.epoxy.util;

import static org.junit.jupiter.api.Assertions.*;

import com.epoxy.config.exceptions.ApiFailedException;
import com.epoxy.config.exceptions.EpoxyBadRequestException;
import com.epoxy.model.EpoxyRequest;
import com.epoxy.model.constants.AppConstants;
import com.epoxy.model.dto.EpoxyRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import java.util.List;

public class UtilTest {
    @InjectMocks
    private Util util;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFromJsonArrayToList_givenJsonArray_shouldReturnJavaList() {
        String jsonArray = "['https://jsonplaceholder.typicode.com/users','https://jsonplaceholder.typicode.com/comments','http://restapi.adequateshop.com/api/Traveler?page=1']";
        List<String> endpoints = util.fromJsonArrayToList(jsonArray);
        assertNotNull(endpoints);
        assertEquals(3, endpoints.size());
        assertEquals("https://jsonplaceholder.typicode.com/users", endpoints.get(0));
    }

    @Test
    public void testFromJsonArrayToList_givenInvalidJsonArray_shouldReturnNullAndLogException() {
        String jsonArray = "https://jsonplaceholder.typicode.com/user,'http://restapi.adequateshop.com/api/Traveler?page=1']";
        List<String> endpoints = util.fromJsonArrayToList(jsonArray);
        assertNull(endpoints);
    }

    @Test
    public void testFromXmlToJson_givenValidXml_shouldReturnParsedJsonNode() {
        String xmlResponse = getXmlData();
        JsonNode jsonNode = util.fromXmlToJson(xmlResponse);
        assertNotNull(jsonNode);
        assertEquals("11133", jsonNode.get("id").asText());
    }

    @Test
    public void testFromXmlToJson_givenInvalidXml_shouldReturnNullAndLogException() {
        String xmlResponse = getInvalidXmlData();
        JsonNode jsonNode = util.fromXmlToJson(xmlResponse);
        assertNull(jsonNode);
    }

    @Test
    public void testFromStringToJsonNode_givenValidJson_shouldReturnParsedJson() {
        String jsonResponse = getJsonData();
        JsonNode jsonNode = util.fromStringToJsonNode(jsonResponse);
        assertNotNull(jsonNode);
        assertEquals("Sincere@april.biz", jsonNode.get("email").asText());
    }

    @Test
    public void testFromStringToJsonNode_givenInvalidJson_shouldReturnNullAndLogException() {
        String jsonResponse = "@#$$%%"+getJsonData();
        JsonNode jsonNode = util.fromStringToJsonNode(jsonResponse);
        assertNull(jsonNode);
    }

    @Test
    public void testDecodeBase64_givenBase64_shouldReturnJsonArray() {
        String base64Uri = getBase64Uri();
        String decodedString = util.decodeBase64(base64Uri);
        assertNotNull(decodedString);
        assertEquals("['https://jsonplaceholder.typicode.com/users']", decodedString);
    }

    @Test
    public void testParseApiResponse_givenJson_shouldReturnParsedJson() {
        String jsonResponse = getJsonData();
        MediaType mediaType = MediaType.APPLICATION_JSON;
        JsonNode jsonNode = util.parseApiResponse(jsonResponse, mediaType);
        assertNotNull(jsonNode);
        assertEquals("Sincere@april.biz", jsonNode.get("email").asText());
    }

    @Test
    public void testParseApiResponse_givenXml_shouldReturnParsedXml() {
        String xmlResponse = getXmlData();
        MediaType mediaType = MediaType.APPLICATION_XML;
        JsonNode jsonNode = util.parseApiResponse(xmlResponse, mediaType);
        assertNotNull(jsonNode);
        assertEquals("11133", jsonNode.get("id").asText());
    }

    @Test
    public void testIsSuccessFullApiCall_givenFailAnyErrorType_shouldThrowApiFailedException() {
        assertThrows(ApiFailedException.class, () -> util.isSuccessFullApiCall(AppConstants.ERROR_FAIL_ANY));
    }

    @Test
    public void testValidateRequest_givenValidRequestDto_shouldValidateRequest() {
        EpoxyRequestDTO epoxyRequestDTO = new EpoxyRequestDTO();
        epoxyRequestDTO.setBase64ApisEncoded(getBase64Uri());
        epoxyRequestDTO.setErrorsType("fail_any");

        EpoxyRequest epoxyRequest = util.validateRequest(epoxyRequestDTO);

        assertNotNull(epoxyRequest);
        assertEquals(1, epoxyRequest.getEndpoints().size());
        assertEquals(AppConstants.ERROR_FAIL_ANY, epoxyRequest.getErrorsType());
    }

    @Test
    public void testValidateRequest_givenInvalidRequestDto_shouldThrowException() {
        EpoxyRequestDTO epoxyRequestDTO = new EpoxyRequestDTO();
        epoxyRequestDTO.setBase64ApisEncoded(getBase64Uri());
        epoxyRequestDTO.setErrorsType("fail_anyyyyy");
        assertThrows(EpoxyBadRequestException.class, () -> util.validateRequest(epoxyRequestDTO));
    }

    private String getXmlData(){
        String xml = "<Travelerinformation>\n" +
                "<id>11133</id>\n" +
                "<name>Developer</name>\n" +
                "<email>Developer12@gmail.com</email>\n" +
                "<adderes>USA</adderes>\n" +
                "<createdat>0001-01-01T00:00:00</createdat>\n" +
                "</Travelerinformation>";
        return xml;
    }

    private String getInvalidXmlData(){
        String xml = "<Travelerinformation>\n" +
                "<id>11133</id>\n" +
                "<name>Developer</name>\n" +
                "<email>Developer12@gmail.com</email>\n" +
                "<adderes>USA</adderes>\n" +
                "<createdat>0001-01-01T00:00:00<createdat>\n" +
                "<Travelerinformation>";
        return xml;
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

    private String getBase64Uri(){
        return "WydodHRwczovL2pzb25wbGFjZWhvbGRlci50eXBpY29kZS5jb20vdXNlcnMnXQ==";
    }
}
