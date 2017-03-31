package ampc.com.gistone.util;

import com.fasterxml.jackson.databind.JsonNode;

import ampc.com.gistone.database.inter.TSiteMapper;
import ampc.com.gistone.database.model.TSite;
import ampc.com.gistone.util.RegionConfig;
import ampc.com.gistone.util.Jsons;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mengyue on 2016/11/3.
 */
@RestController
@RequestMapping
@Component
public class RequestRegionData {
	@Autowired
	private TSiteMapper tSiteMapper;

    @Autowired
    RegionConfig regionConfig = new RegionConfig();

    final static Logger logger = LoggerFactory.getLogger(RequestRegionData.class);

    //执行这个方法前 需要去配置UrlConfig.json文件的 URL
    public void request() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String regionData = "";
        ConfigFile configFile = Jsons.readObjFromJsonFile("src/main/resources/UrlConfig.json", ConfigFile.class);
        List<TreeMap<String,String>> urlList = configFile.url;
        for (Map<String,String> url : urlList) {
            HttpClient httpClient = new HttpClient();
            Set<Map.Entry<String, String>> set= url.entrySet();
            for (Map.Entry<String, String> cfg:set) {
                GetMethod getMethod = new GetMethod(cfg.getValue());
                int status = httpClient.executeMethod(getMethod);
                if (HttpStatus.SC_OK == status) {
                    InputStream inpuStream = getMethod.getResponseBodyAsStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inpuStream);
                    BufferedReader read = new BufferedReader(inputStreamReader);
                    StringBuffer stringBuffer = new StringBuffer();
                    String str = "";
                    while ((str = read.readLine()) != null) {
                        stringBuffer.append(str);
                    }
                    regionData = stringBuffer.toString();
                    JsonNode node = Jsons.mapper.readTree(regionData);
                    Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                    Map<String, JsonNode> map = null;
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> entry = iterator.next();
                        if ("data".equals(entry.getKey())) {
                            map = new HashMap<>();
                            JsonNode jsonNode = entry.getValue();
                            map.put(cfg.getKey(), jsonNode);

                        }
                    }
                    String json = Jsons.mapper.writeValueAsString(map);
                    RegionConfig regionConfig = Jsons.jsonToObj(json, RegionConfig.class);
                    String  value= cfg.getKey();
                    String pattern = "^([a-z]|[A-Z])";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(value);
                    String string= "";
                    while (m.find()) {
                        string= m.group();
                    }
                    String  first = string.toUpperCase();
                    String upperCase = String.valueOf(first).toUpperCase();
                    String  ok= value.replaceFirst(pattern,upperCase);
                    StringBuffer setM = new StringBuffer();
                    setM.append("set").append(ok);
                    Class<? extends RegionConfig> clazz1 = RegionConfig.class;
                    Method  method1 = clazz1.getMethod(String.valueOf(setM),List.class);
                    StringBuffer getM = new StringBuffer();
                    getM.append("get").append(ok);
                    Class<? extends RegionConfig> clazz2 = this.regionConfig.getClass();
                    Method  method2 = clazz2.getMethod(String.valueOf(getM),null);

                    List<HashMap<String, String>>  hashMapList= (List<HashMap<String, String>>) method2.invoke(regionConfig,null);
                    method1.invoke(this.regionConfig,hashMapList);
                    System.out.println(regionConfig);
                    List<HashMap<String, String>> list= this.regionConfig.getStation();
                    for(HashMap<String, String> theone:list){
                    TSite tsite=new TSite();
                    tsite.setCityName(theone.get("cityName"));
                    tsite.setLat(theone.get("lat"));
                    tsite.setLon(theone.get("lon"));
                    tsite.setSiteCode(theone.get("regionId"));
                    tsite.setStationId(theone.get("stationId"));
                    tsite.setStationName(theone.get("stationName"));
                    tSiteMapper.insertSelective(tsite);
                    }
                } else {
                    logger.debug("请求异常：status =" + Integer.valueOf(status));
                }
            }

        }
    }

    public static class ConfigFile {
        public List<TreeMap<String,String>> url;
    }

    //下面这个是在没网的时候，或者暂时无法连接到目的地时，用来代替远程的方法
    public void test() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {


        List<String> path = new ArrayList<>();
        List<String> json = new ArrayList<>();
        path.add("ncdata/tmp1.json");
        path.add("ncdata/tmp2.json");
        path.add("ncdata/tmp3.json");
        path.add("ncdata/tmp4.json");
        for (String str:path) {
            String string = Jsons.readFile(str);
            json.add(string);
        }
        int i = 1;
        for (String jsonData : json) {
            try {
                JsonNode node = Jsons.mapper.readTree(jsonData);
                Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                Map<String, JsonNode> map = null;

                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = iterator.next();

                    if ("data".equals(entry.getKey())) {
                        map = new HashMap<>();
                        JsonNode jsonNode = entry.getValue();
                        if (i==1) {
                            map.put("province", jsonNode);
                            String wantJson = Jsons.mapper.writeValueAsString(map);
                            RegionConfig regionConfig= Jsons.jsonToObj( wantJson,RegionConfig.class);

                            String  value= "province";
                            String pattern = "^([a-z]|[A-Z])";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(value);
                            String string= "";
                            while (m.find()) {
                                string= m.group();
                            }
                            String  first = string.toUpperCase();
                            String upperCase = String.valueOf(first).toUpperCase();
                            String  ok= value.replaceFirst(pattern,upperCase);
                            StringBuffer setM = new StringBuffer();
                            setM.append("set").append(ok);
                            Class<? extends RegionConfig> clazz1 = RegionConfig.class;
                            Method  method1 = clazz1.getMethod(String.valueOf(setM),List.class);
                            StringBuffer getM = new StringBuffer();
                            getM.append("get").append(ok);
                            Class<? extends RegionConfig> clazz2 = this.regionConfig.getClass();
                            Method  method2 = clazz2.getMethod(String.valueOf(getM),null);
                            //下面这里是空的 7号
                            List<HashMap<String, String>>  hashMapList= (List<HashMap<String, String>>) method2.invoke(regionConfig,null);
                            method1.invoke(this.regionConfig,hashMapList);
                            //this.regionConfig.setProvince(regionConfig.getProvince());
                        }
                        if (i==2) {
                            map.put("city", jsonNode);
                            String wantJson = Jsons.mapper.writeValueAsString(map);
                            RegionConfig regionConfig= Jsons.jsonToObj( wantJson,RegionConfig.class);

                            String  value= "city";
                            String pattern = "^([a-z]|[A-Z])";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(value);
                            String string= "";
                            while (m.find()) {
                                string= m.group();
                            }
                            String  first = string.toUpperCase();
                            String upperCase = String.valueOf(first).toUpperCase();
                            String  ok= value.replaceFirst(pattern,upperCase);
                            StringBuffer setM = new StringBuffer();
                            setM.append("set").append(ok);
                            Class<? extends RegionConfig> clazz1 = RegionConfig.class;
                            Method  method1 = clazz1.getMethod(String.valueOf(setM),List.class);
                            StringBuffer getM = new StringBuffer();
                            getM.append("get").append(ok);
                            Class<? extends RegionConfig> clazz2 = this.regionConfig.getClass();
                            Method  method2 = clazz2.getMethod(String.valueOf(getM),null);
                            //下面这里是空的 7号
                            List<HashMap<String, String>>  hashMapList= (List<HashMap<String, String>>) method2.invoke(regionConfig,null);
                            method1.invoke(this.regionConfig,hashMapList);
                            //this.regionConfig.setCity(r.city);
                        }
                        if (i==3) {
                            map.put("county", jsonNode);
                            String wantJson = Jsons.mapper.writeValueAsString(map);
                            RegionConfig regionConfig= Jsons.jsonToObj( wantJson,RegionConfig.class);

                            String  value= "county";
                            String pattern = "^([a-z]|[A-Z])";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(value);
                            String string= "";
                            while (m.find()) {
                                string= m.group();
                            }
                            String  first = string.toUpperCase();
                            String upperCase = String.valueOf(first).toUpperCase();
                            String  ok= value.replaceFirst(pattern,upperCase);
                            StringBuffer setM = new StringBuffer();
                            setM.append("set").append(ok);
                            Class<? extends RegionConfig> clazz1 = RegionConfig.class;
                            Method  method1 = clazz1.getMethod(String.valueOf(setM),List.class);
                            StringBuffer getM = new StringBuffer();
                            getM.append("get").append(ok);
                            Class<? extends RegionConfig> clazz2 = this.regionConfig.getClass();
                            Method  method2 = clazz2.getMethod(String.valueOf(getM),null);
                            //下面这里是空的 7号
                            List<HashMap<String, String>>  hashMapList= (List<HashMap<String, String>>) method2.invoke(regionConfig,null);
                            method1.invoke(this.regionConfig,hashMapList);
                        }
                        if (i==4) {
                            map.put("station", jsonNode);
                            String wantJson = Jsons.mapper.writeValueAsString(map);
                            RegionConfig regionConfig= Jsons.jsonToObj( wantJson,RegionConfig.class);

                            String  value= "station";
                            String pattern = "^([a-z]|[A-Z])";
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(value);
                            String string= "";
                            while (m.find()) {
                                string= m.group();
                            }
                            String  first = string.toUpperCase();
                            String upperCase = String.valueOf(first).toUpperCase();
                            String  ok= value.replaceFirst(pattern,upperCase);
                            StringBuffer setM = new StringBuffer();
                            setM.append("set").append(ok);
                            Class<? extends RegionConfig> clazz1 = RegionConfig.class;
                            Method  method1 = clazz1.getMethod(String.valueOf(setM),List.class);
                            StringBuffer getM = new StringBuffer();
                            getM.append("get").append(ok);
                            Class<? extends RegionConfig> clazz2 = this.regionConfig.getClass();
                            Method  method2 = clazz2.getMethod(String.valueOf(getM),null);
                            
                            List<HashMap<String, String>>  hashMapList= (List<HashMap<String, String>>) method2.invoke(regionConfig,null);
                            method1.invoke(this.regionConfig,hashMapList);
                        }
                        i++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}