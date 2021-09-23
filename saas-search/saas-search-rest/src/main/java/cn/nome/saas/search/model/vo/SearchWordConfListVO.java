package cn.nome.saas.search.model.vo;


import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "单词配置列表明细VO")
public class SearchWordConfListVO extends ToString {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "单词")
    private String word;

    @ApiModelProperty(value = "单词类型,0其他,1引导词,2热词")
    private Integer wordType;

    @ApiModelProperty(value="跳转页面ID")
    private Integer jumpId;

    @ApiModelProperty(value = "跳转路径名称")
    private String jumpName = "";

    @ApiModelProperty(value = "跳转类型,0无操作,1跳活动页,2跳搜索")
    private Integer jumpType;

    @ApiModelProperty(value = "状态，0初始化,1发布,2停止")
    private Integer status;

    @ApiModelProperty(value = "排序值")
    private Integer sortNum;

    @ApiModelProperty(value = "开始时间")
    private String startTime = "";

    @ApiModelProperty(value = "结束时间")
    private String endTime ="";

    @ApiModelProperty(value = "发布时间")
    private String releaseTime = "";

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

    public Integer getJumpId() {
        return jumpId;
    }

    public void setJumpId(Integer jumpId) {
        this.jumpId = jumpId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
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

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }
}
