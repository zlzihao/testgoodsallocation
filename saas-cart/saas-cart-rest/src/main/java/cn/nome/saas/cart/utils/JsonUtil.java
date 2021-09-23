package cn.nome.saas.cart.utils;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @author chentaikuang
 */
public class JsonUtil {

    /**
     * [{XX:XX},{YY,BB}] -> List<Object>
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
        return ts;
    }
}
