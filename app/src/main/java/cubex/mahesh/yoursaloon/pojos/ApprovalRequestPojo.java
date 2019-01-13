package cubex.mahesh.yoursaloon.pojos;

public class ApprovalRequestPojo {

    String  customerID, customerType, imageUrl;

    public ApprovalRequestPojo(String customerID, String customerType, String imageUrl) {
        this.customerID = customerID;
        this.customerType = customerType;
        this.imageUrl = imageUrl;
    }

    public ApprovalRequestPojo() {
    }



    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
