package cubex.mahesh.yoursaloon.payment.bean;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

public class ErrorsBean {

	@SerializedName("type")
	private String type; 
	@SerializedName("message")
	private String message; 
	@SerializedName("errors")
	private String errors;
	
	public ErrorsBean(String JSONSTREEM){
		JsonParser parser = new JsonParser(); 
		JsonElement jsonTree = parser.parse(JSONSTREEM);
		JsonObject  jobject = jsonTree.getAsJsonObject();
		setType(jobject.getAsJsonPrimitive("type").getAsString());
		setMessage(jobject.getAsJsonPrimitive("message").getAsString());
		try{
			jobject =  jobject.getAsJsonObject("errors");
			setErrors(jobject.toString());
		}catch (ClassCastException ce) {
			// Log class cast exception and handle it
				try{
					JsonArray errors  = jobject.getAsJsonArray("errors");
					setErrors(errors.toString());	
				}catch(ClassCastException anotherClassError) {
					JsonPrimitive errors =  jobject.getAsJsonPrimitive("errors");
					setErrors(errors.toString());
				}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
				
		
		
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;

	}


	public String getErrors() {
		return errors;
	}


	public void setErrors(String errors) {
		this.errors = errors;
	}


	@Override
	public String toString() {
		return "ErrorsBean [type=" + type + ", message=" + message + ", errors=" + errors + "]";
	}
	
	
	
	
}
