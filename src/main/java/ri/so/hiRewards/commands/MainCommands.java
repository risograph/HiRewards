package ri.so.hiRewards.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import ri.so.hiRewards.HiRewards;

import java.util.ArrayList;
import java.util.List;

public class MainCommands implements CommandExecutor, TabExecutor {

    final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("HiRewards");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equals("about")) {
            sender.sendMessage("HiRewards by riso");
            sender.sendMessage("Rewards your players for greeting new members!");
            sender.sendMessage(plugin.getConfig().getString("about_emoji"));
            return true;
        }

        if (args.length == 1 && args[0].equals("reload")) {
            if (!sender.hasPermission("hirewards.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do this!");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage("Reloaded config!");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "/hirewards reload " + ChatColor.RESET + "- Reloads the config from config.yml.");
        sender.sendMessage(ChatColor.YELLOW + "/hirewards about "  + ChatColor.RESET + "- Displays info about this plugin and its creator!.");
        sender.sendMessage(ChatColor.YELLOW + "/hirewards help "   + ChatColor.RESET + "- Displays this message.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> validArguments = new ArrayList<>();
        // autocomplete
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("reload", "about", "help"), validArguments);
            return validArguments;
        }
        return List.of();
    }
}
