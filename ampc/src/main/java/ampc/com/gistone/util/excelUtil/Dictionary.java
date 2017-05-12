package ampc.com.gistone.util.excelUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 另一个字典项实体类
 * Created by Administrator on 2016/12/12.
 */
@JsonPropertyOrder({"keyParam","SO2", "NOx", "PM25", "VOC", "NH3", "PMcoarse", "PM10more", "BC", "OC", "CO2", "CO","dictionary","version","valueParam","excelName"})
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dictionary {
    public String keyParam;
//    public double SO2;
//    public double NOx;
//    public double PM25;
//    public double VOC;
//    public double NH3;
//    public double PMcoarse;
//    public double PM10more;
//    public double BC;
//    public double OC;
//    public double CO2;
//    public double CO;
    public String dictionary;
    public String version;
    public String valueParam;
    public String excelName;

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String excelName) {
        this.excelName = excelName;
    }

    public String getValueParam() {
        return valueParam;
    }

    public void setValueParam(String valueParam) {
        this.valueParam = valueParam;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKeyParam() {
        return keyParam;
    }

    public void setKeyParam(String keyParam) {
        this.keyParam = keyParam;
    }

   
}
