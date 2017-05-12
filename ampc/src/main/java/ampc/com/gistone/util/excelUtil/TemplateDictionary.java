package ampc.com.gistone.util.excelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典项实体类
 * Created by chf on 2016/12/8.
 */
public class TemplateDictionary {

    private String value;
    private String key;
    private String parentKey;

    private List<TemplateDictionary> child = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public List<TemplateDictionary> getChild() {
        return child;
    }

    public void setChild(List<TemplateDictionary> child) {
        this.child = child;
    }
}
