package cubex.mahesh.yoursaloon.payment.bean;

public class CreditCardResponse extends SourceResponse {
	
	public CreditCardResponse(SourceResponse source) {
		this.cardNetwork = source.cardNetwork;
		this.cardNumber = source.cardNumber;
		this.errorCode = source.errorCode;
		this.message = source.message;
		this.name = source.name;
		this.transactionURL = source.transactionURL;
		this.type = source.type;
		this.username = source.username;
	}

	public String getName() {
		return name;
	}


	public String getCardNetwork() {
		return cardNetwork;
	}


	public String getCardNumber() {
		return cardNumber;
	}


	@Override
	public String toString() {
		return "CreditCardResponse [type=" + type + ", name=" + name + ", message=" + message + ", cardNetwork="
				+ cardNetwork + ", cardNumber=" + cardNumber + "]";
	}

	
}
