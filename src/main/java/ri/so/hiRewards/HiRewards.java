package ri.so.hiRewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Sound;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class HiRewards extends JavaPlugin implements Listener {

    private final Set<UUID> eligiblePlayers = new HashSet<>();
    private final Set<UUID> rewardedPlayers = new HashSet<>();
    private List<String> welcomePatterns;
    private List<String> rewardCommands;
    private Logger log;

    @Override
    public void onEnable() {
        log = this.getLogger();

        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        loadConfig();

        log.info("👋 HiRewards has been enabled.");
    }

    @Override
    public void onDisable() {
        log.info("❌ HiRewards has been disabled.");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        welcomePatterns = config.getStringList("words");
        rewardCommands = config.getStringList("rewards");

        // Fallbacks for config keys
        if (welcomePatterns == null || welcomePatterns.isEmpty()) {
            log.warning("No 'words' property found in config.yml. Disabling trigger words.");
            welcomePatterns = List.of("");
        }

        if (rewardCommands == null || rewardCommands.isEmpty()) {
            log.warning("No 'rewards' property found in config.yml. Disabling rewards.");
            rewardCommands = List.of("");
        }

        log.info("Config loaded successfully.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joinedPlayer = event.getPlayer();
        UUID joinedPlayerUUID = joinedPlayer.getUniqueId();

        // Clear the eligible players list when a new player joins
        eligiblePlayers.clear();

        // Add all online players to the eligible list (except the joined player)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getUniqueId().equals(joinedPlayerUUID)) {
                eligiblePlayers.add(player.getUniqueId());
            }
        }

        log.info("Welcoming period started for " + joinedPlayer.getName());

        // Start a 20-second timer
        new BukkitRunnable() {
            @Override
            public void run() {
                eligiblePlayers.clear();
                rewardedPlayers.clear();
                log.info("Welcoming period ended.");
            }
        }.runTaskLater(this, 20 * 20L); // 20 ticks/second * 20 seconds
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String message = event.getMessage();

        // Check if player is eligible and hasn't been rewarded
        if (eligiblePlayers.contains(playerUUID) && !rewardedPlayers.contains(playerUUID)) {
            // Match message against welcome patterns
            boolean isMatch = welcomePatterns.stream()
                    .anyMatch(pattern -> Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message).find());
            if (isMatch) {
                // Schedule the task to run on the main thread
                Bukkit.getScheduler().runTask(this, () -> {
                    for (String command : rewardCommands) {
                        String parsedCommand = command.replace("%player%", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
                    }
                });

                // Mark player as rewarded
                rewardedPlayers.add(playerUUID);
                // TODO: add to config, place after message.
                //player.sendMessage(ChatColor.GREEN + "[+10 XP]" + ChatColor.GRAY + " for welcoming a new player!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 29);
                log.info( player.getName() + " was rewarded for welcoming a player.");
            }
        }
    }
}
