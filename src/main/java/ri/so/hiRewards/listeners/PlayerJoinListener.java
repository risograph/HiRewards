package ri.so.hiRewards.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

import static org.bukkit.Bukkit.getLogger;

public class PlayerJoinListener implements Listener {

    private Player latestJoinPlayer;
    private long latestJoinTimestamp;
    private boolean latestJoinIsFirstJoin;

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player p = e.getPlayer();
        latestJoinPlayer = p;
        latestJoinTimestamp = System.currentTimeMillis();
        latestJoinIsFirstJoin = !p.hasPlayedBefore();
        //p.sendMessage("Welcome, " + latestJoinPlayer.getDisplayName() + ".");
        //log.info("Player " + latestJoinPlayer.getDisplayName() + " joined at " + latestJoinTimestamp);
        //log.info("Player is joining for the first time? " + latestJoinIsFirstJoin);
    }

    public Player getLatestJoinPlayer() { return latestJoinPlayer; }
    public long getLatestJoinTimestamp() { return latestJoinTimestamp; }
    public boolean getLatestJoinIsFirstJoin() { return latestJoinIsFirstJoin; }

}
