package teq.development.seatech;

public class ManufacturerSkeleton {

    String id;
    String name;
    String phone;
    String rmaRequired;
    String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRmaRequired() {
        return rmaRequired;
    }

    public void setRmaRequired(String rmaRequired) {
        this.rmaRequired = rmaRequired;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNeedProduct() {
        return needProduct;
    }

    public void setNeedProduct(String needProduct) {
        this.needProduct = needProduct;
    }

    String needProduct;
}
