package cubex.mahesh.yoursaloon.payment.bean;

import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

public class Response {

	@SerializedName("code")
	private int statusCode;
	@SerializedName("type")
	private String errorType; 
	@SerializedName("message")
	private String message;
	@SerializedName("errors")
	private HashMap<String, String> errors;
	
	public Response() {
		super();
	}


	public Response(int statusCode, String errorType, String message, HashMap<String, String> errors) {
		super();
		this.statusCode = statusCode;
		this.errorType = errorType;
		this.message = message;
		this.errors = errors;
	}
	
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public HashMap<String, String> getErrors() {
		return errors;
	}
	public void setErrors(HashMap<String, String> errors) {
		this.errors = errors;
	} 	
	
	
	public boolean isSuccessful(){
		if (getStatusCode() == 200)
			return true; 
		return false; 
	}
	
}
