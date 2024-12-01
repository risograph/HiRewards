package ri.so.hiRewards.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ri.so.hiRewards.HiRewards;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.*;

public class PlayerChatListener implements Listener {
    private final HiRewards plugin;
    private final PlayerJoinListener playerJoinListener;
    private final Logger log = getLogger();

    public PlayerChatListener(PlayerJoinListener playerJoinListener, HiRewards plugin) {
        this.playerJoinListener = playerJoinListener;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        // Chat Event info
        Player p = e.getPlayer();
        long now = System.currentTimeMillis();

        // Latest Join info
        Player latestJoinPlayer       = playerJoinListener.getLatestJoinPlayer();
        long latestJoinTimestamp      = playerJoinListener.getLatestJoinTimestamp();
        boolean latestJoinIsFirstJoin = playerJoinListener.getLatestJoinIsFirstJoin();

        // Config info
        int greeting_window = plugin.getConfig().getInt("greeting_window");
        List<String> firstJoinGreetRewards = plugin.getConfig().getStringList("rewards.firstjoin");
        List<String> joinGreetRewards = plugin.getConfig().getStringList("rewards.join");
        List<String> regexPatterns = plugin.getConfig().getStringList("greetings");

        // Returns true if message matches the regex from config.yml
        boolean isGreeting = regexPatterns.stream().anyMatch(pattern -> {
            Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = compiledPattern.matcher(e.getMessage());
            return matcher.find();
        });

        // If message is valid greeting and sender isn't incoming player and reward window hasn't passed.
        // TODO: and player hasn't greeted joiner within cooldown
        if (isGreeting && p != latestJoinPlayer && now - latestJoinTimestamp < greeting_window * 1000L) {
            //p.sendMessage("You earned a reward for welcoming " + latestJoinPlayer.getDisplayName() + "!");
            log.info(p.getDisplayName() + " was rewarded for welcoming " + latestJoinPlayer.getDisplayName() + ".");
            // Reward executor
            if (latestJoinIsFirstJoin && !firstJoinGreetRewards.isEmpty()) {
                // rewards.firstjoin
                for (String firstJoinGreetReward : firstJoinGreetRewards) {
                    String command = firstJoinGreetReward.replace("%player%", p.getDisplayName());
                    getScheduler().runTask(plugin, () -> getServer().dispatchCommand(getConsoleSender(), command));
                }
            } else if (!latestJoinIsFirstJoin && !joinGreetRewards.isEmpty()) {
                // rewards.join
                for (String joinGreetReward : joinGreetRewards) {
                    String command = joinGreetReward.replace("%player%", p.getDisplayName());
                    getScheduler().runTask(plugin, () -> getServer().dispatchCommand(getConsoleSender(), command));
                }
            }
        }

        // Debug stuff
        //p.sendMessage("Last joining player " + latestJoinPlayer.getDisplayName() + " joined at " + latestJoinTimestamp);
        //p.sendMessage("It has been " + timeSinceJoin + "seconds since they joined.");
        //p.sendMessage("That is " + (timeSinceJoin > 20 ? "greater" : "less") + " than 20 seconds.");
    }
}
