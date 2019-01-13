package cubex.mahesh.yoursaloon.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("msgId")
@Expose
private String msgId;
@SerializedName("result")
@Expose
private Integer result;
@SerializedName("MessageAr")
@Expose
private String messageAr;
@SerializedName("MessageEn")
@Expose
private String messageEn;
@SerializedName("msgLength")
@Expose
private Integer msgLength;
@SerializedName("countNumber")
@Expose
private Integer countNumber;
@SerializedName("point")
@Expose
private Integer point;
@SerializedName("rejectedNumber")
@Expose
private String rejectedNumber;
@SerializedName("acceptedNumber")
@Expose
private String acceptedNumber;

public String getMsgId() {
return msgId;
}

public void setMsgId(String msgId) {
this.msgId = msgId;
}

public Integer getResult() {
return result;
}

public void setResult(Integer result) {
this.result = result;
}

public String getMessageAr() {
return messageAr;
}

public void setMessageAr(String messageAr) {
this.messageAr = messageAr;
}

public String getMessageEn() {
return messageEn;
}

public void setMessageEn(String messageEn) {
this.messageEn = messageEn;
}

public Integer getMsgLength() {
return msgLength;
}

public void setMsgLength(Integer msgLength) {
this.msgLength = msgLength;
}

public Integer getCountNumber() {
return countNumber;
}

public void setCountNumber(Integer countNumber) {
this.countNumber = countNumber;
}

public Integer getPoint() {
return point;
}

public void setPoint(Integer point) {
this.point = point;
}

public String getRejectedNumber() {
return rejectedNumber;
}

public void setRejectedNumber(String rejectedNumber) {
this.rejectedNumber = rejectedNumber;
}

public String getAcceptedNumber() {
return acceptedNumber;
}

public void setAcceptedNumber(String acceptedNumber) {
this.acceptedNumber = acceptedNumber;
}

}
