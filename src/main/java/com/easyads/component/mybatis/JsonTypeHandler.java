package com.easyads.component.mybatis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;

        // 创建 ObjectMapper 并配置忽略 null 字段
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toObject(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toObject(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toObject(cs.getString(columnIndex));
    }

    private String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON string: " + e.getMessage(), e);
        }
    }

    private T toObject(String content) {
        try {
            if (content == null || content.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(content, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON string to object: " + e.getMessage(), e);
        }
    }
}