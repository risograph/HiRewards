package ri.so.hiRewards.db;

import ri.so.hiRewards.HiRewards;

import java.util.logging.Level;

public class Error {
    public static void execute(HiRewards plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(HiRewards plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}