package ampc.com.gistone.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.makenv.erp.util.json.Jsons;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tumor on 2017/3/8.
 */

@JsonPropertyOrder({"SO2", "NOx", "PM25", "VOC", "NH3", "PMcoarse", "PM10more", "BC", "OC", "CO2", "CO"})	//属性排序@JsonPropertyOrder，注释在类声明中;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)	//是为了控制返回的json字符串显示哪些字段。这里的设置是为null的字段不显示
@JsonIgnoreProperties(ignoreUnknown = true)		//此注解是类注解，作用是json序列化时将java bean中的一些属性忽略掉，序列化和反序列化都受影响
public class SpeciesVector {
    public static final double nodata = -9999.0D;
    public static final int length = 11;
    public double SO2;
    public double NOx;
    public double PM25;
    public double VOC;
    public double NH3;
    public double PMcoarse;
    public double PM10more;
    public double BC;
    public double OC;
    public double CO2;
    public double CO;

    public SpeciesVector() {
        this(-9999.0D);
    }

    public SpeciesVector(String sp) throws IOException {
        this.put((SpeciesVector) Jsons.jsonToObj(sp, SpeciesVector.class));
    }

    public SpeciesVector(SpeciesVector v) {
        this.put(v);
    }

    public SpeciesVector(double v) {
        this.SO2 = v;
        this.NOx = v;
        this.PM25 = v;
        this.VOC = v;
        this.NH3 = v;
        this.PMcoarse = v;
        this.PM10more = v;
        this.BC = v;
        this.OC = v;
        this.CO2 = v;
        this.CO = v;
    }

    public SpeciesVector(double[] arr) {
        this();
        this.put((double[]) arr);
    }

    public Map<String, Double> toMap() {
        TreeMap m = new TreeMap();
        m.put("SO2", Double.valueOf(this.SO2));
        m.put("NOX", Double.valueOf(this.NOx));
        m.put("PM25", Double.valueOf(this.PM25));
        m.put("VOC", Double.valueOf(this.VOC));
        m.put("NH3", Double.valueOf(this.NH3));
        m.put("PMcoarse", Double.valueOf(this.PMcoarse));
        m.put("PM10more", Double.valueOf(this.PM10more));
        m.put("BC", Double.valueOf(this.BC));
        m.put("OC", Double.valueOf(this.OC));
        m.put("CO2", Double.valueOf(this.CO2));
        m.put("CO", Double.valueOf(this.CO));
        return m;
    }

    public double[] toArray() {
        return new double[]{this.SO2, this.NOx, this.PM25, this.VOC, this.NH3, this.PMcoarse, this.PM10more, this.BC, this.OC, this.CO2, this.CO};
    }

    public double[] toArray(double[] v) {
        v[0] = this.SO2;
        v[1] = this.NOx;
        v[2] = this.PM25;
        v[3] = this.VOC;
        v[4] = this.NH3;
        v[5] = this.PMcoarse;
        v[6] = this.PM10more;
        v[7] = this.BC;
        v[8] = this.OC;
        v[9] = this.CO2;
        v[10] = this.CO;
        return v;
    }

    public void put(String species, double value) {
        String var4 = species.toUpperCase();
        byte var5 = -1;
        switch (var4.hashCode()) {
            case -1241257423:
                if (var4.equals("PM10MORE")) {
                    var5 = 7;
                }
                break;
            case -697658132:
                if (var4.equals("PMCOARSE")) {
                    var5 = 6;
                }
                break;
            case 2113:
                if (var4.equals("BC")) {
                    var5 = 8;
                }
                break;
            case 2156:
                if (var4.equals("CO")) {
                    var5 = 11;
                }
                break;
            case 2516:
                if (var4.equals("OC")) {
                    var5 = 9;
                }
                break;
            case 66886:
                if (var4.equals("CO2")) {
                    var5 = 10;
                }
                break;
            case 77241:
                if (var4.equals("NH3")) {
                    var5 = 5;
                }
                break;
            case 77495:
                if (var4.equals("NOX")) {
                    var5 = 1;
                }
                break;
            case 82262:
                if (var4.equals("SO2")) {
                    var5 = 0;
                }
                break;
            case 85162:
                if (var4.equals("VOC")) {
                    var5 = 4;
                }
                break;
            case 2458880:
                if (var4.equals("PM25")) {
                    var5 = 3;
                }
                break;
            case 76225116:
                if (var4.equals("PM2.5")) {
                    var5 = 2;
                }
        }

        switch (var5) {
            case 0:
                this.SO2 = value;
                break;
            case 1:
                this.NOx = value;
                break;
            case 2:
                this.PM25 = value;
                break;
            case 3:
                this.PM25 = value;
                break;
            case 4:
                this.VOC = value;
                break;
            case 5:
                this.NH3 = value;
                break;
            case 6:
                this.PMcoarse = value;
                break;
            case 7:
                this.PM10more = value;
                break;
            case 8:
                this.BC = value;
                break;
            case 9:
                this.OC = value;
                break;
            case 10:
                this.CO2 = value;
                break;
            case 11:
                this.CO = value;
                break;
            default:
                throw new RuntimeException("unknown species:" + species);
        }

    }

    public double get(String species) {
        String var2 = species.toUpperCase();	//将所有的英文字符转换为大写字母
        byte var3 = -1;
        switch (var2.hashCode()) {
            case -1241257423:
                if (var2.equals("PM10MORE")) {
                    var3 = 6;
                }
                break;
            case -697658132:
                if (var2.equals("PMCOARSE")) {
                    var3 = 5;
                }
                break;
            case 2113:
                if (var2.equals("BC")) {
                    var3 = 7;
                }
                break;
            case 2156:
                if (var2.equals("CO")) {
                    var3 = 10;
                }
                break;
            case 2516:
                if (var2.equals("OC")) {
                    var3 = 8;
                }
                break;
            case 66886:
                if (var2.equals("CO2")) {
                    var3 = 9;
                }
                break;
            case 77241:
                if (var2.equals("NH3")) {
                    var3 = 4;
                }
                break;
            case 77495:
                if (var2.equals("NOX")) {
                    var3 = 1;
                }
                break;
            case 82262:
                if (var2.equals("SO2")) {
                    var3 = 0;
                }
                break;
            case 85162:
                if (var2.equals("VOC")) {
                    var3 = 3;
                }
                break;
            case 2458880:
                if (var2.equals("PM25")) {
                    var3 = 2;
                }
        }

        switch (var3) {
            case 0:
                return this.SO2;
            case 1:
                return this.NOx;
            case 2:
                return this.PM25;
            case 3:
                return this.VOC;
            case 4:
                return this.NH3;
            case 5:
                return this.PMcoarse;
            case 6:
                return this.PM10more;
            case 7:
                return this.BC;
            case 8:
                return this.OC;
            case 9:
                return this.CO2;
            case 10:
                return this.CO;
            default:
                throw new RuntimeException("unknown species:" + species);
        }
    }

    public void put(double[] arr) {
        this.SO2 = arr[0];
        this.NOx = arr[1];
        this.PM25 = arr[2];
        this.VOC = arr[3];
        this.NH3 = arr[4];
        this.PMcoarse = arr[5];
        this.PM10more = arr[6];
        this.BC = arr[7];
        this.OC = arr[8];
        this.CO2 = arr[9];
        this.CO = arr[10];
    }

    public void put(SpeciesVector sv) {
        this.SO2 = sv.SO2;
        this.NOx = sv.NOx;
        this.PM25 = sv.PM25;
        this.VOC = sv.VOC;
        this.NH3 = sv.NH3;
        this.PMcoarse = sv.PMcoarse;
        this.PM10more = sv.PM10more;
        this.BC = sv.BC;
        this.OC = sv.OC;
        this.CO2 = sv.CO2;
        this.CO = sv.CO;
    }

    public void fill(double[] arr) {
        arr[0] = this.SO2;
        arr[1] = this.NOx;
        arr[2] = this.PM25;
        arr[3] = this.VOC;
        arr[4] = this.NH3;
        arr[5] = this.PMcoarse;
        arr[6] = this.PM10more;
        arr[7] = this.BC;
        arr[8] = this.OC;
        arr[9] = this.CO2;
        arr[10] = this.CO;
    }

    public SpeciesVector subtract(SpeciesVector sv) {
        this.SO2 -= sv.SO2;
        this.NOx -= sv.NOx;
        this.PM25 -= sv.PM25;
        this.VOC -= sv.VOC;
        this.NH3 -= sv.NH3;
        this.PMcoarse -= sv.PMcoarse;
        this.PM10more -= sv.PM10more;
        this.BC -= sv.BC;
        this.OC -= sv.OC;
        this.CO2 -= sv.CO2;
        this.CO -= sv.CO;
        return this;
    }

    public SpeciesVector plus(SpeciesVector sv) {
        this.SO2 += sv.SO2;
        this.NOx += sv.NOx;
        this.PM25 += sv.PM25;
        this.VOC += sv.VOC;
        this.NH3 += sv.NH3;
        this.PMcoarse += sv.PMcoarse;
        this.PM10more += sv.PM10more;
        this.BC += sv.BC;
        this.OC += sv.OC;
        this.CO2 += sv.CO2;
        this.CO += sv.CO;
        return this;
    }

    public SpeciesVector division(SpeciesVector sv) {
        this.SO2 /= sv.SO2;
        this.NOx /= sv.NOx;
        this.PM25 /= sv.PM25;
        this.VOC /= sv.VOC;
        this.NH3 /= sv.NH3;
        this.PMcoarse /= sv.PMcoarse;
        this.PM10more /= sv.PM10more;
        this.BC /= sv.BC;
        this.OC /= sv.OC;
        this.CO2 /= sv.CO2;
        this.CO /= sv.CO;
        return this;
    }

    public SpeciesVector multiply(String sp, double v) {
        if (sp.equals("so2")) {
            this.SO2 *= v;
            return this;
        }

        if (sp.equals("so2")) {
            this.SO2 *= v;
            return this;
        }

        if (sp.equals("nox")) {
            this.NOx *= v;
            return this;
        }

        if (sp.equals("pm2.5")) {
            this.PM25 *= v;
            return this;
        }
        if (sp.equals("voc")) {
            this.VOC *= v;
            return this;
        }

        if (sp.equals("nh3")) {
            this.NH3 *= v;
            return this;
        }

        if (sp.equals("pmcorase")) {
            this.PMcoarse *= v;
            return this;
        }

        if (sp.equals("pm10more")) {
            this.PM10more *= v;
            return this;
        }

        if (sp.equals("bc")) {
            this.BC *= v;
            return this;
        }
        if (sp.equals("oc")) {
            this.OC *= v;
            return this;
        }

        if (sp.equals("co2")) {
            this.CO2 *= v;
            return this;
        }

        if (sp.equals("co")) {
            this.CO *= v;
            return this;
        }
        return this;
    }

    public SpeciesVector multiply(double v) {
        this.SO2 *= v;
        this.NOx *= v;
        this.PM25 *= v;
        this.VOC *= v;
        this.NH3 *= v;
        this.PMcoarse *= v;
        this.PM10more *= v;
        this.BC *= v;
        this.OC *= v;
        this.CO2 *= v;
        this.CO *= v;
        return this;
    }

    public SpeciesVector multiply(SpeciesVector sv) {
        this.SO2 *= sv.SO2;
        this.NOx *= sv.NOx;
        this.PM25 *= sv.PM25;
        this.VOC *= sv.VOC;
        this.NH3 *= sv.NH3;
        this.PMcoarse *= sv.PMcoarse;
        this.PM10more *= sv.PM10more;
        this.BC *= sv.BC;
        this.OC *= sv.OC;
        this.CO2 *= sv.CO2;
        this.CO *= sv.CO;
        return this;
    }

    public void overrideThis(SpeciesVector sv, double nodatavalue) {
        if (sv.SO2 != nodatavalue) {
            this.SO2 = sv.SO2;
        }

        if (sv.NOx != nodatavalue) {
            this.NOx = sv.NOx;
        }

        if (sv.PM25 != nodatavalue) {
            this.PM25 = sv.PM25;
        }

        if (sv.VOC != nodatavalue) {
            this.VOC = sv.VOC;
        }

        if (sv.NH3 != nodatavalue) {
            this.NH3 = sv.NH3;
        }

        if (sv.PMcoarse != nodatavalue) {
            this.PMcoarse = sv.PMcoarse;
        }

        if (sv.PM10more != nodatavalue) {
            this.PM10more = sv.PM10more;
        }

        if (sv.BC != nodatavalue) {
            this.BC = sv.BC;
        }

        if (sv.OC != nodatavalue) {
            this.OC = sv.OC;
        }

        if (sv.CO2 != nodatavalue) {
            this.CO2 = sv.CO2;
        }

        if (sv.CO != nodatavalue) {
            this.CO = sv.CO;
        }

    }

    public SpeciesVector override(SpeciesVector sv) {
        if (sv == null) {
            return this;
        } else {
            SpeciesVector v = new SpeciesVector(this);
            v.overrideThis(sv, -9999.0D);
            return v;
        }
    }

    public static SpeciesVector override(SpeciesVector sv1, SpeciesVector sv2) {
        return sv1 == null ? sv2 : sv1.override(sv2);
    }

    public void setDefaultValue(double nodatavalue, double v) {
        if (this.SO2 == nodatavalue) {
            this.SO2 = v;
        }

        if (this.NOx == nodatavalue) {
            this.NOx = v;
        }

        if (this.PM25 == nodatavalue) {
            this.PM25 = v;
        }

        if (this.VOC == nodatavalue) {
            this.VOC = v;
        }

        if (this.NH3 == nodatavalue) {
            this.NH3 = v;
        }

        if (this.PMcoarse == nodatavalue) {
            this.PMcoarse = v;
        }

        if (this.PM10more == nodatavalue) {
            this.PM10more = v;
        }

        if (this.BC == nodatavalue) {
            this.BC = v;
        }

        if (this.OC == nodatavalue) {
            this.OC = v;
        }

        if (this.CO2 == nodatavalue) {
            this.CO2 = v;
        }

        if (this.CO == nodatavalue) {
            this.CO = v;
        }

    }
}
