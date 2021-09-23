package cn.nome.saas.allocation.model.rule;

import cn.nome.platform.common.utils.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * GoodsCategroyTree
 *
 * @author Bruce01.fan
 * @date 2019/9/2
 */
public class GoodsCategroyTree extends ToString{

    private int level;

    private String name;

    private String fullName;

    private List<GoodsCategroyTree> childs;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<GoodsCategroyTree> getChilds() {
        return childs;
    }

    public void setChilds(List<GoodsCategroyTree> childs) {
        this.childs = childs;
    }

    public void addAllChilds(List<GoodsCategroyTree> childList) {
        if (this.childs == null) {
            this.childs = new ArrayList<>();
        }

        this.childs.addAll(childList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsCategroyTree that = (GoodsCategroyTree) o;

        if (level != that.level) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = level;
        result = 31 * result + name.hashCode();
        return result;
    }
}
