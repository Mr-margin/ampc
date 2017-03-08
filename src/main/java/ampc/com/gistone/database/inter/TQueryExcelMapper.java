package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TQueryExcel;

public interface TQueryExcelMapper {
    int deleteByPrimaryKey(Long queryId);

    int insert(TQueryExcel record);

    int insertSelective(TQueryExcel record);

    TQueryExcel selectByPrimaryKey(Long queryId);

    int updateByPrimaryKeySelective(TQueryExcel record);

    int updateByPrimaryKey(TQueryExcel record);
}