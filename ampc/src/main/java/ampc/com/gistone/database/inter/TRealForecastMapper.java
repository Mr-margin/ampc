package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TRealForecast;

public interface TRealForecastMapper {
    int deleteByPrimaryKey(Long realForecastId);

    int insert(TRealForecast record);

    int insertSelective(TRealForecast record);

    TRealForecast selectByPrimaryKey(Long realForecastId);

    int updateByPrimaryKeySelective(TRealForecast record);

    int updateByPrimaryKey(TRealForecast record);
}