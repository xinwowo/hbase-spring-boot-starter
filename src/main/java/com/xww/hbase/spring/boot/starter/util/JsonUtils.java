package com.xww.hbase.spring.boot.starter.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public class JsonUtils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"));
        objectMapper.getSerializationConfig()
                .with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.getDeserializationConfig()
                .with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("将对象转换为Json字符串时出现异常[object=" + obj + "]", e);
        }
    }

    public static byte[] toJsonBytes(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException("将对象转换为Json字节数组时出现异常[object=" + obj + "]", e);
        }
    }

    public static <T> T toObject(String jsonString, Class<T> objClazz) {
        try {
            return objectMapper.readValue(jsonString, objClazz);
        } catch (Exception e) {
            throw new RuntimeException("将Json字符串转换为对象时出现异常[str=" + jsonString + ";objectType=" + objClazz + "]", e);
        }
    }

    public static <T> T toObject(byte[] bytes, Class<T> objClazz) {
        try {
            return objectMapper.readValue(bytes, objClazz);
        } catch (Exception e) {
            throw new RuntimeException("将Json字节数组转换为对象时出现异常[objectType=" + objClazz + "]", e);
        }
    }

    public static <T> List<T> toObjectList(String str, TypeReference<List<T>> typeReference) {
        try {
            return objectMapper.readValue(str, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("将Json字符串转换为对象列表时出现异常[str=" + str + ";typeReference=" + typeReference + "]", e);
        }
    }

    public static Map<String, Object> toJsonMap(Object object) {
        JSONObject jsonObj = (JSONObject) JSON.toJSON(object);
        return jsonObj;
    }
}
