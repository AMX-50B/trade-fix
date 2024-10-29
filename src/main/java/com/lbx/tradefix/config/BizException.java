package com.lbx.tradefix.config;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.driver.Message;

/**
 * @author LiuY
 * @date 2024/10/29
 **/
@Slf4j
@Data
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String code;
    private String msg;
    private String[] detail;


    public static BizException builder() {
        return new BizException();
    }

    public  BizException msg(String msg) {
        this.msg = msg;
        return this;
    }
    public BizException detail(String... detail) {
        this.detail = detail;
        return this;
    }

    public BizException code(String code) {
        this.code = code;
        return this;
    }
    public BizException build() {
        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int pos = 0;
        while (pos < msg.length()) {
            int placeholderPos = msg.indexOf("{}", pos);
            if (placeholderPos == -1) {
                result.append(msg.substring(pos));
                break;
            }
            result.append(msg, pos, placeholderPos);
            if (argIndex < detail.length) {
                result.append(detail[argIndex]);
                argIndex++;
            } else {
                result.append("{}");
            }
            pos = placeholderPos + 2; // Move past "{}"
        }
        this.msg = result.toString();
        return this;
    }
}
