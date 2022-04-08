import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FloatsTest {

  private static Connection c;

  @BeforeEach
  void setUp() throws Exception {
    c = Helper.getConnection();
    Helper.reset(c);
  }

  @AfterEach
  void tearDown() throws Exception{
    if (c != null){
      c.close();
    }
  }

  @Test
  void roundTripDouble() throws Exception {
    Helper.execStatement(c,"CREATE TABLE ci.floats (id VARCHAR(10), num DOUBLE PRECISION);");

    // inserting e, expecting to keep all digits of precision
    Double expectedNum = 2.718281828459045d;

    try(PreparedStatement stmt = c.prepareStatement("INSERT INTO ci.floats VALUES ('e', ?);")){
      stmt.setDouble(1, expectedNum);
      stmt.execute();
    };

    Double readNum;

    try(PreparedStatement stmt = c.prepareStatement("SELECT num FROM ci.floats WHERE id = 'e';");
        ResultSet rs = stmt.executeQuery()){

      rs.next();
      readNum = rs.getDouble(1);
    }
    assertEquals(expectedNum, readNum); // read double is truncated to 9 digits

  }
}