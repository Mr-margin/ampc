package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSectorTemplate;

public interface TSectorTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TSectorTemplate record);

    int insertSelective(TSectorTemplate record);

    TSectorTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TSectorTemplate record);

    int updateByPrimaryKey(TSectorTemplate record);
}