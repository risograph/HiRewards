package ri.so.hiRewards.utils;

import ri.so.hiRewards.db.SQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class GreetingLogger {

    private static final Logger log = getLogger();

    public static void logGreeting(String greeterUuid, String greetedUuid, long timestamp) {
        String insertSQL = "INSERT INTO greetings (greeter_uuid, greeted_uuid, greeting_timestamp) VALUES (?, ?, ?);";

        try (Connection conn = SQLiteConnector.connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, greeterUuid);
                pstmt.setString(2, greetedUuid);
                pstmt.setLong(3, timestamp);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }
    }
}