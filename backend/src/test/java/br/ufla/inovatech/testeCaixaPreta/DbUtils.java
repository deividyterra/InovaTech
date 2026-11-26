package br.ufla.inovatech.testeCaixaPreta;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DbUtils {

    private static final Properties CONFIG = carregarConfig();

    private static Properties carregarConfig() {
        try (InputStream in = DbUtils.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties p = new Properties();
            p.load(in);
            return p;
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível ler db.properties", e);
        }
    }

    private static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"),
                CONFIG.getProperty("db.pass"));
    }

    public static int executar(String sql, Object... params) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar: " + sql, e);
        }
    }

    public static Object buscarValor(String sql, Object... params) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getObject(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar: " + sql, e);
        }
    }
}