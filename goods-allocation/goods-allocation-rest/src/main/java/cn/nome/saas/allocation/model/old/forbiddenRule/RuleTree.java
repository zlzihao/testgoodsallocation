package cn.nome.saas.allocation.model.old.forbiddenRule;

import cn.nome.platform.common.utils.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * RuleTree
 *
 * @author Bruce01.fan
 * @date 2019/5/25
 */
public class RuleTree extends ToString {


    public static final int AREA_TYPE = 1; // 区域纬度
    public static final int GOODS_TYPE = 2; // 商品纬度

    public static final int REGION_LEVEL = 1;
    public static final int PROVINCE_LEVEL = 2;
    public static final int CITY_LEVEL = 3;
    public static final int SHOP_LEVEL = 4;

    public static final int BIG_LEVEL = 1;
    public static final int MIDDLE_LEVEL = 2;
    public static final int SMALL_LEVEL = 3;
    public static final int SKC_LEVEL = 4;
    public static final int SIZE_ID_LEVEL = 5;

    private int type;

    private String name;

    private String code;

    private int level;

    private boolean leaf = false;

    private List<RuleTree> childList;

    public static RuleTree buildAreaRuleTree(){
        RuleTree ruleTree = new RuleTree();
        ruleTree.setType(AREA_TYPE);
        return ruleTree;
    }

    public static RuleTree buildGoodsRuleTree(){
        RuleTree ruleTree = new RuleTree();
        ruleTree.setType(GOODS_TYPE);
        return ruleTree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleTree ruleTree = (RuleTree) o;

        if (type != ruleTree.type) return false;
        if (level != ruleTree.level) return false;
        if (name != null ? !name.equals(ruleTree.name) : ruleTree.name != null) return false;
        if (code != null ? !code.equals(ruleTree.code) : ruleTree.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + level;
        return result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<RuleTree> getChildList() {
        return childList;
    }

    public void setChildList(List<RuleTree> childList) {
        this.childList = childList;
    }

    public void addAllChilds(List<RuleTree> childList) {
        if (this.childList == null) {
            this.childList = new ArrayList<>();
        }

        this.childList.addAll(childList);
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
}
