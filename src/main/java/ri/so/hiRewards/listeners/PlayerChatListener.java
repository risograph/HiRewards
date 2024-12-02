package ri.so.hiRewards.listeners;

import org.bukkit.entity.Player;
import ri.so.hiRewards.HiRewards;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ri.so.hiRewards.utils.GreetingLogger;
import ri.so.hiRewards.utils.GreetingRetriever;

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
        List<String> regexPatterns = plugin.getConfig().getStringList("greetings");
        List<String> joinGreetRewards = plugin.getConfig().getStringList("rewards.join");
        List<String> firstJoinGreetRewards = plugin.getConfig().getStringList("rewards.firstjoin");

        long lastGreetingTimestamp = GreetingRetriever.getLastGreetingTimestamp(
                p.getUniqueId().toString(),
                latestJoinPlayer.getUniqueId().toString()
        );

        // Returns true if message matches the regex from config.yml
        boolean isGreeting = regexPatterns.stream().anyMatch(pattern -> {
            Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = compiledPattern.matcher(e.getMessage());
            return matcher.find();
        });

        // If message is valid greeting and sender isn't incoming player and reward window hasn't passed.
        // lastGreetingTimestamp == 0 means that player has not greeted the other yet, therefore it succeeds
        // lastGreetingTimestamp != 0 means that player has greeted the other, which means it's up to the comparison
        if (isGreeting && p != latestJoinPlayer && now - latestJoinTimestamp < greeting_window * 1000L && (lastGreetingTimestamp == 0 || now - lastGreetingTimestamp >= 8 * 60 * 60 * 1000L)) {
            log.info(p.getDisplayName() + " was rewarded for welcoming " + latestJoinPlayer.getDisplayName() + ".");
            if (latestJoinIsFirstJoin && !firstJoinGreetRewards.isEmpty()) {
                handleGreetSuccess(firstJoinGreetRewards, p, latestJoinPlayer);
            } else if (!latestJoinIsFirstJoin && !joinGreetRewards.isEmpty()) {
                handleGreetSuccess(joinGreetRewards, p, latestJoinPlayer);
            }
        }
    }

    private void handleGreetSuccess(List<String> rewardList, Player p, Player latestJoinPlayer) {
        for (String reward : rewardList) {
            String command = reward.replace("%player%", p.getDisplayName());
            getScheduler().runTask(plugin, () -> getServer().dispatchCommand(getConsoleSender(), command));
        }
        // Log timestamp for greeting in sqlite db
        GreetingLogger.logGreeting(
                p.getUniqueId().toString(),
                latestJoinPlayer.getUniqueId().toString(),
                System.currentTimeMillis()
        );
    }
}
