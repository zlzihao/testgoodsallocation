package cn.nome.saas.allocation.feign.model;

import cn.nome.platform.common.utils.ToString;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Y3TokenRequest
 *
 * @author Bruce01.fan
 * @date 2020/2/9
 */
public class Y3TokenRequest extends ToString {

    @JsonProperty("AppId")
    private String AppId;

    @JsonProperty("Nonce")
    private String Nonce;

    @JsonProperty("Timestamp")
    private String Timestamp;

    @JsonProperty("Signature")
    private String Signature;

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getNonce() {
        return Nonce;
    }

    public void setNonce(String nonce) {
        Nonce = nonce;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }
}
