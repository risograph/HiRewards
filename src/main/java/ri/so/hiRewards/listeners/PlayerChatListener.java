package ri.so.hiRewards.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    private final PlayerJoinListener playerJoinListener;

    public PlayerChatListener(PlayerJoinListener playerJoinListener) {
        this.playerJoinListener = playerJoinListener;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Player latestJoinPlayer = playerJoinListener.getLatestJoinPlayer();
        long latestJoinTimestamp = playerJoinListener.getLatestJoinTimestamp();
        p.sendMessage("Last joining player " + latestJoinPlayer.getDisplayName() + " joined at " + latestJoinTimestamp);
    }
}
