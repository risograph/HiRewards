package ri.so.hiRewards.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import ri.so.hiRewards.HiRewards; // Import main class!


public abstract class Database {
    static HiRewards plugin;
    static Connection conn;
    public Database(HiRewards instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        conn = getSQLConnection();
        try{
            assert conn != null;
            PreparedStatement ps = conn.prepareStatement("SELECT greeting_timestamp FROM playerdata WHERE greeter_uuid = ? AND greeted_uuid = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    public long getTimestamp(Player greeter, Player greeted) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            assert conn != null;
            ps = conn.prepareStatement("SELECT greeting_timestamp FROM playerdata WHERE greeter_uuid = ? AND greeted_uuid = ?");
            ps.setString(1, String.valueOf(greeter.getUniqueId()));
            ps.setString(2, String.valueOf(greeted.getUniqueId()));
            rs = ps.executeQuery();

            if (rs.next()) { // Check if a result exists
                return rs.getLong("greeting_timestamp");
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public void setTimestamp(Player greeter, Player greeted, long time) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            assert conn != null;
            ps = conn.prepareStatement("INSERT INTO playerdata (greeting_timestamp, greeter_uuid, greeted_uuid) VALUES (?, ?, ?)");
            ps.setLong(1, time); // The new timestamp value
            ps.setString(2, String.valueOf(greeter.getUniqueId()));
            ps.setString(3, String.valueOf(greeted.getUniqueId()));
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}