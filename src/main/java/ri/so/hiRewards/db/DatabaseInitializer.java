package ri.so.hiRewards.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class DatabaseInitializer {

    static Logger log = getLogger();

    public static void initialize() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS greetings (
                    greeter_uuid TEXT NOT NULL,
                    greeted_uuid TEXT NOT NULL,
                    greeting_timestamp INTEGER NOT NULL
                );
                """;

        try (Connection conn = SQLiteConnector.connect()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            log.info(String.valueOf(e));
        }
    }
}