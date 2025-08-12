package com.example.demo.config;

import org.apache.ibatis.type.TypeHandler;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.context.annotation.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis-Plus配置类
 */
@Configuration
public class MyBatisConfig {

    /**
     * MyBatis-Plus分页插件配置
     * 注释掉MyBatis-Plus分页插件，改用PageHelper
     */
    /*@Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }*/

    /**
     * 几何数据类型处理器
     * 用于处理MySQL的GEOMETRY类型与JTS Geometry对象之间的转换
     */
    public static class GeometryTypeHandler implements TypeHandler<Geometry> {
        
        private final WKTReader wktReader = new WKTReader();
        private final WKTWriter wktWriter = new WKTWriter();

        @Override
        public void setParameter(PreparedStatement ps, int i, Geometry parameter, org.apache.ibatis.type.JdbcType jdbcType) throws SQLException {
            if (parameter == null) {
                ps.setObject(i, null);
            } else {
                // 将Geometry对象转换为WKT字符串，然后使用ST_GeomFromText函数
                String wkt = wktWriter.write(parameter);
                ps.setString(i, "ST_GeomFromText('" + wkt + "')");
            }
        }

        @Override
        public Geometry getResult(ResultSet rs, String columnName) throws SQLException {
            String wkt = rs.getString(columnName);
            return parseGeometry(wkt);
        }

        @Override
        public Geometry getResult(ResultSet rs, int columnIndex) throws SQLException {
            String wkt = rs.getString(columnIndex);
            return parseGeometry(wkt);
        }

        @Override
        public Geometry getResult(CallableStatement cs, int columnIndex) throws SQLException {
            String wkt = cs.getString(columnIndex);
            return parseGeometry(wkt);
        }

        private Geometry parseGeometry(String wkt) throws SQLException {
            if (wkt == null || wkt.trim().isEmpty()) {
                return null;
            }
            try {
                // 如果是二进制格式，需要先转换为WKT
                if (wkt.startsWith("\\x") || !wkt.contains("(")) {
                    // 这里可能需要处理MySQL返回的二进制格式
                    // 暂时返回null，实际使用时需要根据MySQL的具体返回格式进行处理
                    return null;
                }
                return wktReader.read(wkt);
            } catch (ParseException e) {
                throw new SQLException("Failed to parse geometry: " + wkt, e);
            }
        }
    }
}
