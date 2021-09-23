package cn.nome.saas.search.model;


import cn.nome.platform.common.utils.ToString;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;

@ApiModel(value = "单词配置分页列表model")
public class SearchWordConfListModel extends ToString {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单词类型,0其他,1引导词,2热词", required = true)
    @Min(value = 1)
    private Integer wordType;

    @ApiModelProperty(value = "当前页", required = true)
    @Min(value = 1,message = "当前页最小为1")
    private int curPage = 1;

    @ApiModelProperty(value = "页条数", required = true)
    @Min(value = 1,message = "页条数最小为1")
    private int pageSize = 10;

    public Integer getWordType() {
        return wordType;
    }

    public void setWordType(Integer wordType) {
        this.wordType = wordType;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
