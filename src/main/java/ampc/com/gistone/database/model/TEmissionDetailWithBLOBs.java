package ampc.com.gistone.database.model;

public class TEmissionDetailWithBLOBs extends TEmissionDetail {
    private String emissionDetails;

    private String measureReduce;

    public String getEmissionDetails() {
        return emissionDetails;
    }

    public void setEmissionDetails(String emissionDetails) {
        this.emissionDetails = emissionDetails == null ? null : emissionDetails.trim();
    }

    public String getMeasureReduce() {
        return measureReduce;
    }

    public void setMeasureReduce(String measureReduce) {
        this.measureReduce = measureReduce == null ? null : measureReduce.trim();
    }
}