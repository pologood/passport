package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.utils.iploc.Ip2location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-19 Time: 下午4:14 To change this template use
 * File | Settings | File Templates.
 */
public class IpLocationUtil {

    private static final Ip2location instance;

    private static final Map<String, String> city;

    static {
        instance = new Ip2location();
        city = Maps.newHashMap();
        try {
            InputStream inloc = IpLocationUtil.class.getResourceAsStream("/location.dat");
            instance.readData(inloc);
            InputStream incity = IpLocationUtil.class.getResourceAsStream("/cities.dat");
            BufferedReader is = new BufferedReader(new InputStreamReader(incity));
            String readValue = is.readLine();
            while (readValue != null) {
                String[] kv = readValue.split("\\|", 2);
                if (kv.length >= 2) {
                    city.put(kv[0], kv[1]);
                }
                readValue = is.readLine();
            }
        } catch (IOException e) {
            // 无默认数据
        }
    }

    public static String getCity(String ip) {
        // String cityName = "IP归属地未知";
        String cityName = "IP归属地未知";
        try {
            if (Strings.isNullOrEmpty(ip)) {
                return cityName;
            }

            if (ip.indexOf("10.") == 0 || ip.indexOf("192.168.") == 0) {
                return "局域网";
            }
            String location = instance.getLocation(ip);
            if (location != null && !location.equals("") && location.length() >= 6) {
                String key = location.substring(0, 6);
                if (city.containsKey(key)) {
                    // 如果里面存在这个城市.
                    cityName = city.get(key);
                }
            }
            return cityName;
        } catch (Exception e) {
            return cityName;
        }
    }

}
