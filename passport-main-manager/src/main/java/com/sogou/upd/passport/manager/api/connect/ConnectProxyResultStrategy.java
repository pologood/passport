package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 根据平台封装返回该平台对应结果的抽象策略接口，该接口封装所有具体策略所需的接口方法
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
@Component
public interface ConnectProxyResultStrategy {

    public Result buildCommonResultByPlatform(HashMap<String, Object> paramMaps);

    public HashMap<String, Object> convertToFormatMap(HashMap<String, Object> formatMaps);

}
