package com.intuit.services.common.raml2swagger.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.intuit.services.common.raml2swagger.service.constants.SwaggerConstants;


@Path("/v1")
public class RAML2Swagger implements SwaggerConstants{

	Logger log = Logger.getLogger("RAML2Swagger");
	
	 
	 @GET
	 @Produces({MediaType.APPLICATION_JSON})
	 //@Consumes({MediaType.APPLICATION_JSON})
	 //@CacheMaxAge(time = 5, unit = TimeUnit.MINUTES)
	 /**
	  * this method creates the top level swagger json doc that simply tells all the resources exists.
	  * 
	  * @param filePath
	  * 	name of the filePath (either url or filename that exists in the classpath)
	  * 
	  * {"apiVersion": "1.0.0","swaggerVersion": "1.2","apis": [{"path": "/v1/orders","description": "This service allows Offering Systems to place an order into enterprise systems."}]}		   
	  * 
	  * @return
	  * 	Response object that contains JOSN file
	  */
	 public Response convertRaml2SwaggerTopLevelJSON(@QueryParam("filePath") String filePath, @QueryParam("basePath") String basePath)
	 {
		 	String swaggerJSONContents = "";
		   log.info("response.getStatus() : ");
		   
		   //List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlLocation);
		   Raml raml = new RamlDocumentBuilder().build(filePath);
		   
		   JSONObject swaggerJSON = new JSONObject();
		   try 
		   {
			   //ResourceListing swaggerResource = ResourceListing.
			   swaggerJSON.put(APIVERSION_PARAM_KEY, APIVERSION_PARAM_VALUE);
			   swaggerJSON.put(SWAGGERVERSION_PARAM_KEY, SWAGGERVERSION_PARAM_VALUE);
		   
			   Collection<JSONObject> apiList = new ArrayList<JSONObject>();
			   for ( Map.Entry<String, Resource> resourceEntry : raml.getResources().entrySet() )
			   {
				   //Map<String,String> apiMap = new HashMap<String,String>();
				   JSONObject apiMap = new JSONObject();
				   apiMap.put( PATH_PARAM_KEY, resourceEntry.getValue().getUri()+"?filePath="+filePath+"&basePath="+basePath);
				   apiMap.put(DESCRIPTION_PARAM_KEY, resourceEntry.getValue().getDescription());
				   apiList.add(apiMap);
			   }
			   
			   swaggerJSON.put(APIS_PARAM_KEY, apiList);
			   
			   swaggerJSONContents = swaggerJSON.toString();
		   } 
		   catch (JSONException e) 
		   {
			   e.printStackTrace();
			   return Response.status(500).entity(swaggerJSONContents)
				    		//.cacheControl(control)
				    		//.header("Access-Control-Allow-Origin", "*")
				            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").
				            build();
		   }
		   		   
		    return Response.ok().entity(swaggerJSONContents)
		    		//.cacheControl(control)
		    		//.header("Access-Control-Allow-Origin", "*")
		            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").
		            build();
	 }	
	 
	 
	 
	 @Path("/{subResources:.*}")
	 @GET
	 @Produces({MediaType.APPLICATION_JSON})
	 /**
	  * This method creates the detail level swagger json doc for a particular resource like (/v1/payments etc.)
	  * 
	  * @param filePath
	  * 	name of the filePath (either url or filename that exists in the classpath)
	  * 
	  * @param basePath
	  * 	name of the path that we set as a basepath for your swagger doc (this is the url that swagger uses during 'try it' calls). If it is missing than service will set it to '/'
	  * 
	  * @return
	  * 	Response object that contains JOSN file
	  */
	 public Response convertRaml2SwaggerDetailJSON(@Context UriInfo uriInfo,@QueryParam("filePath") String filePath, @QueryParam("basePath") String basePath)
	 {
		 	String swaggerJSONContents = "";
		 	log.info("uriInfo.getPath(): "+uriInfo.getPath());
		 	log.info("uriInfo.getBaseUri(): "+uriInfo.getBaseUri());
		 	String subResourceURI = null;
		 	for ( Map.Entry<String, List<String>> pp : uriInfo.getPathParameters().entrySet() )
		 	{
		 		log.info("Path Param: "+pp.getKey() + "," + pp.getValue().get(0));
		 		subResourceURI = "/" + pp.getValue().get(0);
		 	}
		 	log.info("uriInfo.getQueryParameters(): "+uriInfo.getQueryParameters());
		 	log.info("filePath: "+filePath);
		   
		   //List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlLocation);
		   Raml raml = new RamlDocumentBuilder().build(filePath);
		   
		   JSONObject swaggerJSON = new JSONObject();
		   try 
		   {
			   swaggerJSON.put(APIVERSION_PARAM_KEY, APIVERSION_PARAM_VALUE);
			   swaggerJSON.put(SWAGGERVERSION_PARAM_KEY, SWAGGERVERSION_PARAM_VALUE);

			   if ( basePath==null || basePath.equals("") )
			   {
				   basePath= "/";
			   }
			   swaggerJSON.put( PATH_PARAM_KEY, basePath);
			   swaggerJSON.put( DESCRIPTION_PARAM_KEY, subResourceURI);
		   
			   Collection<JSONObject> apiList = new ArrayList<JSONObject>();
			   for ( Map.Entry<String, Resource> resourceEntry : raml.getResources().entrySet() )
			   {
				   log.info("resourceEntry.getValue().getUri() : " + resourceEntry.getValue().getUri() );
				   
				   if ( resourceEntry.getValue().getUri().equalsIgnoreCase(subResourceURI) )
				   {
					   JSONObject topLevelResourceSwaggerData = addResourceSwaggerData(resourceEntry);
					   apiList.add(topLevelResourceSwaggerData);
					   for ( Map.Entry<String, Resource> subResourceEntry : resourceEntry.getValue().getResources().entrySet() )
					   {
						   apiList.add(addResourceSwaggerData(subResourceEntry));						   
					   }
				   }
			   }
			   
			   swaggerJSON.put(APIS_PARAM_KEY, apiList);
			   
			   swaggerJSONContents = swaggerJSON.toString();
		   } 
		   catch (JSONException e) 
		   {
			   e.printStackTrace();
			   //throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
			   return Response.status(500).entity(swaggerJSONContents)
				    		//.cacheControl(control)
				    		//.header("Access-Control-Allow-Origin", "*")
				            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").
				            build();
		   }
		   		   
		    return Response.ok().entity(swaggerJSONContents)
		    		//.cacheControl(control)
		    		//.header("Access-Control-Allow-Origin", "*")
		            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").
		            build();
	 }

	 
	 
	 /**
	  * generates the resource level swagger data
	  * 
	  * @param resourceEntry
	  * 	resource for which swagger doc will be generated
	  * 
	  * @return
	  * 	Swagger json object
	  */
	 private JSONObject addResourceSwaggerData(Map.Entry<String, Resource> resourceEntry) {
		JSONObject apiMap = new JSONObject();
		try 
		{
			apiMap.put(PATH_PARAM_KEY, resourceEntry.getValue().getUri());
			List<JSONObject> operations = new ArrayList<JSONObject>();
			for ( Map.Entry<ActionType, Action> action : resourceEntry.getValue().getActions().entrySet() )
			{
				addSwaggerResourceInterfaceInfo(operations,action);
			}
		   apiMap.put( OPERATIONS_PARAM_KEY, operations);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return apiMap;
	}

	 
	 /**
	  * add swagger operations info for a particular interface (like method name, its parameters etc.)
	  * 
	  * @param operations
	  * 
	  * @param action
	  * 	Action object coming out from the RAML specs
	  */
	private void addSwaggerResourceInterfaceInfo(List<JSONObject> operations,Map.Entry<ActionType, Action> action) 
	{
		JSONObject operation = new JSONObject();
		try 
		{
			operation.put(METHOD_PARAM_KEY, action.getKey().toString());
			operation.put(SUMMARY_PARAM_KEY, action.getValue().getDescription());
			operation.put(NOTES_PARAM_KEY, action.getValue().getDescription());
			operation.put(NICKNAME_PARAM_KEY, action.getValue().getDescription());
			operation.put(TYPE_PARAM_KEY, action.getValue().getType().name());
			
			//TODO::: it has to be fixed by reading from RAML specs and set it here
			List<String> mediaTypes = new ArrayList<String>();
			mediaTypes.add( MEDIATYPE_APPLICATION_XML );mediaTypes.add( MEDIATYPE_APPLICATION_JSON );
			operation.put(PRODUCES_MEDIATYPE_PARAM_KEY, mediaTypes);
			operation.put(CONSUMES_MEDIATYPE_PARAM_KEY, mediaTypes);
		   
			//read parameters headers,body,query etc.
		   Collection<JSONObject> parameters = new ArrayList<JSONObject>();
		   log.info("action.getValue().getQueryParameters() : " + action.getValue().getQueryParameters() );

		   //add header/query & body params
		   addSwaggerResourceInterfaceHeaderParametersInfo(action, parameters);
		   addSwaggerResourceInterfaceQueryParametersInfo(action, parameters);
		   addSwaggerResourceInterfaceBodyParametersInfo(action, parameters);
		   operation.put(PARAMETERS_PARAM_KEY,parameters);	
		   
		   //adds response data (response codes & description)
		   operation.put( RESPONSEMESSAGES_PARAM_KEY, addSwaggerResourceInterfaceResponseInfo(action) );

		   operations.add(operation);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		   
	}


	
	/**
	 * add swagger resource interface parameter info (header/query/body etc.) 
	 * 
	 * @param action
	 * 
	 * @param parameters
	 * 
	 * @throws JSONException
	 */
	private JSONObject addSwaggerResourceInterfaceParametersInfo(String name,String defaultValue,String description, boolean isRequired, String type, String paramType) 
			throws JSONException {
		   JSONObject qp = new JSONObject();
		   qp.put(NAME_PARAM_KEY, name);
		   qp.put(DEFAULTVALUE_PARAM_KEY, defaultValue);
		   qp.put(DESCRIPTION_PARAM_KEY, description);
		   qp.put(REQUIRED_PARAM_KEY, String.valueOf(isRequired));
		   qp.put(TYPE_PARAM_KEY, type);
		   qp.put(PARAMTYPE_PARAM_KEY, paramType);
		   return qp;
	}	
	
	
	
	/**
	 * add swagger resource interface header param info 
	 * 
	 * @param action
	 * 
	 * @param parameters
	 * 
	 * @throws JSONException
	 */
	private void addSwaggerResourceInterfaceHeaderParametersInfo(
			Map.Entry<ActionType, Action> action,
			Collection<JSONObject> parameters) throws JSONException {
		for ( Map.Entry<String, Header> headerMap : action.getValue().getHeaders().entrySet() )
		   {			
			   JSONObject qp = new JSONObject();
			   qp = addSwaggerResourceInterfaceParametersInfo ( headerMap.getValue().getDisplayName(), headerMap.getValue().getDefaultValue(), 
					   										headerMap.getValue().getDescription(), headerMap.getValue().isRequired(),
					   										headerMap.getValue().getType().toString(), PARAMTYPE_HEADER);
/*			   qp.put( NAME_PARAM_KEY, headerMap.getValue().getDisplayName());
			   qp.put( DESCRIPTION_PARAM_KEY, headerMap.getValue().getDescription());
			   qp.put( DEFAULTVALUE_PARAM_KEY, headerMap.getValue().getDefaultValue());
			   qp.put( REQUIRED_PARAM_KEY, String.valueOf(headerMap.getValue().isRequired()));
			   qp.put( TYPE_PARAM_KEY, headerMap.getValue().getType().toString());
			   qp.put( PARAMTYPE_PARAM_KEY, PARAMTYPE_HEADER);
*/
			   parameters.add(qp);
		   }
	}


	/**
	 * add swagger resource interface query param info 
	 * 
	 * @param action
	 * 
	 * @param parameters
	 * 
	 * @throws JSONException
	 */
	private void addSwaggerResourceInterfaceQueryParametersInfo(
			Map.Entry<ActionType, Action> action,
			Collection<JSONObject> parameters) throws JSONException {
		for ( Map.Entry<String, QueryParameter> queryParamMap : action.getValue().getQueryParameters().entrySet() )
		   {			
			   log.info("queryParamMap.getValue().getDisplayName() : " + queryParamMap.getValue().getDisplayName() );
			   JSONObject qp = new JSONObject();
			   
			   qp = addSwaggerResourceInterfaceParametersInfo ( queryParamMap.getValue().getDisplayName(), queryParamMap.getValue().getDefaultValue(), 
					   											queryParamMap.getValue().getDescription(), queryParamMap.getValue().isRequired(),
					   											queryParamMap.getValue().getType().toString(), PARAMTYPE_QUERY);
			   
/*			   
			   qp.put(NAME_PARAM_KEY, queryParamMap.getValue().getDisplayName());
			   qp.put(DEFAULTVALUE_PARAM_KEY, queryParamMap.getValue().getDefaultValue());
			   qp.put(DESCRIPTION_PARAM_KEY, queryParamMap.getValue().getDescription());
			   qp.put(REQUIRED_PARAM_KEY, String.valueOf(queryParamMap.getValue().isRequired()));
			   qp.put(REQUIRED_PARAM_KEY, queryParamMap.getValue().getType().toString());
			   qp.put(PARAMTYPE_PARAM_KEY,PARAMTYPE_QUERY);*/
			   parameters.add(qp);
		   }
	}

	
	/**
	 * add swagger resource interface body param info 
	 * 
	 * @param action
	 * 
	 * @param parameters
	 * 
	 * @throws JSONException
	 */	
	private void addSwaggerResourceInterfaceBodyParametersInfo(Map.Entry<ActionType, Action> action,
			Collection<JSONObject> parameters) throws JSONException {
		for ( Map.Entry<String, MimeType> mimeType : action.getValue().getBody().entrySet() )
		   {			
			   log.info("mimeType.getValue().getType() : " + mimeType.getValue().getType());
			   JSONObject qp = new JSONObject();

			   qp = addSwaggerResourceInterfaceParametersInfo ( "body", mimeType.getValue().getExample(), 
					   										    "body", //TODO:: how to read it from RAML spec 
					   										    Boolean.TRUE,mimeType.getValue().getType(), PARAMTYPE_BODY);
/*
			   qp.put(NAME_PARAM_KEY, "body");
			   qp.put(DEFAULTVALUE_PARAM_KEY, mimeType.getValue().getExample());
			   qp.put(DESCRIPTION_PARAM_KEY, "body");//TODO:: how to read it from RAML spec
			   qp.put(DESCRIPTION_PARAM_KEY, Boolean.TRUE);
			   qp.put(REQUIRED_PARAM_KEY, mimeType.getValue().getType());
			   qp.put(PARAMTYPE_PARAM_KEY,"body");*/
			   parameters.add(qp);
		   }
	}		 
	
	
	/**
	 * add swagger resource interface respone data which will be in the following format.
	 * 
	 *
	 *{
	 * "code": 500,
	 * "message": "It could happen because of multiple reason like downstream service failed to respond within specified time etc.",
	 * "responseModel": ""
	 * },
	 * 
	 * @param action
	 * 
	 * @return  List<JSONObject>
	 * 		list of jsonobject that contains all the responses for a particular method/interface
	 * 
	 * @throws JSONException
	 */
	private List<JSONObject> addSwaggerResourceInterfaceResponseInfo(Map.Entry<ActionType, Action> action) throws JSONException {
		   List<JSONObject> responselist = new ArrayList<JSONObject>();
		for ( Map.Entry<String, org.raml.model.Response> responsesMap : action.getValue().getResponses().entrySet() )
		   {			
			   JSONObject responseData = new JSONObject();
			   responseData.put( CODE_PARAM_KEY, responsesMap.getKey());
			   responseData.put( MESSAGE_PARAM_KEY, "" + "error occured");//TODO:: how to read it from RAML, can it be an example string
			   responseData.put( RESPONSEMODEL_PARAM_KEY, "" + "");//TODO:: how to read it from RAML
			   responselist.add(responseData);
		   }
		return responselist;
	}
	
}
