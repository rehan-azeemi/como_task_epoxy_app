package com.epoxy.model.constants;

public class AppConstants {

    /*
    * Api Endpoints Constants
    * */
    public static final String EPOXY_V1_BASE_API = "/v1/fetch/";
    public static final String EPOXY_COMBINED_API = "{apis}/combined";
    public static final String EPOXY_APPENDED_API = "{apis}/appended";

    /*
     * Api Request Param Constants
     * */
    public static final String REQUEST_PARAM_APIS = "apis";
    public static final String REQUEST_PARAM_ERRORS = "errors";
    public static final String REQUEST_PARAM_TIMEOUT = "timeout";

    /*
     * Api Error Messages
     * */
    public static final String INCORRECT_BASE64 = "Incorrect Encoded Base64 URI";
    public static final String INVALID_ERROR_TYPE = "Invalid Error Type [fail_any,replace]";
    public static final String API_FAILED = "failed";

    /*
     * Api Error Codes
     * */
    public static final String ERROR_FAIL_ANY = "fail_any";
    public static final String ERROR_REPLACE = "replace";

    /*
    * Default Timeout
    * */
    public static final Integer TIME_OUT = 1000;
}
