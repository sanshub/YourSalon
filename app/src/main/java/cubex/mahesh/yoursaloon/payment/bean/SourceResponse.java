package cubex.mahesh.yoursaloon.payment.bean;

import com.google.gson.annotations.SerializedName;

public class SourceResponse {
	
	@SerializedName("type")
	protected String type;
	
	// Sadad Attributes.. 
	@SerializedName("username")
	protected String username; //sadad’s username
	@SerializedName("error_code")
	protected String errorCode; 
	@SerializedName("transaction_url")
	protected String transactionURL;
	
	public String getTransactionURL() {
		return transactionURL;
	}



	// Credit Card attribute
	@SerializedName("name")
	protected String name;
	@SerializedName( "message")
	protected String message; 
	@SerializedName("company")
	protected String cardNetwork;
	@SerializedName("number")
	protected String cardNumber;
	
	
	public SourceResponse getPaymentTypeObj(){
		if (this.type.equals("creditcard")) {
			return new CreditCardResponse(this);
		}else if(this.type.equals("sadad")){
			return new SadadResponse(this);
		}else {
			throw new IllegalArgumentException("Uknown Source Type");
		}
	}



	public String getType() {
		return type;
	}

	
	public String getMessage() {
		return message;
	}



	@Override
	public String toString() {
		return getPaymentTypeObj().toString();
		
	}

	
	

}
