/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sogou.upd.passport.oauth2.common.parameters;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthMessage;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import java.util.Map;

/**
 * GET请求 构造URL的参数
 * 不带#access_Token锚点
 * @author shipengzhi
 *
 */
public class QueryParameterApplier implements OAuthParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {

        String messageUrl = message.getLocationUri();
        if (messageUrl != null) {
            boolean isContainsQuery = messageUrl.contains("?");
            StringBuffer url = new StringBuffer(messageUrl);

            StringBuffer query = new StringBuffer(OAuthUtils.format(params.entrySet(),
                    CommonConstant.DEFAULT_CONTENT_CHARSET));

            if (!Strings.isNullOrEmpty(query.toString())) {
                if (isContainsQuery) {
                    url.append("&").append(query);
                } else {
                    url.append("?").append(query);
                }
            }

            message.setLocationUri(url.toString());
        }
        return message;
    }
}
