package cubex.mahesh.yoursaloon.payment.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentsResponseBean extends Response{

	@SerializedName("payments")
	ArrayList<PaymentResponseBean> payments;

	public List<PaymentResponseBean> getPayments() {
		return payments;
	}

	@Override
	public String toString() {
		String payments; 
		payments = "PaymentsResponseBean [payments=" ;
		for (PaymentResponseBean p: this.payments) {
			payments+= p.toString() ;
		}
		payments+= "]";
		return payments;
	}
	
	
}
