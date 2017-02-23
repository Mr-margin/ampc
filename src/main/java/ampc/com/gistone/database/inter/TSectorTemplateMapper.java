package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSectorTemplate;

public interface TSectorTemplateMapper {
    int deleteByPrimaryKey(Long sectorTemplateId);

    int insert(TSectorTemplate record);

    int insertSelective(TSectorTemplate record);

    TSectorTemplate selectByPrimaryKey(Long sectorTemplateId);

    int updateByPrimaryKeySelective(TSectorTemplate record);

    int updateByPrimaryKey(TSectorTemplate record);
}