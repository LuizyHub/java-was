package csv;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class CsvJdbcDriver implements Driver {

    static {
        try {
            DriverManager.registerDriver(new CsvJdbcDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!url.startsWith("jdbc:csv:")) {
            return null;
        }
        String filePath = url.substring("jdbc:csv:".length());
        return new CsvConnection(filePath);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:csv:");
    }

    // 나머지 메서드들은 간단히 구현합니다.
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }
}
