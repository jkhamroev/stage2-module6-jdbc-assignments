package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            Properties props = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try(InputStream stream = loader.getResourceAsStream("app.properties")) {
                props.load(stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String driver = props.getProperty("postgres.driver");
            String url = props.getProperty("postgres.url");
            String password = props.getProperty("postgres.password");
            String name = props.getProperty("postgres.name");
            instance = new CustomDataSource(driver, url, password, name);
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        CustomConnector connector = new CustomConnector();
        return connector.getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
