package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TUserMsg;

public interface TUserMsgMapper {
    int deleteByPrimaryKey(Long userMsgId);

    int insert(TUserMsg record);

    int insertSelective(TUserMsg record);

    TUserMsg selectByPrimaryKey(Long userMsgId);

    int updateByPrimaryKeySelective(TUserMsg record);

    int updateByPrimaryKey(TUserMsg record);
}