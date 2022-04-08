import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

public class Helper {

  public static String instance = "<something>.eu-central-1";
  public static String user = "<user>";
  public static String password = "<password>";

  public static String db = "DWH";        // <-- needs to exist for tests to run
  public static String schema = "CI";     // <-- is dropped and re-created on every test

  public static Connection getConnection() throws Exception {
    String url = "jdbc:snowflake://"+instance+".snowflakecomputing.com";
    Properties props = new Properties();
    props.put("user", user);
    props.put("password", password);
    props.put("db", db);
    props.put("schema", schema);
    return DriverManager.getConnection(url, props);
  }

  public static void reset(Connection c) throws Exception {

    // workaround for https://github.com/snowflakedb/snowflake-jdbc/issues/533
    execStatement(c, "ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON';");

    // defined behavior for interpreting timestamps
    execStatement(c, "ALTER SESSION SET TIMEZONE = 'UTC';");

    // hard reset of schema test environment
    execStatement(c, "DROP SCHEMA IF EXISTS "+schema+" CASCADE;");
    execStatement(c, "CREATE SCHEMA "+schema+";");

  }

  public static boolean execStatement(Connection c, String sql) throws Exception {
    try (Statement s = c.createStatement()){
      return s.execute(sql);
    }
  }


}
