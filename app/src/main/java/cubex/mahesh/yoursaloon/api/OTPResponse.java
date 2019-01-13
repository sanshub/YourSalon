package cubex.mahesh.yoursaloon.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPResponse {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("ResponseStatus")
@Expose
private String responseStatus;
@SerializedName("Data")
@Expose
private Data data;
@SerializedName("Error")
@Expose
private Error error;

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public String getResponseStatus() {
return responseStatus;
}

public void setResponseStatus(String responseStatus) {
this.responseStatus = responseStatus;
}

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

public Error getError() {
return error;
}

public void setError(Error error) {
this.error = error;
}

}