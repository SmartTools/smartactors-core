package info.smart_tools.smartactors.core.sql_commons;

public interface JDBCConnectionOptions {
    String getUrl();

    String getUsername();

    String getPassword();

    Integer getValidationTimeout();
}
