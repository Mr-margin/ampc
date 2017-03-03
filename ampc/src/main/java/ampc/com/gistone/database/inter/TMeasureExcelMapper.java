package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMeasureExcel;

public interface TMeasureExcelMapper {
    int deleteByPrimaryKey(Long measureExcelId);

    int insert(TMeasureExcel record);

    int insertSelective(TMeasureExcel record);

    TMeasureExcel selectByPrimaryKey(Long measureExcelId);

    int updateByPrimaryKeySelective(TMeasureExcel record);

    int updateByPrimaryKey(TMeasureExcel record);
}