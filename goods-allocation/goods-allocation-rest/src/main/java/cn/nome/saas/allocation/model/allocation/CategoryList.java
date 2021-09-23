package cn.nome.saas.allocation.model.allocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CategoryList
 *
 * @author Bruce01.fan
 * @date 2019/8/27
 */
public class CategoryList {

    Set<String> bigCategory = new HashSet<>();

    Set<String> middleCategory = new HashSet<>();

    Set<String> smallCategory = new HashSet<>();

    public Set<String> getBigCategory() {
        return bigCategory;
    }

    public void setBigCategory(Set<String> bigCategory) {
        this.bigCategory = bigCategory;
    }

    public Set<String> getMiddleCategory() {
        return middleCategory;
    }

    public void setMiddleCategory(Set<String> middleCategory) {
        this.middleCategory = middleCategory;
    }

    public Set<String> getSmallCategory() {
        return smallCategory;
    }

    public void setSmallCategory(Set<String> smallCategory) {
        this.smallCategory = smallCategory;
    }
}
