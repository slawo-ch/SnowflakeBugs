import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimestampsTest {

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
  void readSubSecondPrecisionTsNeg() throws Exception {
    Helper.execStatement(c,"CREATE TABLE ci.datetimes (id VARCHAR(10), ts TIMESTAMP_TZ);");

    // inserting timestamp - with sub-second precision
    ZonedDateTime expectedTime = ZonedDateTime.of(
      1969, 7, 20, 20, 17, 40, 123000000, ZoneId.of("UTC"));

    try(PreparedStatement stmt = c.prepareStatement("INSERT INTO ci.datetimes VALUES ('moon', ?);")){
      stmt.setTimestamp(1, new Timestamp(expectedTime.toInstant().toEpochMilli()));
      stmt.execute();
    };

    Instant expectedTs = expectedTime.toInstant();
    Timestamp readTs;

    try(PreparedStatement stmt = c.prepareStatement("SELECT ts FROM ci.datetimes WHERE id = 'moon';");
        ResultSet rs = stmt.executeQuery()){

      rs.next();
      readTs = rs.getTimestamp(1);
    }

    assertEquals(expectedTs, readTs.toInstant()); // off by a second

  }
}