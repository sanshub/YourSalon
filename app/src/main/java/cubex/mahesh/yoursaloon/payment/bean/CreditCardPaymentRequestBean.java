package cubex.mahesh.yoursaloon.payment.bean;

public class CreditCardPaymentRequestBean extends SourceRequest{
	
	
	
	public CreditCardPaymentRequestBean(SourceRequest source) {
		this.name = source.name;
		this.number = source.number;
		this.cvc = source.cvc;
		this.month = source.month;
		this.year = source.year;
		this.type = "creditcard";
	}

	/**
	 * The type of payment source. Should be “creditcard”
	 */
	public String type = "creditcard" ;
	/**
	 * Card holder’s name
	 */
	private String name; 
	/**
	 * The card number
	 */
	private String number;  
	/**
	 * Card security code.
	 * */
	private String cvc;
	
	/**
	 * 	Two digit number representing the card’s expiration month.
	 */
	 private String month;
	
	 /**
	 * 	Four digit number representing the card’s expiration year.
	 */
	 private String year;	 
	 
	 
	 
	 
	 public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCvc() {
		return cvc;
	}

	public void setCvc(String cvc) {
		this.cvc = cvc;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
		

	
}
