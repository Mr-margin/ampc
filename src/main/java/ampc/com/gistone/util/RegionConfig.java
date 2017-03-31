package ampc.com.gistone.util;

import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dell on 2015-12-16.
 */

@Configuration
public class RegionConfig {

  private List<HashMap<String, String>> province;
  private List<HashMap<String, String>> city;
  private List<HashMap<String, String>> station;
  private List<HashMap<String, String>> county;

  public RegionConfig() {

  }


  public List<HashMap<String, String>> getProvince() {
    return province;
  }

  public void setProvince(List<HashMap<String, String>> province) {
    this.province = province;
  }

  public List<HashMap<String, String>> getCity() {
    return city;
  }

  public void setCity(List<HashMap<String, String>> city) {
    this.city = city;
  }

  public List<HashMap<String, String>> getStation() {
    return station;
  }

  public void setStation(List<HashMap<String, String>> station) {
    this.station = station;
  }

  public List<HashMap<String, String>> getCounty() {
    return county;
  }

  public void setCounty(List<HashMap<String, String>> county) {
    this.county = county;
  }
}
