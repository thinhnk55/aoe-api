package vn.vietdefi.util.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class HikariClient {
    public HikariDataSource dataSource;

    public HikariClient(String jdbcUrl, String user, String password, String database, String otherConfigFile) throws IOException {
        InputStream input = new FileInputStream(otherConfigFile);
        Properties prop = new Properties();
        prop.put("jdbcUrl", jdbcUrl);
        prop.put("dataSource.user", user);
        prop.put("dataSource.password", password);
        prop.put("dataSource.database", database);
        prop.load(input);
        HikariConfig config = new HikariConfig(prop);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection;
    }

    public void releaseConnection(Connection connection, PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null)
                preparedStatement.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        try {
            if (connection != null)
                connection.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void releaseConnection(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        try {
            if (preparedStatement != null)
                preparedStatement.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        try {
            if (connection != null)
                connection.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
