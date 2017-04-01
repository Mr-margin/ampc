package ampc.com.gistone.extract.netcdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Netcdf {

  static final Logger logger = LoggerFactory.getLogger(Netcdf.class);

  public static Array[] loadArray(String fn, String... names) throws IOException {
    long currTime = System.currentTimeMillis();
    NetcdfFile nc = NetcdfFile.open(fn);
    System.out.println(System.currentTimeMillis() - currTime);
    int num = names.length;
    Array[] arrs = new Array[num];

    Map<String, Variable> vars = nc.getVariables().stream().collect(
      Collectors.toMap(Variable::getShortName,
        Function.identity()));

    for (int ii = 0; ii < num; ++ii) {
      if (!vars.containsKey(names[ii])) {
        continue;
      }

      Variable var = vars.get(names[ii]);
      arrs[ii] = var.read();
    }

    nc.close();
    return arrs;
  }

  public static Map getAttributes(String fn) throws IOException {
    NetcdfFile nc = NetcdfFile.open(fn);
    Map m = new HashMap();

    for (Attribute attribute : nc.getGlobalAttributes()) {
      if (attribute.getDataType().equals(DataType.DOUBLE))
        m.put(attribute.getShortName(), attribute.getNumericValue());
      if (attribute.getDataType().equals(DataType.STRING))
        m.put(attribute.getShortName(), attribute.getStringValue());
      if (attribute.getDataType().equals(DataType.FLOAT))
        m.put(attribute.getShortName(), attribute.getNumericValue());
      if (attribute.getDataType().equals(DataType.INT))
        m.put(attribute.getShortName(), attribute.getNumericValue());
    }


    nc.close();
    return m;
  }


  public static void createNcFile(String pathOut, List<VariablesBean> variablesBeanList, Map globalAttribute) throws Exception {
    logger.info("start createNcFile " + pathOut);
    try {
      File f = new File(pathOut);
      if (!f.getParentFile().exists())
        f.getParentFile().mkdirs();

      NetcdfFileWriter nc = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, pathOut);
      nc.create();

      nc.setRedefineMode(true);

      if (null != globalAttribute) {
        Iterator iter = globalAttribute.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry) iter.next();
          Attribute attribute = new Attribute(entry.getKey().toString(), entry.getValue().toString());
          nc.addGroupAttribute(null, attribute);
        }
      }

      for (VariablesBean vb : variablesBeanList) {
        logger.info("Dump " + vb.getName());
        List<Dimension> dims = vb.getDimensionList();
        nc.setRedefineMode(true);
        buildDimensions(nc, dims);
        Variable v = nc.addVariable(null, vb.getName(), vb.getDataType(), buildDims(dims));
        nc.setRedefineMode(false);
        nc.write(v, vb.getData());
      }
      nc.close();
      logger.info("finish build " + pathOut);
    } catch (Exception e) {
      logger.error("createNcFile error", e);
    }
  }

  public static void buildDimensions(NetcdfFileWriter nc, List<Dimension> dims) throws Exception {
    try {
      for (Dimension dim : dims) {
        if(!nc.hasDimension(null, dim.getFullName())) {
          nc.addDimension(null, dim.getFullName(), dim.getLength());
        }
      }
    } catch (Exception e) {
      logger.error("buildDimensions error", e);
    }
  }

  public static String buildDims(List<Dimension> dims) {
    String args = "";
    for (Dimension dim : dims) {
      args += dim.getFullName() + " ";
    }
    return args;
  }



}