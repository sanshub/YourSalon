package cubex.mahesh.yoursaloon.payment.bean;

import com.google.gson.annotations.SerializedName;


public class PaymentResponseBean extends Response{
	
	@SerializedName("id")
	private String id; 
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("amount")
	private double amount; 
	
	@SerializedName("currency")
	private String currency;
	
	@SerializedName("description")
	private String description;	//payment description
	
	
	@SerializedName("fee")
	private double fee;
	
	@SerializedName("refunded")
	private double refunded;
	
	@SerializedName("refunded_at")
	private String refunded_at;
	
	@SerializedName("amount_format")
	private String amountFormat; 
	
	@SerializedName("fee_format")
	private String feeFormat;
	
	@SerializedName("invoice_id")
	private String invoiceID;
	
	@SerializedName("ip")
	private String ip;
	
	@SerializedName("created_at")
	private String createdOn;
	
	@SerializedName("updated_at")
	private String updatedON;
	
	@SerializedName("source")
	private SourceResponse source;
    
	public SourceResponse getSource() {
		return source;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getRefunded() {
		return refunded;
	}
	public void setRefunded(double refunded) {
		this.refunded = refunded;
	}
	public String getRefunded_at() {
		return refunded_at;
	}
	public void setRefunded_at(String refunded_at) {
		this.refunded_at = refunded_at;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public String getAmountFormat() {
		return amountFormat;
	}
	public void setAmountFormat(String amountFormat) {
		this.amountFormat = amountFormat;
	}
	public String getFeeFormat() {
		return feeFormat;
	}
	public void setFeeFormat(String feeFormat) {
		this.feeFormat = feeFormat;
	}
	public String getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getUpdatedON() {
		return updatedON;
	}
	public void setUpdatedON(String updatedON) {
		this.updatedON = updatedON;
	}
	@Override
	public String toString() {
		return "PaymentResponseBean [id=" + id + ", status=" + status + ", amount=" + amount + ", currency=" + currency
				+ ", description=" + description + ", fee=" + fee + ", refunded=" + refunded + ", refunded_at="
				+ refunded_at + ", amountFormat=" + amountFormat + ", feeFormat=" + feeFormat + ", invoiceID="
				+ invoiceID + ", ip=" + ip + ", createdOn=" + createdOn + ", updatedON=" + updatedON + ", source"
				+ getSource().toString() + "]";
	}
	
	
	
	
	
	
	
	
	
//	@Override
//	public String toString() {
//		return "PaymentResponseBean [id=" + id + ", status=" + status + ", amount=" + amount + ", currency=" + currency
//				+ ", description=" + description + ", fee=" + fee + ", amountFormat=" + amountFormat + ", feeFormat="
//				+ feeFormat + ", invoiceID=" + invoiceID + ", ip=" + ip + ", createdOn=" + createdOn + ", updatedON="
//				+ updatedON + ", source=" + source + "]";
//	}
	
	
	
	
}
