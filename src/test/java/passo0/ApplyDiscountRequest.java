package passo0;

public class ApplyDiscountRequest {

    private String typeOfApply;
    private Integer id;
    private Integer percentage;

    public ApplyDiscountRequest(){}

    public ApplyDiscountRequest(String typeOfApply, Integer id, Integer percentage) {
        this.typeOfApply = typeOfApply;
        this.id = id;
        this.percentage = percentage;
    }

    public String getTypeOfApply() {
        return typeOfApply;
    }

    public void setTypeOfApply(String typeOfApply) {
        this.typeOfApply = typeOfApply;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}
