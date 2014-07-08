package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.UserExtInfoTmp;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Account表的DAO操作
 * User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface UserExtInfoTmpDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " user_ext_info_tmp ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " userid, question, answer, username, birthday, gender, createtime, createip ";

    /**
     * 值列表
     */
    String
            VALUE_FIELD =
            " :userid, :userExtInfoTmp.question, :userExtInfoTmp.answer, :userExtInfoTmp.username, " +
                    ":userExtInfoTmp.birthday, :userExtInfoTmp.gender, :userExtInfoTmp.createtime, " +
                    ":userExtInfoTmp.createip ";

    /**
     * 根据passportId获取UserExtInfo表数据
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where userid=:userid")
    public UserExtInfoTmp getUserExtInfoTmpByUserid(@SQLParam("userid") String userid) throws DataAccessException;

    /**
     * 根据passportId获取UserExtInfo表数据
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where userid=:userid")
    public List<UserExtInfoTmp> listUserExtInfoTmpByUserid(@SQLParam("userid") String userid) throws DataAccessException;

    /**
     * 更新所有不为空的字段
     */
    @SQL(
            "update " + TABLE_NAME + " set "
                    + "#if(:userExtInfoTmp.question != null){question=:userExtInfoTmp.question,} "
                    + "#if(:userExtInfoTmp.answer != null){answer=:userExtInfoTmp.answer,} "
                    + "#if(:userExtInfoTmp.username != null){username=:userExtInfoTmp.username,} "
                    + "#if(:userExtInfoTmp.birthday != null){birthday=:userExtInfoTmp.birthday,} "
                    + "#if(:userExtInfoTmp.gender != null){gender=:userExtInfoTmp.gender,} "
                    + "#if(:userExtInfoTmp.createtime != null){createtime=:userExtInfoTmp.createtime,} "
                    + "#if(:userExtInfoTmp.createip != null){createip=:userExtInfoTmp.createip} "
                    + " where userid = :userid")
    public int updateUserExtInfoTmp(@SQLParam("userid") String userid,
                                    @SQLParam("userExtInfoTmp") UserExtInfoTmp userExtInfoTmp) throws DataAccessException;

    /**
     * 根据passportId删除UserExtInfo表数据
     */
    @SQL("delete from" + TABLE_NAME + " where userid=:userid limit :count")
    public int deleteMulUserExtInfoTmpByUserid(@SQLParam("userid") String userid, @SQLParam("count") int count) throws DataAccessException;

}
