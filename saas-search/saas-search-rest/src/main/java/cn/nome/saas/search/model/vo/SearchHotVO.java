package cn.nome.saas.search.model.vo;

/**
 * @author chentaikuang
 */
public class SearchHotVO {
    private String word;
    private long count;

    public SearchHotVO(String word, long count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
