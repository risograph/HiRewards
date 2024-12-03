package ri.so.hiRewards.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import ri.so.hiRewards.HiRewards; // import your main class

public class SQLite extends Database{
    static String dbname;
    public SQLite(HiRewards instance){
        super(instance);
        dbname = "playerdata";
    }

    public String SQLiteCreateTokensTable = """
                CREATE TABLE IF NOT EXISTS playerdata (
                    greeter_uuid TEXT NOT NULL,
                    greeted_uuid TEXT NOT NULL,
                    greeting_timestamp INTEGER NOT NULL
                );
            """;

    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname);
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+ dbname);
            }
        }
        try {
            if(conn !=null&&!conn.isClosed()){
                return conn;
            }
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return conn;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        conn = getSQLConnection();
        try {
            assert conn != null;
            Statement s = conn.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, String.valueOf(ex));
        }
        initialize();
    }
}