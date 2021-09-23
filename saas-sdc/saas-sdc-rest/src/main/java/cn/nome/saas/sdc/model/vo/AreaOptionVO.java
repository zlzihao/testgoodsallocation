package cn.nome.saas.sdc.model.vo;

import cn.nome.platform.common.utils.ToString;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/25 10:30
 */
public class AreaOptionVO extends ToString {

    private Integer id;
    private Integer areaTypeId;
    private Integer parentId;
    private String areaName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAreaTypeId() {
        return areaTypeId;
    }

    public void setAreaTypeId(Integer areaTypeId) {
        this.areaTypeId = areaTypeId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        return "AreaOptionVO{" +
                "id=" + id +
                ", areaTypeId=" + areaTypeId +
                ", parentId=" + parentId +
                ", areaName='" + areaName + '\'' +
                '}';
    }
}
