package ampc.com.gistone.extract;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class ManageParams {

	private List<Variable> variableList1; // diff or ratio
	// scenario1/scenario2
	private List<Variable> variableList2;

	private NumberFormat nf;
	private List<PointBean> pointBeanList;
	private ExtractRequestParams params;
	private String filePath1;
	private String filePath2;
	private NetcdfFile ncFile1;
	private Map attributes;
	private Map<String, List<Variable>> variableMap1; // diff or ratio
	// scenario1/scenario2
	private Map<String, List<Variable>> variableMap2;
	private Projection projection;
	private Interpolation interpolation;
	private List<Map<String, Object>> res;

	private List<float[][]> floatList1;
	private List<float[][]> floatList2;

	private PointBean[][] pointBeanArray;

	public ManageParams() {
		variableList1 = new ArrayList<Variable>();
		variableList2 = new ArrayList<Variable>();
		nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(10);
		variableMap1 = new HashMap<String, List<Variable>>();
		variableMap2 = new HashMap<String, List<Variable>>();
		floatList1 = new ArrayList<float[][]>();
		floatList2 = new ArrayList<float[][]>();
	}

	public List<Variable> getVariableList1() {
		return variableList1;
	}

	public void setVariableList1(List<Variable> variableList1) {
		this.variableList1 = variableList1;
	}

	public List<Variable> getVariableList2() {
		return variableList2;
	}

	public void setVariableList2(List<Variable> variableList2) {
		this.variableList2 = variableList2;
	}

	public NumberFormat getNf() {
		return nf;
	}

	public void setNf(NumberFormat nf) {
		this.nf = nf;
	}

	public List<PointBean> getPointBeanList() {
		return pointBeanList;
	}

	public void setPointBeanList(List<PointBean> pointBeanList) {
		this.pointBeanList = pointBeanList;
	}

	public ExtractRequestParams getParams() {
		return params;
	}

	public void setParams(ExtractRequestParams params) {
		this.params = params;
	}

	public String getFilePath1() {
		return filePath1;
	}

	public void setFilePath1(String filePath1) {
		this.filePath1 = filePath1;
	}

	public String getFilePath2() {
		return filePath2;
	}

	public void setFilePath2(String filePath2) {
		this.filePath2 = filePath2;
	}

	public NetcdfFile getNcFile1() {
		return ncFile1;
	}

	public void setNcFile1(NetcdfFile ncFile1) {
		this.ncFile1 = ncFile1;
	}

	public Map getAttributes() {
		return attributes;
	}

	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	public Map<String, List<Variable>> getVariableMap1() {
		return variableMap1;
	}

	public void setVariableMap1(Map<String, List<Variable>> variableMap1) {
		this.variableMap1 = variableMap1;
	}

	public Map<String, List<Variable>> getVariableMap2() {
		return variableMap2;
	}

	public void setVariableMap2(Map<String, List<Variable>> variableMap2) {
		this.variableMap2 = variableMap2;
	}

	public Projection getProjection() {
		return projection;
	}

	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public List<Map<String, Object>> getRes() {
		return res;
	}

	public void setRes(List<Map<String, Object>> res) {
		this.res = res;
	}

	public List<float[][]> getFloatList1() {
		return floatList1;
	}

	public void setFloatList1(List<float[][]> floatList1) {
		this.floatList1 = floatList1;
	}

	public List<float[][]> getFloatList2() {
		return floatList2;
	}

	public void setFloatList2(List<float[][]> floatList2) {
		this.floatList2 = floatList2;
	}

	public PointBean[][] getPointBeanArray() {
		return pointBeanArray;
	}

	public void setPointBeanArray(PointBean[][] pointBeanArray) {
		this.pointBeanArray = pointBeanArray;
	}

}
