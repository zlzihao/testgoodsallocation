package cn.nome.saas.search.model.vo;

import java.util.List;

/**
 * @author chentaikuang
 */
public class SearchHotRankWrap {

    List<SearchHotVO> hotVOS;
    private String rankTime;

    public List<SearchHotVO> getHotVOS() {
        return hotVOS;
    }

    public void setHotVOS(List<SearchHotVO> hotVOS) {
        this.hotVOS = hotVOS;
    }

    public String getRankTime() {
        return rankTime;
    }

    public void setRankTime(String rankTime) {
        this.rankTime = rankTime;
    }
}
