package cubex.mahesh.yoursaloon.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by srikanthk on 10/13/2018.
 */

public class Error {

    @SerializedName("ErrorCode")
    @Expose
    private Integer ErrorCode;
    @SerializedName("MessageAr")
    @Expose
    private String MessageAr;

    public Integer getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(Integer errorCode) {
        ErrorCode = errorCode;
    }

    public String getMessageAr() {
        return MessageAr;
    }

    public void setMessageAr(String messageAr) {
        MessageAr = messageAr;
    }

    public String getMessageEn() {
        return MessageEn;
    }

    public void setMessageEn(String messageEn) {
        MessageEn = messageEn;
    }

    @SerializedName("MessageEn")
    @Expose
    private String MessageEn;
}
