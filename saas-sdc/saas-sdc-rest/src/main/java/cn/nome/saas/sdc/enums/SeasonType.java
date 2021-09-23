package cn.nome.saas.sdc.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lizihao@nome.com
 */
public enum SeasonType {
    SPRING(1, "春"),
    SUMMER(2, "夏"),
    AUTUMN(3, "秋"),
    WINTER(4, "冬"),
    SPRING_VALUE(5, "春季"),
    SUMMER_VALUE(4, "夏季"),
    AUTUMN_VALUE(4, "秋季"),
    WINTER_VALUE(4, "冬季"),
    ;
    private List<String> list;
    private Integer type;

    private String msg;

    SeasonType(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static List<String> getSeason() {
        List<String> list = new ArrayList<>();
        list.add(SPRING.getMsg());
        list.add(SUMMER.getMsg());
        list.add(AUTUMN.getMsg());
        list.add(WINTER.getMsg());
        list.add(SPRING_VALUE.getMsg());
        list.add(SUMMER_VALUE.getMsg());
        list.add(AUTUMN_VALUE.getMsg());
        list.add(WINTER_VALUE.getMsg());
        return list;
    }
}
