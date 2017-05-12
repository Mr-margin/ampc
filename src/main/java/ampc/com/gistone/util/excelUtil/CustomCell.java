package ampc.com.gistone.util.excelUtil;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * 单元格的实体类
 */
public class CustomCell {
    /** 所在行的键值 */
    private String rowKey;
    /** 单元格内容 */
    private String v;
    /** 单元格宽 */
    private Integer w;
    /** 单元格高 */
    private Integer h;
    /** 合并区域键值 firstRow-lastRow-firstColumn-lastColumn */
    private String regionKey;
    /** 合并区域的值 regionValue */
    private String regionValue;
    /** 横向对齐 */
    private short alignment;
    /** 纵向对齐 */
    private short verticalAlignment;
    /** 色值 **/
    Object color = null;
    /** 样式 */
    private CellStyle style;

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public int getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getRegionKey() {
        return regionKey;
    }

    public void setRegionKey(String regionKey) {
        this.regionKey = regionKey;
    }

    public short getAlignment() {
        return alignment;
    }

    public void setAlignment(short alignment) {
        this.alignment = alignment;
    }

    public short getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(short verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public Object getColor() {
        return color;
    }

    public void setColor(Object color) {
        this.color = color;
    }

    public CellStyle getStyle() {
        return style;
    }

    public void setStyle(CellStyle style) {
        this.style = style;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getRegionValue() {
        return regionValue;
    }

    public void setRegionValue(String regionValue) {
        this.regionValue = regionValue;
    }
}
