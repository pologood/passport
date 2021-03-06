package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
/**
 * 手机号工具类 User: mayan Date: 13-3-27 Time: 上午11:19 To change this template use File | Settings | File Templates.
 */
public class PhoneUtil {

	public static final String PHONE_FORMAT = "(^[0-9]{3,4}-[0-9]{3,8}$)|^(13[0-9]|14[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$";
	public static final int PHONE_LENTH = 11;
	/**
	 * 验证手机号码、电话号码是否有效
	 * 新联通 （中国联通+中国网通）手机号码开头数字 130、131、132、145、155、156、185、186、176（4G） 　　
	 * 新移动 （中国移动+中国铁通）手机号码开头数字 134、135、136、137、138、139、147、150、151、152、157、158、159、182、183、187、188、178（4G）
	 * 新电信 （中国电信 <http://baike.baidu.com/view/3214.htm>+中国卫通）手机号码开头数字 133、153、180、181、189、177（4G）
     * 虚拟运营商  170
	 */
	public static boolean verifyPhoneNumberFormat(String phone) {
        return !Strings.isNullOrEmpty(phone) && phone.matches(PHONE_FORMAT);
    }

	public static void main(String[] args) {
		String[] phones = new String[] { "1523620111", "11011363254", "15811363254", "15811364216", "15811364216",
				"13011111111", "15811364216", "022-6232903-22", "022-6232903", "+8615811364216", "8615811224181" ,"17043123221", "007-007972","14105487898","17548791237","15357448798"};
		for (String phone : phones) {
			System.out.print(phone + "  ");
			System.out.println(PhoneUtil.verifyPhoneNumberFormat(phone));
		}

		String phone = "8615811224181";
		if (phone.length() > PhoneUtil.PHONE_LENTH) {
            phone = phone.substring(phone.length() - PhoneUtil.PHONE_LENTH);
            System.out.println(phone);
		}

    }
}
