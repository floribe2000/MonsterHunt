package com.matejdro.bukkit.monsterhunt.commands;

import de.geistlande.monsterhunt.Localizer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.HuntZone;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Setting;
import com.matejdro.bukkit.monsterhunt.SettingsOld;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntTeleCommand extends BaseCommand {

    public HuntTeleCommand() {
        needPlayer = true;
        permission = "monsterhunt.usercmd.hunttele";
        adminCommand = false;
    }


    public Boolean run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());
        if (!SettingsOld.globals.getBoolean(Setting.HuntZoneMode.getString(), false) || world == null || world.getWorld() == null)
            return false;

        boolean permission = !Util.permission(player, "monsterhunt.noteleportrestrictions", PermissionDefault.OP);

        if (world.state == 0 && permission) {
            Util.Message(Localizer.INSTANCE.getString("hunt.zone.tp.noHunt"), player);
            return true;
        } else if (world.Score.containsKey(player.getUniqueId()) && world.worldSettings.getEnableSignup() && permission) {
            Util.Message(Localizer.INSTANCE.getString("hunt.zone.tp.noSignup"), player);
            return true;
        }

        world.tpLocations.put(player, player.getLocation());
        player.teleport(HuntZone.teleport);
        return true;
    }

}
