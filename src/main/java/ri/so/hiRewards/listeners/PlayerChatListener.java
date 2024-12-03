package ri.so.hiRewards.listeners;

import org.bukkit.entity.Player;
import ri.so.hiRewards.HiRewards;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.*;

public class PlayerChatListener implements Listener {
    private final HiRewards plugin;
    private final PlayerJoinListener playerJoinListener;

    public PlayerChatListener(PlayerJoinListener playerJoinListener, HiRewards plugin) {
        this.playerJoinListener = playerJoinListener;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) throws ClassNotFoundException {

        // Chat Event info
        Player p = e.getPlayer();
        long now = System.currentTimeMillis();

        // Join Event info
        Player latestJoinPlayer       = playerJoinListener.getLatestJoinPlayer();
        long latestJoinTimestamp      = playerJoinListener.getLatestJoinTimestamp();
        boolean latestJoinIsFirstJoin = playerJoinListener.getLatestJoinIsFirstJoin();

        // DB info
        long lastGreetingTimestamp = plugin.getDatabase().getTimestamp(p, latestJoinPlayer);
        //long lastGreetingTimestamp = 0;

        // Config info
        // TODO: don't load this all immediately. probably affects performance
        List<String> regexPatterns         = plugin.getConfig().getStringList("greetings");
        int greeting_window                = plugin.getConfig().getInt("greeting_window", 20);
        String firstjoin_sound             = "Sound." + plugin.getConfig().getString("rewards.firstjoin.sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
        String join_sound                  = "Sound." + plugin.getConfig().getString("rewards.join.sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
        int firstjoin_volume               = plugin.getConfig().getInt("rewards.firstjoin.sound.volume", 10);
        int join_volume                    = plugin.getConfig().getInt("rewards.join.sound.volume", 10);
        int firstjoin_pitch                = plugin.getConfig().getInt("rewards.firstjoin.sound.pitch", 1);
        int join_pitch                     = plugin.getConfig().getInt("rewards.join.sound.pitch", 1);
        List<String> firstJoinGreetRewards = plugin.getConfig().getStringList("rewards.firstjoin.commands");
        List<String> joinGreetRewards      = plugin.getConfig().getStringList("rewards.join.commands");
        int reward_cooldown                = plugin.getConfig().getInt("reward_cooldown", 8);

        // Checks if message counts as a greeting
        boolean isGreeting = regexPatterns.stream().anyMatch(pattern -> {
            Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = compiledPattern.matcher(e.getMessage());
            return matcher.find();
        });

        // Debug stuff
        plugin.getLogger().log(Level.FINE, p.getDisplayName() + " sent a message!");
        plugin.getLogger().log(Level.FINE, "Was it a greeting? " + isGreeting);
        plugin.getLogger().log(Level.FINE, "Who joined last? " + (latestJoinPlayer.getDisplayName()));
        plugin.getLogger().log(Level.FINE, "When was it sent? " + now);
        plugin.getLogger().log(Level.FINE, "How long is that after last join? " + ((now - latestJoinTimestamp) / 1000L));
        plugin.getLogger().log(Level.FINE, "When did they last welcome " + (latestJoinPlayer.getDisplayName()) + "? " + lastGreetingTimestamp);
        plugin.getLogger().log(Level.FINE, "How long ago did they last welcome " + (latestJoinPlayer.getDisplayName()) + "? " + ((lastGreetingTimestamp == 0 ? 0 : (now - lastGreetingTimestamp) / 60 / 60 / 1000L)));

        // If message is valid greeting and sender isn't incoming player and reward window hasn't passed.
        // lastGreetingTimestamp == 0 means that player has not greeted the other yet, therefore it succeeds
        // lastGreetingTimestamp != 0 means that player has greeted the other, which means it's up to the comparison
        if (isGreeting && p != latestJoinPlayer && now - latestJoinTimestamp < greeting_window * 1000L && (lastGreetingTimestamp == 0 || now - lastGreetingTimestamp >= reward_cooldown * 60 * 60 * 1000L)) {
            plugin.getLogger().log(Level.INFO, p.getDisplayName() + " was rewarded for welcoming " + latestJoinPlayer.getDisplayName() + ".");
            if (latestJoinIsFirstJoin && !firstJoinGreetRewards.isEmpty()) {
                handleGreetSuccess(firstJoinGreetRewards, p, latestJoinPlayer);
                p.playSound(p.getLocation(), firstjoin_sound, firstjoin_volume, firstjoin_pitch);
            } else if (!latestJoinIsFirstJoin && !joinGreetRewards.isEmpty()) {
                handleGreetSuccess(joinGreetRewards, p, latestJoinPlayer);
                p.playSound(p.getLocation(), join_sound, join_volume, join_pitch);
            }
        }
    }

    // TODO: GreetingHandler class containing the remaining code after if isGreeting statement
    private void handleGreetSuccess(List<String> rewardList, Player p, Player latestJoinPlayer) {
        plugin.getDatabase().setTimestamp(p, latestJoinPlayer, System.currentTimeMillis());
        for (String reward : rewardList) {
            String command = reward.replace("%player%", p.getDisplayName());
            getScheduler().runTask(plugin, () -> getServer().dispatchCommand(getConsoleSender(), command));
        }
    }
}
