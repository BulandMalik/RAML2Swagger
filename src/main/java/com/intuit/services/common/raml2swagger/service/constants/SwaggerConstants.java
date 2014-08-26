package com.intuit.services.common.raml2swagger.service.constants;

public interface SwaggerConstants
{

	public static String APIVERSION_PARAM_KEY = "apiVersion";
	public static String APIVERSION_PARAM_VALUE = "1.0.0";
	
	public static String SWAGGERVERSION_PARAM_KEY = "swaggerVersion";
	public static String SWAGGERVERSION_PARAM_VALUE = "1.2";
	
	public static String PATH_PARAM_KEY = "path";
	public static String DESCRIPTION_PARAM_KEY = "description";	
	public static String APIS_PARAM_KEY = "apis";
	public static String OPERATIONS_PARAM_KEY = "operations";
	
	//interfcae param keys
	public static String MEDIATYPE_APPLICATION_JSON = "application/json";
	public static String MEDIATYPE_APPLICATION_XML = "application/xml";	
	public static String METHOD_PARAM_KEY = "method";
	public static String SUMMARY_PARAM_KEY = "summary";
	public static String NOTES_PARAM_KEY = "notes";	
	public static String NICKNAME_PARAM_KEY = "nickname";
	public static String TYPE_PARAM_KEY = "type";
	public static String PRODUCES_MEDIATYPE_PARAM_KEY = "produces";
	public static String CONSUMES_MEDIATYPE_PARAM_KEY = "consumes";
	public static String PARAMETERS_PARAM_KEY = "parameters";
	public static String RESPONSEMESSAGES_PARAM_KEY = "responseMessages";
	
	public static String NAME_PARAM_KEY = "name";
	public static String DEFAULTVALUE_PARAM_KEY = "defaultValue";	
	public static String REQUIRED_PARAM_KEY = "required";	
	public static String PARAMTYPE_PARAM_KEY = "paramType";	
	public static String PARAMTYPE_HEADER = "header";
	public static String PARAMTYPE_QUERY = "query";
	public static String PARAMTYPE_BODY = "body";	

	//interface respone swagger keys
	public static String CODE_PARAM_KEY = "code";
	public static String MESSAGE_PARAM_KEY = "message";
	public static String RESPONSEMODEL_PARAM_KEY = "responseModel";	
}
