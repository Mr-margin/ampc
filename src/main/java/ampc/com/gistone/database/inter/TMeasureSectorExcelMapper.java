package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMeasureSectorExcel;

public interface TMeasureSectorExcelMapper {
    int deleteByPrimaryKey(Long msExcelId);

    int insert(TMeasureSectorExcel record);

    int insertSelective(TMeasureSectorExcel record);

    TMeasureSectorExcel selectByPrimaryKey(Long msExcelId);

    int updateByPrimaryKeySelective(TMeasureSectorExcel record);

    int updateByPrimaryKey(TMeasureSectorExcel record);
}