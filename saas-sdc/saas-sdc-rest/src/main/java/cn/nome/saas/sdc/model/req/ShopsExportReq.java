package cn.nome.saas.sdc.model.req;

import cn.nome.platform.common.utils.ToString;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/11/11 14:09
 */
public class ShopsExportReq extends ToString {

    @NotEmpty(message = "属性类型不能为空")
    private String attributeTypes;

    private Integer corpId;

    public String getAttributeTypes() {
        return attributeTypes;
    }

    public void setAttributeTypes(String attributeTypes) {
        this.attributeTypes = attributeTypes;
    }

    public List<Integer> getAttributeTypesList() {
        List<Integer> list = new ArrayList<>();
        String[] items = Strings.split(StringUtils.trim(this.attributeTypes), ',');
        for (String item : items) {
            list.add(Integer.parseInt(item));
        }
        return list;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }
}
