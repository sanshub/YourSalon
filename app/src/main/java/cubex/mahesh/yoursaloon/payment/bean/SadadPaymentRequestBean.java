package cubex.mahesh.yoursaloon.payment.bean;

import com.google.gson.annotations.SerializedName;

public class SadadPaymentRequestBean extends SourceRequest{
	
	
	public SadadPaymentRequestBean() {
	}
	
	public SadadPaymentRequestBean(SourceRequest source) {
		this.username = source.username;
		this.successUrl = source.successUrl;
		this.failUrl = source.failUrl;
		this.type = "sadad";
	}


	@SerializedName("type")
	public String type = "sadad" ;
	@SerializedName("username")
	public String username; //sadad’s username
	@SerializedName("success_url")
	public String successUrl; //optional	success url page in customer ’s site. Use Moyasar’s success url if not given.
	@SerializedName("fail_url")
	public String failUrl; //optional	fail url page in customer ’s site. Use Moyasar’s fail url if not given.
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSuccess_url() {
		return successUrl;
	}
	public void setSuccess_url(String success_url) {
		this.successUrl = success_url;
	}
	public String getFail_url() {
		return failUrl;
	}
	public void setFail_url(String fail_url) {
		this.failUrl = fail_url;
	}
	
	
	
	
	
}
