package com.sogou.upd.passport.service.moduleblacklist.impl;

import com.sogou.upd.passport.dao.moduleblacklist.ModuleBlackListDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.moduleblacklist.ModuleBlacklist;
import com.sogou.upd.passport.service.moduleblacklist.ModuleBlackListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * nginx module 获取黑名单服务实现
 * User: chengang
 * Date: 14-8-27
 * Time: 下午6:45
 */
public class ModuleBlackListServiceImpl implements ModuleBlackListService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleBlackListServiceImpl.class);


    @Autowired
    private ModuleBlackListDAO moduleBlackListDAO;


    //添加module 黑名单，只对指定IP、user 白名单进行开放。

    @Override
    public List<ModuleBlacklist> getWholeModuleBlackList() throws ServiceException {
        return null;
    }

    @Override
    public List<ModuleBlacklist> getIncreModuleBlackList(Date update_timestamp) throws ServiceException {
        return null;
    }

    @Override
    public boolean checkUseridExist(String userid) throws ServiceException {


        return false;
    }

    @Override
    public boolean updateModuleBlackUserExpireTime(String userid, int expire_time) throws ServiceException {
        return false;
    }

    @Override
    public boolean deleteModuleBlackListByUserid(String usrid) throws ServiceException {
        return false;
    }

    @Override
    public ModuleBlacklist insertModuleBlackList(ModuleBlacklist moduleBlacklist) throws ServiceException {
        return null;
    }

    @Override
    public boolean batchInsertModuleBlackList(List<ModuleBlacklist> blackLists) throws ServiceException {
        return false;
    }
}
