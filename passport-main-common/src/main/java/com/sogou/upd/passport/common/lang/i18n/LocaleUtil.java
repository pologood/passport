package com.sogou.upd.passport.common.lang.i18n;

/**
 * 用来处理地域和字符编码的工具类。
 * User: shipengzhi
 * Date: 13-5-23
 * Time: 下午11:46
 * To change this template use File | Settings | File Templates.
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sogou.upd.passport.common.lang.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 用来处理地域和字符编码的工具类。
 *
 * @author Michael Zhou
 * @version $Id: LocaleUtil.java 1149 2004-08-10 02:01:41Z baobao $
 */
public class LocaleUtil {
    /* ============================================================================ */
    /*  常量和singleton。                                                           */
    /* ============================================================================ */
    private static final Set AVAILABLE_LANGUAGES = Sets.newHashSet();
    private static final Set AVAILABLE_COUNTRIES = Sets.newHashSet();
    private static final LocaleInfo systemLocaleInfo;
    private static LocaleInfo defaultLocalInfo;
    private static final ThreadLocal contextLocaleInfoHolder = new ThreadLocal();

    static {
        // 取得所有可用的国家和语言。
        Locale[] availableLocales = Locale.getAvailableLocales();

        for (int i = 0; i < availableLocales.length; i++) {
            Locale locale = availableLocales[i];

            AVAILABLE_LANGUAGES.add(locale.getLanguage());
            AVAILABLE_COUNTRIES.add(locale.getCountry());
        }

        // 取得系统locale和charset。
        systemLocaleInfo = new LocaleInfo();

        // 设置默认locale和charset。
        defaultLocalInfo = systemLocaleInfo;
    }

    /* ============================================================================ */
    /*  解析locale和localeInfo。                                                    */
    /* ============================================================================ */

    /**
     * 解析locale字符串。
     * <p/>
     * <p>
     * Locale字符串是符合下列格式：<code>language_country_variant</code>。
     * </p>
     *
     * @param localeName 要解析的字符串
     * @return <code>Locale</code>对象，如果locale字符串为空，则返回<code>null</code>
     */
    public static Locale parseLocale(String localeName) {
        if (localeName != null) {
            String[] localeParts = StringUtils.split(localeName, "_");
            int len = localeParts.length;

            if (len > 0) {
                String language = localeParts[0];
                String country = "";
                String variant = "";

                if (len > 1) {
                    country = localeParts[1];
                }

                if (len > 2) {
                    variant = localeParts[2];
                }

                return new Locale(language, country, variant);
            }
        }

        return null;
    }

    /**
     * 取得正规的字符集名称, 如果指定字符集不存在, 则抛出<code>UnsupportedEncodingException</code>.
     *
     * @param charset 字符集名称
     * @return 正规的字符集名称
     */
    public static String getCanonicalCharset(String charset) {
        return Charset.forName(charset).name();
    }

    /**
     * 取得正规的字符集名称, 如果指定字符集不存在, 则返回<code>null</code>.
     *
     * @param charset 字符集名称
     * @return 正规的字符集名称, 如果指定字符集不存在, 则返回<code>null</code>
     */
    public static String getCanonicalCharset(String charset, String defaultCharset) {
        String result = null;

        try {
            result = getCanonicalCharset(charset);
        } catch (IllegalArgumentException e) {
            if (defaultCharset != null) {
                try {
                    result = getCanonicalCharset(defaultCharset);
                } catch (IllegalArgumentException ee) {
                }
            }
        }

        return result;
    }

    /**
     * 取得备选的resource bundle风格的名称列表。
     * 例如：<code>calculateBundleNames("hello.jsp", new Locale("zh", "CN", "variant"))</code>将返回下面列表：
     * hello.jsp
     * hello_zh.jsp
     * hello_zh_CN.jsp
     * hello_zh_CN_variant.jsp
     *
     * @param baseName bundle的基本名
     * @param locale   区域设置
     * @return 所有备选的bundle名
     */
    public static List calculateBundleNames(String baseName, Locale locale) {
        return calculateBundleNames(baseName, locale, false);
    }

    /**
     * 取得备选的resource bundle风格的名称列表。
     * 例如：<code>calculateBundleNames("hello.jsp", new Locale("zh", "CN", "variant"),
     * false)</code>将返回下面列表：
     * hello.jsp
     * hello_zh.jsp
     * hello_zh_CN.jsp
     * hello_zh_CN_variant.jsp
     * 当<code>noext</code>为<code>true</code>时，不计算后缀名，例如<code>calculateBundleNames("hello.world",
     * new Locale("zh", "CN", "variant"), true)</code>将返回下面列表：
     * hello.world
     * hello.world_zh
     * hello.world_zh_CN
     * hello.world_zh_CN_variant
     *
     * @param baseName bundle的基本名
     * @param locale   区域设置
     * @return 所有备选的bundle名
     */
    public static List calculateBundleNames(String baseName, Locale locale, boolean noext) {
        baseName = StringUtil.defaultIfEmpty(baseName, StringUtil.EMPTY_STRING);

        if (locale == null) {
            locale = new Locale("");
        }

        // 取后缀。
        String ext = StringUtil.EMPTY_STRING;
        int extLength = 0;

        if (!noext) {
            int extIndex = baseName.lastIndexOf(".");

            if (extIndex != -1) {
                ext = baseName.substring(extIndex, baseName.length());
                extLength = ext.length();
                baseName = baseName.substring(0, extIndex);

                if (extLength == 1) {
                    ext = StringUtil.EMPTY_STRING;
                    extLength = 0;
                }
            }
        }

        // 计算locale后缀。
        List result = Lists.newArrayList(4);
        String language = locale.getLanguage();
        int languageLength = language.length();
        String country = locale.getCountry();
        int countryLength = country.length();
        String variant = locale.getVariant();
        int variantLength = variant.length();

        StringBuffer buffer = new StringBuffer(baseName);

        buffer.append(ext);
        result.add(buffer.toString());
        buffer.setLength(buffer.length() - extLength);

        // 如果locale是("", "", "").
        if ((languageLength + countryLength + variantLength) == 0) {
            return result;
        }

        // 加入baseName_language，如果baseName为空，则不加下划线。
        if (buffer.length() > 0) {
            buffer.append('_');
        }

        buffer.append(language);

        if (languageLength > 0) {
            buffer.append(ext);
            result.add(buffer.toString());
            buffer.setLength(buffer.length() - extLength);
        }

        if ((countryLength + variantLength) == 0) {
            return result;
        }

        // 加入baseName_language_country
        buffer.append('_').append(country);

        if (countryLength > 0) {
            buffer.append(ext);
            result.add(buffer.toString());
            buffer.setLength(buffer.length() - extLength);
        }

        if (variantLength == 0) {
            return result;
        }

        // 加入baseName_language_country_variant
        buffer.append('_').append(variant);

        buffer.append(ext);
        result.add(buffer.toString());
        buffer.setLength(buffer.length() - extLength);

        return result;
    }

    /* ============================================================================ */
    /*  取得系统范围的、默认的、线程范围的locale和charset。                         */
    /*                                                                              */
    /*   系统作用域： 由JVM所运行的操作系统环境决定，在JVM生命期内不改变。          */
    /*   默认作用域： 在整个JVM中全局有效，可被改变。默认值同“系统locale”。       */
    /*   线程作用域： 在整个线程中全局有效，可被改变。默认值同“默认locale”。      */
    /* ============================================================================ */

    /**
     * 取得操作系统默认的区域。
     *
     * @return 操作系统默认的区域
     */
    public static LocaleInfo getSystem() {
        return systemLocaleInfo;
    }

    /**
     * 取得默认的区域。
     *
     * @return 默认的区域
     */
    public static LocaleInfo getDefault() {
        return (defaultLocalInfo == null) ? systemLocaleInfo
                : defaultLocalInfo;
    }

    /**
     * 取得当前thread默认的区域。
     *
     * @return 当前thread默认的区域
     */
    public static LocaleInfo getContext() {
        LocaleInfo contextLocaleInfo = (LocaleInfo) contextLocaleInfoHolder.get();

        return (contextLocaleInfo == null) ? getDefault()
                : contextLocaleInfo;
    }

}
