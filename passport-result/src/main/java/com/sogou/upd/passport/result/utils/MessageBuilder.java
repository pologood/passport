package com.sogou.upd.passport.result.utils;

import com.sogou.upd.passport.result.resourcebundle.ResourceBundleFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 通过资源束创建消息的工具类, 支持所有原子类型, 方便使用.
 * 使用方法:
 *   String message = new MessageBuilder(bundle, key)
 *                   .append(param1)
 *                   .append(param2)
 *                   .toString();
 * 在构造此类时, 可以提供一个<code>quiet</code>参数.  如果此参数为<code>true</code>, 并且resource bundle找不到,
 * 则不会抛出<code>MissingResourceException</code>, 而是返回一个默认的字符串.
 */
public class MessageBuilder {
    protected final List params = new ArrayList(5);
    protected final ResourceBundle bundle;
    protected final String key;

    /**
     * 创建一个<code>MessageBuilder</code>.
     *
     * @param bundleName 资源束
     * @param key        键值
     * @throws java.util.MissingResourceException
     *          指定bundle未找到, 或创建bundle错误
     */
    public MessageBuilder(String bundleName, String key) {
        this(ResourceBundleFactory.getBundle(bundleName), key);
    }

    /**
     * 创建一个<code>MessageBuilder</code>.
     *
     * @param bundle 资源束
     * @param key    键值
     */
    public MessageBuilder(ResourceBundle bundle, String key) {
        this.bundle = bundle;
        this.key = key;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(Object param) {
        params.add(param);
        return this;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(boolean param) {
        params.add(new Boolean(param));
        return this;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(char param) {
        params.add(new Character(param));
        return this;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(double param) {
        params.add(new Double(param));
        return this;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(float param) {
        params.add(new Float(param));
        return this;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(int param) {
        params.add(new Integer(param));
        return this;
    }

    /**
     * 增加一个参数.
     *
     * @param param 参数
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(long param) {
        params.add(new Long(param));
        return this;
    }

    /**
     * 增加多个参数.
     *
     * @param params 参数表
     * @return <code>MessageBuilder</code>自身
     */
    public MessageBuilder append(Object[] params) {
        if (params != null) {
            this.params.addAll(Arrays.asList(params));
        }

        return this;
    }

    /**
     * 取得消息字符串.
     *
     * @return 消息字符串
     */
    public String toString() {
        return getMessage();
    }

    /**
     * 从资源束中取得消息字符串.
     *
     * @return 消息字符串
     * @throws java.util.MissingResourceException
     *          指定resource key未找到
     */
    protected String getMessage() {
        return MessageUtil.getMessage(bundle, key, params.toArray());
    }
}

