package ampc.com.gistone.extract.netcdf;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;

import java.util.List;
import java.util.Map;

/**
 * Created by xll on 2017/2/23.
 */
public class VariablesBean {

  private String name;
  private DataType dataType;
  private Array data;
  private List<Dimension> dimensionList;
  private Map variablesAttribute;

  public VariablesBean(String name, DataType dataType, Array data, List<Dimension> dimensionList) {
    this.name = name;
    this.dataType = dataType;
    this.data = data;
    this.dimensionList = dimensionList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  public Array getData() {
    return data;
  }

  public void setData(Array data) {
    this.data = data;
  }

  public List<Dimension> getDimensionList() {
    return dimensionList;
  }

  public void setDimensionList(List<Dimension> dimensionList) {
    this.dimensionList = dimensionList;
  }

  public Map getVariablesAttribute() {
    return variablesAttribute;
  }

  public void setVariablesAttribute(Map variablesAttribute) {
    this.variablesAttribute = variablesAttribute;
  }
}
