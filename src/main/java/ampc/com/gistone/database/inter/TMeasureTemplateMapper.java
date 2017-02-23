package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMeasureTemplate;

public interface TMeasureTemplateMapper {
    int deleteByPrimaryKey(Long measureTemplateId);

    int insert(TMeasureTemplate record);

    int insertSelective(TMeasureTemplate record);

    TMeasureTemplate selectByPrimaryKey(Long measureTemplateId);

    int updateByPrimaryKeySelective(TMeasureTemplate record);

    int updateByPrimaryKey(TMeasureTemplate record);
}