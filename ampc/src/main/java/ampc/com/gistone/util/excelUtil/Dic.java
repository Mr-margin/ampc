package ampc.com.gistone.util.excelUtil;



import java.util.ArrayList;
import java.util.List;

/**
 * 字典项的实体类
 * Created by chf on 2017/2/8.
 */
public class Dic {

	//普通下拉列表的字典项值
    private List<EcloudDictionaryAll> list;
    //行政区划下拉列表字典项的值
    private List<TemplateDictionary> region;

    public List<TemplateDictionary> getRegion() {
        return region;
    }

    public void setRegion(List<TemplateDictionary> region) {
        this.region = region;
    }

    public List<EcloudDictionaryAll> getList() {
        return list;
    }

    public void setList(List<EcloudDictionaryAll> list) {
        this.list = list;
    }

    public static List<EcloudDictionaryAll> build() {
        List<EcloudDictionaryAll> list = new ArrayList<>();
        EcloudDictionaryAll d = new EcloudDictionaryAll();
        d.setSheetName("equipfueltype");
        d.setLevel1("表p1011_机组信息表");
        d.setLevel2("原煤");
        d.setLevel3("煤粉炉");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equipfueltype");
        d.setLevel1("表p1011_机组信息表");
        d.setLevel2("洗精煤");
        d.setLevel3("煤粉炉");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equipfueltype");
        d.setLevel1("表p1011_机组信息表");
        d.setLevel2("型煤");
        d.setLevel3("自动炉排层燃炉");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equipfueltype");
        d.setLevel1("表p1011_机组信息表");
        d.setLevel2("其它洗煤");
        d.setLevel3("自动炉排层燃炉");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equipfueltype");
        d.setLevel1("表p1011_机组信息表");
        d.setLevel2("洗精煤");
        d.setLevel3("流化床炉");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equipfueltype");
        d.setLevel1("表p1011_机组信息表");
        d.setLevel2("洗精煤");
        d.setLevel3("流化床炉");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equiptype");
        d.setLevel1("表s6041_土壤扬尘信息表");
        d.setLevel2("粘土");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equiptype");
        d.setLevel1("表s6041_土壤扬尘信息表");
        d.setLevel2("砂土");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("equiptype");
        d.setLevel1("表s6041_土壤扬尘信息表");
        d.setLevel2("壤土");
        list.add(d);
        d = new EcloudDictionaryAll();
        d.setSheetName("sindustrytype");
        d.setLevel1("表s6041_土壤扬尘信息表");
        d.setLevel2("荒地");
        list.add(d);
        return list;
    }

}
