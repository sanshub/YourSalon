package cubex.mahesh.yoursaloon.payment.main;

import java.net.SocketTimeoutException;
import java.util.Base64;
import java.util.UUID;


import cubex.mahesh.yoursaloon.payment.bean.ErrorsBean;
import cubex.mahesh.yoursaloon.payment.bean.PaymentRequestBean;
import cubex.mahesh.yoursaloon.payment.bean.PaymentResponseBean;
import cubex.mahesh.yoursaloon.payment.bean.PaymentsResponseBean;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoyasarClient {
	
	public static final String API_BASE_URL = "https://apimig.moyasar.com/";
	private String publicKey; 
	private String privateKey;
	private static Retrofit caller;
	
		
	public MoyasarClient(String publicKey, String privateKey, boolean enable_logging) {
		setPublicKey(publicKey);
		setPrivateKey(privateKey);
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();  
		if(enable_logging){
			// set your desired log level
			logging.setLevel(Level.BODY);
		}
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  
		// add your other interceptors …

		// add logging as last interceptor
		httpClient.addInterceptor(logging); // <-- this is the important line!
		caller  = new Retrofit.Builder()
				.baseUrl(API_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
				.build();
}
	
	
	
	public Retrofit getCaller(){
		return caller; 
	}

	public PaymentResponseBean makePayment(PaymentRequestBean payment){
		PaymentResponseBean myPayment = new PaymentResponseBean();
		try{
			
			MoyasarService service = caller.create(MoyasarService.class);
			
			Call<PaymentResponseBean> call = service.pay(getPublicKey(), payment);
			
			Response<PaymentResponseBean> response = call.execute(); 
			if (response.isSuccessful()){
				// 200 OK 
				myPayment = response.body();
				myPayment.setStatusCode(response.code());
				myPayment.setMessage(response.message());
			}else{
				// API error  
				myPayment.setStatusCode(response.code());
				myPayment.setMessage(response.message());
				myPayment.setErrorType("api_error");
				ErrorsBean errors = new ErrorsBean(response.errorBody().string());
				throw new Exception("MOYASAR API ERROR: " + errors);
			}
			
	
			/** Async. call it dose not fit the case.. 
			call.enqueue(new Callback<PaymentResponseBean>(){

				@Override
				public void onFailure(Call<PaymentResponseBean> payment, Throwable e) {
					System.out.println("Error Happend Payments Object: \n"+payment );
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call<PaymentResponseBean> payment,
						Response<PaymentResponseBean> respons) {
					System.out.println("Response ---> \n"+ respons.body() );
//					System.out.println("Payments --> \n" + payment.toString()); 
					 if (respons.isSuccessful()){
						 // We got 200 OK
						 myPayment.setAmount(respons.body().getAmount());
						 myPayment.setDescription(respons.body().getDescription());
					 }else{
						 // WE GOT AN ERROR
					 }
				}

				
			}); **/
			
		}catch(SocketTimeoutException tm)
		{
			System.err.println("API END POINT TIMED OUT");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return myPayment; 
	
	}
	
	
	public PaymentResponseBean getPayment(UUID paymentId){
		return null;
	}
	
	/**
	 * This Class will call Moyasar API to get all payment for this account. 
	 * As per the Moyasar Policy we should use Private key to get data. Therefore, we will use the private key to get the data. 
	 * @return ArrayList of Payments as an objects of PaymentResponse
	 */

	public PaymentsResponseBean getAllPayments(){
		PaymentsResponseBean paymentsList = new PaymentsResponseBean(); 
		try{
			
			MoyasarService service = caller.create(MoyasarService.class);
			
			Call<PaymentsResponseBean> call = service.getPayments(getPrivateKey());
			
			Response<PaymentsResponseBean> response = call.execute();
			
			if (response.isSuccessful()){
				// 200 OK 
				paymentsList = response.body();
				paymentsList.setStatusCode(response.code());
				paymentsList.setMessage(response.message());
				
			}else{
				// API error  
				System.err.println("Errors ===> " + response.errorBody().string().toString());
				paymentsList.setStatusCode(response.code());
				paymentsList.setMessage(response.message());
				paymentsList.setErrorType(response.errorBody().string().toString());
				throw new IllegalAccessException(paymentsList.getStatusCode() + ": " + paymentsList.getMessage() + "\n" + paymentsList.getErrorType());
			}
			
			/** The below is ASYNC call which we will support in feature not now!  
			call.enqueue(new Callback<PaymentsResponseBean>(){

				@Override
				public void onFailure(Call<PaymentsResponseBean> payments, Throwable e) {
					System.out.println("Error Happend Payments Object: \n"+payments );
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call<PaymentsResponseBean> payments,
						Response<PaymentsResponseBean> respons) {
					System.out.println("Response ---> \n"+ respons.body() );
					System.out.println("Payments --> \n" + payments.toString());
					
				}
				
			});**/
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return paymentsList; 
	}

	
	public PaymentResponseBean getPayment(String id){
		PaymentResponseBean myPayment = new PaymentResponseBean();
		try{
			if (id.isEmpty()){
				myPayment.setStatusCode(412);
				myPayment.setErrorType("client_error");
				myPayment.setMessage("Precondition Failed: Payment ID Cannot be Empty or Null");
				throw new IllegalArgumentException("Payment ID Cannot be Empty or Null");
			}
			
			MoyasarService service = caller.create(MoyasarService.class);
			
			Call<PaymentResponseBean> call = service.getPayment(getPrivateKey(),id);
			
			Response<PaymentResponseBean> response = call.execute(); 
			if (response.isSuccessful()){
				// 200 OK 
				myPayment = response.body();
				myPayment.setStatusCode(response.code());
				myPayment.setMessage(response.message());
			}else{
				// API error  
				myPayment.setStatusCode(response.code());
				myPayment.setMessage(response.message());
				myPayment.setErrorType("api_error");
			}
			
	
			/** Async. call it dose not fit the case.. 
			call.enqueue(new Callback<PaymentResponseBean>(){

				@Override
				public void onFailure(Call<PaymentResponseBean> payment, Throwable e) {
					System.out.println("Error Happend Payments Object: \n"+payment );
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call<PaymentResponseBean> payment,
						Response<PaymentResponseBean> respons) {
					System.out.println("Response ---> \n"+ respons.body() );
//					System.out.println("Payments --> \n" + payment.toString()); 
					 if (respons.isSuccessful()){
						 // We got 200 OK
						 myPayment.setAmount(respons.body().getAmount());
						 myPayment.setDescription(respons.body().getDescription());
					 }else{
						 // WE GOT AN ERROR
					 }
				}

				
			}); **/
			
		}catch(SocketTimeoutException tm)
		{
			System.err.println("API END POINT TIMED OUT");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return myPayment; 
	}
	
	
	private String makeKey(String key){ 
		// This method is to prepare the key to match the format "Basic KEY" 
		return "Basic "+Base64.getEncoder().encodeToString(key.getBytes());
	}
	
	
	public void setPublicKey(String publicKey){
		try{
			if (!publicKey.isEmpty()){
				this.publicKey = makeKey(publicKey);
			}
			else
				throw new IllegalArgumentException("Public Key Cannot be empty!"); 
		}catch (Exception e) {
			throw new IllegalArgumentException("Public Key Cannot be empty!");
		} 
	}

	public String getPublicKey() {
		return publicKey;
	}



	public String getPrivateKey() {
		return privateKey;
	}


	public void setPrivateKey(String privateKey) {
		try{
			if (!privateKey.isEmpty()){
				this.privateKey = makeKey(privateKey);
			}
			else
				throw new IllegalArgumentException("Private Key Cannot be empty!"); 
		}catch (Exception e) {
			throw new IllegalArgumentException("Private Key Cannot be empty!");
		} 
	}
	

}
