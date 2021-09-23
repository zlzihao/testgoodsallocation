package cn.nome.saas.search.model.vo;


import java.util.Date;
import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="搜索单词配置表")
public class SearchWordConfVO extends ToString {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="主键")
	private Integer id;

	@ApiModelProperty(value="单词")
	private String word;

	@ApiModelProperty(value="单词类型,0其他,1引导词,2热词")
	private Integer wordType;

	@ApiModelProperty(value="跳转页面ID")
	private Integer jumpId;

	@ApiModelProperty(value="跳转路径名称")
	private String jumpName;

	@ApiModelProperty(value="跳转类型,0无操作,1跳活动页,2跳搜索")
	private Integer jumpType;

	@ApiModelProperty(value="跳转路径")
	private String jumpUrl;

	@ApiModelProperty(value="状态，0初始化,1发布,2停止")
	private Integer status;

	@ApiModelProperty(value="排序值")
	private Integer sortNum;

	@ApiModelProperty(value="开始时间")
	private Date startTime;

	@ApiModelProperty(value="结束时间")
	private Date endTime;

	@ApiModelProperty(value="发布时间")
	private Date releaseTime;

	@ApiModelProperty(value="创建时间")
	private Date createdAt;

	@ApiModelProperty(value="备注")
	private String remark;

	public Integer getJumpId() {
		return jumpId;
	}

	public void setJumpId(Integer jumpId) {
		this.jumpId = jumpId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setWordType(Integer wordType) {
		this.wordType = wordType;
	}

	public Integer getWordType() {
		return wordType;
	}

	public void setJumpName(String jumpName) {
		this.jumpName = jumpName;
	}

	public String getJumpName() {
		return jumpName;
	}

	public void setJumpType(Integer jumpType) {
		this.jumpType = jumpType;
	}

	public Integer getJumpType() {
		return jumpType;
	}

	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}

	public String getJumpUrl() {
		return jumpUrl;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

}
