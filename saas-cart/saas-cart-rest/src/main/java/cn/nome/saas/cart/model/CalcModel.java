package cn.nome.saas.cart.model;

import java.util.List;

/**
 * @author chentaikuang
 */
public class CalcModel extends BaseModel {

    private static final long serialVersionUID = 5717499820030949125L;
    private int uid;
    private List<CalcSkuModel> calcSku;

    public CalcModel(Integer uid, Integer appId, Integer corpId, List<CalcSkuModel> calcSku) {
        this.setUid(uid);
        this.setAppId(appId);
        this.setCorpId(corpId);
        this.setCalcSku(calcSku);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public List<CalcSkuModel> getCalcSku() {
        return calcSku;
    }

    public void setCalcSku(List<CalcSkuModel> calcSku) {
        this.calcSku = calcSku;
    }
}
