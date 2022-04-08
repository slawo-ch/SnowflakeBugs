import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LiteralDecimalsTest {

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
  void decimalLiterals() throws Exception {
    Helper.execStatement(c,"CREATE TABLE ci.decimals (id VARCHAR(10), x_30_20 DECIMAL(30,20));");
    // inserting a value with too many digits, expecting rounding to 2.71828182845904523536
    Helper.execStatement(c,"INSERT INTO ci.decimals VALUES ('e', 2.718281828459045235360287471352662497757247093699959574966967627724076630353)");

    BigDecimal expectedNum = new BigDecimal("2.71828182845904523536");
    BigDecimal readNum;

    try(PreparedStatement stmt = c.prepareStatement("SELECT x_30_20 FROM ci.decimals WHERE id = 'e';");
        ResultSet rs = stmt.executeQuery()){

      rs.next();
      readNum = rs.getBigDecimal(1);
    }

    assertEquals(expectedNum, readNum); // decimal gets out of sync after digit 16...

  }
}