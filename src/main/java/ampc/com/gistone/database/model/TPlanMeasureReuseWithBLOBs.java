package ampc.com.gistone.database.model;

public class TPlanMeasureReuseWithBLOBs extends TPlanMeasureReuse {
    private String measureContent;

    private String tablePool;

    private String tableItem;

    private String tableRatio;

    public String getMeasureContent() {
        return measureContent;
    }

    public void setMeasureContent(String measureContent) {
        this.measureContent = measureContent == null ? null : measureContent.trim();
    }

    public String getTablePool() {
        return tablePool;
    }

    public void setTablePool(String tablePool) {
        this.tablePool = tablePool == null ? null : tablePool.trim();
    }

    public String getTableItem() {
        return tableItem;
    }

    public void setTableItem(String tableItem) {
        this.tableItem = tableItem == null ? null : tableItem.trim();
    }

    public String getTableRatio() {
        return tableRatio;
    }

    public void setTableRatio(String tableRatio) {
        this.tableRatio = tableRatio == null ? null : tableRatio.trim();
    }
}