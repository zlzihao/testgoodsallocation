package cn.nome.saas.search.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public class BaseModel {

    @NotNull(message = "搜索关键字不可为空")
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
