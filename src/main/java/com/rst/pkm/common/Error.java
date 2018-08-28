package com.rst.pkm.common;

/**
 * @author hujia
 */
public class Error {
    /**
     * 在这里定义错误消息
     */
    public static final Error SERVER_EXCEPTION = make(500, "服务器内部错误，请稍后再试");
    public static final Error HTTP_CONTENT_INVALID = make(-10003, "无法解析请求内容");
    public static final Error SIGNATURE_NOT_PRESENT = make(-10004, "拒绝访问，缺少signature");
    public static final Error SID_NOT_PRESENT = make(-10004, "拒绝访问，缺少serviceId");
    public static final Error SIGNATURE_INVALID = make(-10004, "拒绝访问，签名错误");
    public static final Error SID_INVALID = make(-10004, "拒绝访问，serviceId错误");
    public static final Error ADMIN_PWD_INVALID = make(-10004, "拒绝访问，管理员密码错误");
    public static final Error REQUEST_EXPIRED = make(-10005, "拒绝访问，请求过期");
    public static final Error PUB_HASH_INVALID = make(-10006, "无效的公钥哈希");
    public static final Error IP_INVALID = make(-10006, "该IP禁止访问");
    public static final Error NONE_CANONICAL_SIGNATURE = make(-10010, "无法找到合法的签名");

    public String msg = "";
    public int code = -1;

    Error(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "{code:" + code + ",msg:" + msg + "}";
    }

    public static Error make(int code, String msg) {
        return new Error(code, msg);
    }
}
