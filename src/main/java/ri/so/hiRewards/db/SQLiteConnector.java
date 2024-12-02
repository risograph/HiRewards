package ri.so.hiRewards.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class SQLiteConnector {
    static Logger log = getLogger();

    private static final String DB_URL = "jdbc:sqlite:greetings.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            log.info(String.valueOf(e));
            return null;
        }
    }
}
