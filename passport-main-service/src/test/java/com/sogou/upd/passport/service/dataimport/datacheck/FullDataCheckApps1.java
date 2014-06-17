package com.sogou.upd.passport.service.dataimport.datacheck;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.dao.account.AccountDAO;
import com.sogou.upd.passport.dao.account.AccountInfoDAO;
import com.sogou.upd.passport.dao.account.MobilePassportMappingDAO;
import org.apache.commons.collections.CollectionUtils;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-20
 * Time: 下午7:48
 */
public class FullDataCheckApps1 extends RecursiveTask<List<Map<String, String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullDataCheckApps.class);

    private static final long serialVersionUID = 5274042337565024144L;

    private AccountDAO accountDAO;

    private AccountInfoDAO accountInfoDAO;

    private MobilePassportMappingDAO mobilePassportMappingDAO;

    private static final String BASE_FILE_PATH = "D:\\项目\\非第三方账号迁移\\check_full_data\\05_test\\";


    //返回结果
//    private final List<RecursiveTask<List<String>>> forks = Lists.newLinkedList();

    private final List<RecursiveTask<Map<String, String>>> forks = Lists.newLinkedList();


    public FullDataCheckApps1(AccountDAO accountDAO, AccountInfoDAO accountInfoDAO, MobilePassportMappingDAO mobilePassportMappingDAO) {
        this.accountDAO = accountDAO;
        this.accountInfoDAO = accountInfoDAO;
        this.mobilePassportMappingDAO = mobilePassportMappingDAO;

    }


    @Override
    protected List<Map<String, String>> compute() {
        LOGGER.info("FullDataCheckApps check full data start......");
//        List<Integer> differenceLists = Lists.newLinkedList();

        List<Map<String, String>> differences = new ArrayList<>();

        StopWatch watch = new StopWatch();
        watch.start();
        try {
            for (int i = 1; i < 5; i++) {
                String filePath = BASE_FILE_PATH + "05_test_phone_userid_0" + i + ".txt";
                FullDataCheckApp1 task = new FullDataCheckApp1(accountDAO, accountInfoDAO, mobilePassportMappingDAO, filePath);
                task.fork();
                forks.add(task);
            }

            //结果整合
            for (RecursiveTask<Map<String, String>> task : forks) {
                try {
                    LOGGER.info(String.format("FullDataCheckApps check full data 05 test phone task:[%s] failList", task.getClass().getName()));
                    differences.add(task.get());

                  /*  if (CollectionUtils.isNotEmpty((Collection) task.get())) {
                        differenceLists.add(task.get().size());
                    }*/
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("FullDataCheckApps check full data 05 test phone fail.", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("FullDataCheckApps check full data 05 test phone error.", e);
        }
        LOGGER.info("FullDataCheckApps finish use time 05 test phone {}", watch.stop());
        return differences;
    }
}