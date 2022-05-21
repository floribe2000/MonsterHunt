package com.matejdro.bukkit.monsterhunt;

import java.util.ArrayList;

import de.geistlande.monsterhunt.Settings;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;


public class Util {
    public static Permission permission = null;

    public static void Message(String message, CommandSender sender) {
        if (sender instanceof Player) {
            Message(message, (Player) sender);
        } else {
            sender.sendMessage(message);
        }
    }

    public static void Message(String message, Player player) {
        message = message.replaceAll("\\&([0-9abcdef])", "§$1");

        String color = "f";
        final int maxLength = 61; //Max length of chat text message
        final String newLine = "[NEWLINE]";
        ArrayList<String> chat = new ArrayList<String>();
        chat.add(0, "");
        String[] words = message.split(" ");
        int lineNumber = 0;
        for (String word : words) {
            if (chat.get(lineNumber).length() + word.length() < maxLength && !word.equals(newLine)) {
                chat.set(lineNumber, chat.get(lineNumber) + (chat.get(lineNumber).length() > 0 ? " " : "§" + color) + word);

                if (word.contains("§")) color = Character.toString(word.charAt(word.indexOf("§") + 1));
            } else {
                lineNumber++;
                if (!word.equals(newLine)) {
                    chat.add(lineNumber, "§" + color + word);
                } else
                    chat.add(lineNumber, "");
            }
        }
        for (String s : chat) {
            player.sendMessage(s);
        }
    }

    public static void Broadcast(String message) {
        for (Player i : MonsterHunt.instance.getServer().getOnlinePlayers()) {
            Message(message, i);
        }

    }

    public static void Debug(String message) {
        if (Settings.INSTANCE.getConfig().getDebug())
            MonsterHunt.log.info("[MonsterHunt][Debug]" + message);
    }

    public void StartFailed(MonsterHuntWorld world) {
        if (world.worldSettings.getSkipToIfFailsToStart() >= 0) {
            world.getWorld().setTime(world.worldSettings.getSkipToIfFailsToStart());
        }
    }

    public static Boolean permission(Player player, String line, PermissionDefault def) {
        Plugin plugin = MonsterHunt.instance.getServer().getPluginManager().getPlugin("Vault");
        if (plugin != null && setupPermissions())
            return permission.has(player, line);
        else
            return player.hasPermission(new org.bukkit.permissions.Permission(line, def));

    }

    private static Boolean setupPermissions() {
        if (permission != null) return true;

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

}
