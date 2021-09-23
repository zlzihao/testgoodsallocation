package cn.nome.saas.sdc.model.form;


import cn.nome.platform.common.utils.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/6 14:04
 */
public class AreasForm extends ToString {

    private Integer id;

    private Integer corpId;

    @NotNull(message = "区域类型不能为空")
    private Integer areaTypeId;

    private Integer parentId;

    @NotNull(message = "区域名称不能为空")
    @Length(max = 128, min = 0, message = "区域名称字符长度不能超过128")
    private String areaName;

    @Length(max = 64, min = 0, message = "区域编码字符长度不能超过64")
    private String areaCode;

    @Length(max = 256, min = 0, message = "区域编码说明字符长度不能超过256")
    private String areaCodeDescription;

    @Length(max = 256, min = 0, message = "地区位置(对应省份)字符长度不能超过256")
    private String locations;

    @Length(max = 32, min = 0, message = "区域经理字符长度不能超过32")
    private String areaManager;

    @Length(max = 32, min = 0, message = "区域经理工号字符长度不能超过32")
    private String areaManagerJobNumber;

    private Long createUserId;

    private Long lastUpdateUserId;

    private Integer isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaCodeDescription() {
        return areaCodeDescription;
    }

    public void setAreaCodeDescription(String areaCodeDescription) {
        this.areaCodeDescription = areaCodeDescription;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getAreaManager() {
        return areaManager;
    }

    public void setAreaManager(String areaManager) {
        this.areaManager = areaManager;
    }

    public String getAreaManagerJobNumber() {
        return areaManagerJobNumber;
    }

    public void setAreaManagerJobNumber(String areaManagerJobNumber) {
        this.areaManagerJobNumber = areaManagerJobNumber;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}