package ampc.com.gistone.extract;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ampc.com.gistone.extract.netcdf.Netcdf;
import ampc.com.gistone.util.DateUtil;

@Service
public abstract class ExtractService {

	private final static Logger logger = LoggerFactory.getLogger(ExtractService.class);
	public final static String FLAG = "flag";
	@Autowired
	public ResultPathUtil resultPathUtil;
	public ExtractConfig extractConfig;
	// public ExtractRequestParams params;
	// public String filePath1;
	// public String filePath2;
	// public NetcdfFile ncFile1;
	// public Map attributes;
	// public Map<String, List<Variable>> variableMap1; // diff or ratio
	// // scenario1/scenario2
	// public Map<String, List<Variable>> variableMap2;
	// public Projection projection;
	// public Interpolation interpolation;

	// public boolean getNcFilePath() {
	// try {
	// String showType = params.getShowType();
	// if (Constants.SHOW_TYPE_CONCN.equals(showType)) {
	// filePath1 = extractConfig.getConcnFilePath() + "/"
	// + (Constants.TIMEPOINT_H.equals(params.getTimePoint()) ?
	// extractConfig.getConcnHourlyPrefix()
	// : extractConfig.getConcnDailyPrefix());
	// } else if (Constants.SHOW_TYPE_EMIS.equals(showType)) {
	// filePath1 = extractConfig.getEmisFilePath() + "/" +
	// (Constants.TIMEPOINT_H.equals(params.getTimePoint())
	// ? extractConfig.getEmisHourlyPrefix() :
	// extractConfig.getEmisDailyPrefix());
	// } else if (Constants.SHOW_TYPE_WIND.equals(showType)) {
	// filePath1 = extractConfig.getWindFilePath() + "/" +
	// (Constants.TIMEPOINT_H.equals(params.getTimePoint())
	// ? extractConfig.getMeteorHourlyWindPrefix() :
	// extractConfig.getMeteorDailyWindPrefix());
	// } else {
	// return false;
	// }
	// filePath2 = filePath1;
	//
	// filePath1 = resultPathUtil.getRealPath(filePath1, params.getUserId(),
	// params.getDomainId(),
	// params.getMissionId(), params.getScenarioId1(), params.getDomain());
	//
	// if (!Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
	// filePath2 = resultPathUtil.getRealPath(filePath2, params.getUserId(),
	// params.getDomainId(),
	// params.getMissionId(), params.getScenarioId2(), params.getDomain());
	// }
	// return true;
	// } catch (Exception e) {
	// logger.error("ExtractService | getNcFilePath error");
	// return false;
	// }
	//
	// }

	public void getVariables(ManageParams manageParams) {
		long startTimes = System.currentTimeMillis();
		ExtractRequestParams params = manageParams.getParams();
		String timePoint = params.getTimePoint();

		if (Constants.TIMEPOINT_A.equals(timePoint)) {
			List<String> list = params.getDates();
			for (int i = 0; i < list.size(); i++) {
				String date = list.get(i);
				buildVariables(manageParams, date);
			}
			try {
				manageParams.setAttributes(Netcdf.getAttributes(manageParams.getFilePath1()));
			} catch (IOException e) {
				logger.error("ExtractDataService | getVariables() : getAttributes IOException");
				return;
			}
		} else if (Constants.TIMEPOINT_D.equals(timePoint) || Constants.TIMEPOINT_H.equals(timePoint)) {
			buildVariables(manageParams, params.getDay());

			try {
				manageParams.setAttributes(Netcdf.getAttributes(manageParams.getFilePath1()));
			} catch (IOException e) {
				logger.error("ExtractDataService | getVariables() : getAttributes IOException");
				return;
			}
		}
		logger.info("getVariables times : " + (System.currentTimeMillis() - startTimes) + "ms");
	}

	public abstract void buildVariables(ManageParams manageParams, String day);

	public Projection buildProject(ManageParams manageParams) throws TransformException, FactoryException, IOException {

		long readAttrTimes = System.currentTimeMillis();
		ExtractRequestParams params = manageParams.getParams();
		Map attributes = manageParams.getAttributes();
		double xorig = Double.valueOf((String.valueOf(attributes.get("XORIG"))));
		double yorig = Double.valueOf((String.valueOf(attributes.get("YORIG"))));
		double xcell = Double.valueOf((String.valueOf(attributes.get("XCELL"))));
		double ycell = Double.valueOf((String.valueOf(attributes.get("YCELL"))));
		String lat_1 = attributes.get("P_ALP").toString();
		String lat_2 = attributes.get("P_BET").toString();
		String lat_0 = attributes.get("YCENT").toString();
		String lon_0 = attributes.get("XCENT").toString();
		int totalLayer = manageParams.getNcFile1().findDimension("LAY").getLength();
		int row = Integer.valueOf(String.valueOf(attributes.get("NROWS")));
		int col = Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
		params.setXorig(xorig);
		params.setYorig(yorig);
		params.setXcell(xcell);
		params.setYcell(ycell);
		params.setLat_1(lat_1);
		params.setLat_2(lat_2);
		params.setLat_0(lat_0);
		params.setLon_0(lon_0);

		int layer = params.getLayer();
		if (layer < 1 || layer > totalLayer) {
			logger.error("the layer params should be between in 1 and " + totalLayer);
		}
		logger.info("(xorig,yorig) = (" + xorig + "," + yorig + "), (xcell,ycell) = (" + xcell + "," + ycell
				+ "), (nrows, ncols) = (" + row + "," + col + ")");

		manageParams.setInterpolation(new Interpolation(params.getHour(), layer, xorig, yorig, xcell, ycell, row, col));
		logger.info("buildProject times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
		return ProjectUtil.getProj(attributes);

	}

	// need reversalArray from level and vertical direction
	public float[][] reversalArray(float[][] v) {
		int dx = v.length;
		int dy = v[0].length;
		for (int i = 0; i < dx / 2; i++) {
			for (int j = 0; j < dy; j++) {
				float tmp = v[i][j];
				v[i][j] = v[dx - i - 1][j];
				v[dx - i - 1][j] = tmp;
			}
		}

		for (int i = 0; i < dx / 2; i++) {
			for (int j = 0; j < dy; j++) {
				float tmp = v[i][j];
				v[i][j] = v[i][dy - j - 1];
				v[i][dy - j - 1] = tmp;
			}
		}
		return v;
	}

	public String[] buildIamgeFilePath(ExtractRequestParams params, String fileType) {
		String[] path = new String[2];
		Date date = new Date();
		String rootPath = System.getProperty("user.dir");
		String imageFilePath = "";
		String imageFileName = "";
		if (Constants.FILE_TYPE_IMAGE.equals(fileType)) {
			imageFilePath = extractConfig.getImageFilePath(); // /$userid/$domainid/$missionid/$domain/$showType/$calcType/$currDate/
			imageFileName = extractConfig.getImageFileName(); // $timePoint-$day-$hour-$layer-$specie-$scenario-$random.png
		} else {
			imageFilePath = extractConfig.getTiffFilePath(); // /$userid/$domainid/$missionid/$domain/$showType/$calcType/$currDate/
			imageFileName = extractConfig.getTiffFileName(); // $timePoint-$day-$hour-$layer-$specie-$scenario-$random.png
		}
		imageFilePath = imageFilePath.replace("$userid", String.valueOf(params.getUserId()))
				.replace("$domainid", String.valueOf(params.getDomainId()))
				.replace("$missionid", String.valueOf(params.getMissionId()))
				.replace("$domain", String.valueOf(params.getDomain())).replace("$showType", params.getShowType())
				.replace("$calcType", params.getCalcType())
				.replace("$currDate", DateUtil.DATEtoString(date, DateUtil.DATE_FORMAT));
		String day = "";
		String scenario = "";
		if (Constants.TIMEPOINT_A.equals(params.getTimePoint())) {
			List<String> list = params.getDates();
			day = list.get(0) + list.get(list.size() - 1);
		} else {
			day = params.getDay();
		}
		if (Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
			scenario = String.valueOf(params.getScenarioId1());
		} else {
			scenario = String.valueOf(params.getScenarioId1().toString()) + "-"
					+ String.valueOf(params.getScenarioId2());
		}

		imageFileName = imageFileName.replace("$timePoint", params.getTimePoint()).replace("$day", day)
				.replace("$hour", String.valueOf(params.getHour()))
				.replace("$layer", String.valueOf(params.getLayer() - 1)).replace("$specie", params.getSpecie())
				.replace("$scenario", scenario).replace("$random", String.valueOf(new Date().getTime()));
		String contentPath = rootPath + "/" + imageFilePath + "/";
		File file = new File(contentPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		path[0] = contentPath + imageFileName; // 绝对路径，全路径
		path[1] = imageFilePath + imageFileName; // 需要返给前端的路径
		logger.info("file path : " + path[0]);
		return path;

	}

}
