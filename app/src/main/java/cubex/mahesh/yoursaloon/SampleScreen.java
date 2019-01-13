package cubex.mahesh.yoursaloon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Base64;

import cubex.mahesh.yoursaloon.payment.bean.ErrorsBean;
import cubex.mahesh.yoursaloon.payment.bean.PaymentRequestBean;
import cubex.mahesh.yoursaloon.payment.bean.PaymentResponseBean;
import cubex.mahesh.yoursaloon.payment.bean.SourceRequest;
import cubex.mahesh.yoursaloon.payment.main.MoyasarClient;
import cubex.mahesh.yoursaloon.payment.main.MoyasarService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SampleScreen extends AppCompatActivity {
    public static final String API_BASE_URL = "https://apimig.moyasar.com/";
    private String publicKey;
    private String privateKey;
    private static Retrofit caller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_screen);

        setPublicKey("pk_test_61vRELhMuJ2Z23xsZJzvT9GmRwvah8xV4b2EqxnL");
        setPrivateKey("sk_test_65Fimzi7F6LfsSq6pd5S5ov3fvtb1aeQgqJgS7L9");
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
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

    public void makePayment(View view) {

        SourceRequest sqr = new SourceRequest();
        sqr.setUsername("u3041555Xolp");
        sqr.setFailUrl("https://dashboard.stg.moyasar.com/sadad/fail");
        sqr.setSuccessUrl("https://dashboard.stg.moyasar.com/sadad/success");
        sqr.setType("sadad");
        PaymentRequestBean payment = new PaymentRequestBean();
        payment.setAmount(199);
        payment.setSource(sqr);
        payment.setCurrency("SAR");
        payment.setDescription("Testing Java API Wrapprer for Moyasar");
        makePayment(payment);
    }
    PaymentResponseBean myPayment;

    public PaymentResponseBean makePayment(PaymentRequestBean payment){
   myPayment = new PaymentResponseBean();
        try{

            MoyasarService service = caller.create(MoyasarService.class);

            Call<PaymentResponseBean> call = service.pay(getPublicKey(), payment);

            call.enqueue(new Callback<PaymentResponseBean>() {
                @Override
                public void onResponse(Call<PaymentResponseBean> call, Response<PaymentResponseBean> response) {
                    if (response.isSuccessful()){
                        myPayment = response.body();
                        myPayment.setStatusCode(response.code());
                        myPayment.setMessage(response.message());
                    }
                    else {
                        myPayment.setStatusCode(response.code());
                        myPayment.setMessage(response.message());
                        myPayment.setErrorType("api_error");
                        ErrorsBean errors = null;
                        try {
                            errors = new ErrorsBean(response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            throw new Exception("MOYASAR API ERROR: " + errors);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<PaymentResponseBean> call, Throwable t) {

                }
            });

            Response<PaymentResponseBean> response = call.execute();
            if (response.isSuccessful()){
                // 200 OK

            }else{
                // API error

            }



        }catch(SocketTimeoutException tm)
        {
            System.err.println("API END POINT TIMED OUT");
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return myPayment;

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

    private String makeKey(String key){
        // This method is to prepare the key to match the format "Basic KEY"
        return "Basic "+ Base64.getEncoder().encodeToString(key.getBytes());
    }

}
