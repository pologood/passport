package com.sogou.upd.passport.manager.api.connect.impl.qq;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.AbstractConnectProxyResultStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * qzone平台统一结果的实现类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class QzoneConnectProxyResultStrategy extends AbstractConnectProxyResultStrategy {

    @Override
    public Result buildCommonResultByPlatform(HashMap<String, Object> maps) {
        Result result = new APIResultSupport(false);
        if (maps.containsKey("ret") && !maps.get("ret").toString().equals(ErrorUtil.SUCCESS)) {
            result.setCode(maps.get("ret").toString());
            result.setMessage((String) maps.get("msg"));
        } else {
            //封装QQ返回请求正确的结果，返回结果中不包含ret或者包含ret且ret值为0的结果封装
            HashMap<String, Object> data;
            //QQ空间未读数结果封装
            String ret = maps.get("ret").toString();
            if (ret.equals(ErrorUtil.SUCCESS)) {
                result.setCode("0");
                result.setSuccess(true);
                result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.SUCCESS));
                data = convertToFormatMap(maps);
                result.setModels(data);
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashMap<String, Object> convertToFormatMap(HashMap<String, Object> formatMaps) {
        HashMap<String, Object> data;
        data = super.convertToFormatMap(formatMaps);
        data.remove("ret");
        data.remove("msg");
        return data;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


