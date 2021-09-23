package cn.nome.saas.search.model;


import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import java.util.Date;

@ApiModel(value = "修改单词配置model")
public class SearchWordConfModifyModel extends ToString {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", required = true)
    private Integer id;

    @ApiModelProperty(value = "单词", required = true)
    private String word;

    @ApiModelProperty(value = "单词类型,0其他,1引导词,2热词", required = true)
    @Min(value = 1)
    private Integer wordType;

    @ApiModelProperty(value = "跳转页面ID", required = true)
    @Min(value = 0)
    private Integer jumpId;

    @ApiModelProperty(value = "跳转路径名称", required = true)
    private String jumpName;

    @ApiModelProperty(value = "跳转类型,0无操作,1跳活动页,2跳搜索", required = true)
    @Min(value = 1)
    private Integer jumpType;

    @ApiModelProperty(value = "跳转路径", required = true)
    private String jumpUrl;

    @ApiModelProperty(value = "排序值")
    private Integer sortNum = 0;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    public Integer getJumpId() {
        return jumpId;
    }

    public void setJumpId(Integer jumpId) {
        this.jumpId = jumpId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getWordType() {
        return wordType;
    }

    public void setWordType(Integer wordType) {
        this.wordType = wordType;
    }

    public String getJumpName() {
        return jumpName;
    }

    public void setJumpName(String jumpName) {
        this.jumpName = jumpName;
    }

    public Integer getJumpType() {
        return jumpType;
    }

    public void setJumpType(Integer jumpType) {
        this.jumpType = jumpType;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }
}
