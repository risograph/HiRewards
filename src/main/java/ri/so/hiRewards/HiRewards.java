package ri.so.hiRewards;

import org.bukkit.plugin.java.JavaPlugin;
import ri.so.hiRewards.listeners.PlayerChatListener;
import ri.so.hiRewards.listeners.PlayerJoinListener;

import java.util.logging.Logger;

public final class HiRewards extends JavaPlugin {

    Logger log = getLogger();

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        PlayerJoinListener joinListener = new PlayerJoinListener();
        getServer().getPluginManager().registerEvents(joinListener, this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(joinListener, this), this);

        log.info("Plugin loaded successfully.");
    }
}
