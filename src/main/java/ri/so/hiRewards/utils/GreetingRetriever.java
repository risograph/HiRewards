package ri.so.hiRewards.utils;

import ri.so.hiRewards.db.SQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class GreetingRetriever {

    private static final Logger log = getLogger();

    public static long getLastGreetingTimestamp(String greeterUuid, String greetedUuid) {
        String querySQL = """
            SELECT greeting_timestamp
            FROM greetings
            WHERE greeter_uuid = ? AND greeted_uuid = ?
            ORDER BY greeting_timestamp DESC
            LIMIT 1;
            """;

        try (Connection conn = SQLiteConnector.connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
                pstmt.setString(1, greeterUuid);
                pstmt.setString(2, greetedUuid);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getLong("greeting_timestamp");
                }
            }
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }

        return 0; // Return 0 if no record is found
    }
}