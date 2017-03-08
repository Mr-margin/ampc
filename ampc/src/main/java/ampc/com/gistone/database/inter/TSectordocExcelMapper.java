package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSectordocExcel;

public interface TSectordocExcelMapper {
    int deleteByPrimaryKey(Long sectordocId);

    int insert(TSectordocExcel record);

    int insertSelective(TSectordocExcel record);

    TSectordocExcel selectByPrimaryKey(Long sectordocId);

    int updateByPrimaryKeySelective(TSectordocExcel record);

    int updateByPrimaryKey(TSectordocExcel record);
}