package ri.so.hiRewards;

import org.bukkit.plugin.java.JavaPlugin;
import ri.so.hiRewards.commands.MainCommands;
import ri.so.hiRewards.db.Database;
import ri.so.hiRewards.db.SQLite;
import ri.so.hiRewards.listeners.PlayerChatListener;
import ri.so.hiRewards.listeners.PlayerJoinListener;

import java.util.logging.Logger;

public final class HiRewards extends JavaPlugin {

    Logger log = getLogger();
    private Database db;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        this.db = new SQLite(this);
        this.db.load();

        PlayerJoinListener joinListener = new PlayerJoinListener();
        getServer().getPluginManager().registerEvents(joinListener, this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(joinListener, this), this);

        log.info("Plugin loaded successfully.");
    }

    public Database getDatabase() {
        return this.db;
    }

}
