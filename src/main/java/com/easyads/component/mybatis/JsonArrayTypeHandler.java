package com.easyads.component.mybatis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonArrayTypeHandler<T> extends BaseTypeHandler<List<T>> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<List<T>> typeReference;

    // 无参构造函数，用于 MyBatis 反射创建实例
    public JsonArrayTypeHandler() {
        this.typeReference = new TypeReference<List<T>>() {};  // 默认类型引用
    }

    public JsonArrayTypeHandler(TypeReference<List<T>> typeReference) {
        this.typeReference = Objects.requireNonNull(typeReference, "TypeReference argument cannot be null");
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toObject(rs.getString(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toObject(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toObject(cs.getString(columnIndex));
    }

    private String toJson(List<T> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON string. Object: " + object, e);
        }
    }

    private List<T> toObject(String content) {
        try {
            if (content == null || content.trim().isEmpty()) {
                return Collections.emptyList(); // 返回空集合
            }
            return objectMapper.readValue(content, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON string to object. JSON: \"" + content + "\", error: " + e.getMessage(), e);
        }
    }
}
