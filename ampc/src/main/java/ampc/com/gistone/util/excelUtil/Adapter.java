package ampc.com.gistone.util.excelUtil;

import java.util.List;
import java.util.Map;

/**
 * 将Excel中的下拉列表数据组织成java实体类TemplateDictionary
 * 适配器
 */
public abstract class Adapter {

    public Object getDic() {
        return dic;
    }

    public void setDic(Object dic) {
        this.dic = dic;
    }

    public Map<String, List<TemplateDictionary>> getDicMap() {
        return dicMap;
    }

    public void setDicMap(Map<String, List<TemplateDictionary>> dicMap) {
        this.dicMap = dicMap;
    }

    /**
     * 核心字典数据项
     */
    protected Object dic;

    /**
     * 字典map
     */
    protected Map<String, List<TemplateDictionary>> dicMap;

    /**
     * 构建通用返回结构
     * @return
     */
    public abstract Map<String, List<TemplateDictionary>> build();
}
