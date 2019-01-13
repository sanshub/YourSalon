package cubex.mahesh.yoursaloon.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
 
 
public interface ApiInterface {
    @GET("msgSend.php")
    Call<OTPResponse> sendOTP(@Query("mobile") String mobile,
                              @Query("password") String password,
                              @Query("numbers") String numbers,
                              @Query("sender") String sender,
                              @Query("msg") String msg,
                              @Query("applicationType") String applicationType,
                              @Query("lang") String lang,
                              @Query("returnJson") String returnJson);
 

}