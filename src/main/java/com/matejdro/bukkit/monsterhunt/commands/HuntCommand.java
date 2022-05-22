package com.matejdro.bukkit.monsterhunt.commands;

import de.geistlande.monsterhunt.Localizer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Setting;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntCommand extends BaseCommand {

    public HuntCommand() {
        needPlayer = true;
        permission = "monsterhunt.usercmd.hunt";
        adminCommand = false;
    }


    public Boolean run(CommandSender sender, String[] args) {
        MonsterHuntWorld world = HuntWorldManager.getWorld(((Player) sender).getWorld().getName());
        if (world == null || world.getWorld() == null) return true;
        if (world.Score.containsKey(((Player) sender).getName())) {
            Util.Message(Localizer.INSTANCE.getString("personal.signup.error.duplicate"), sender);
            return true;
        }

        if (world.state < 2) {
            if (world.worldSettings.getAnnounceSignup()) {
                String message = Localizer.INSTANCE.getString("hunt.signup.playerAdded", sender.getName(), world.name);
                Util.Broadcast(message);
            } else {
                String message = Localizer.INSTANCE.getString("personal.signup.beforeStart", world.name);
                Util.Message(message, sender);
            }

            world.Score.put(sender.getName(), 0);

        } else if (world.state == 2 && (world.getSignUpPeriodTime() == 0 || world.worldSettings.getAllowSignupAfterStart())) {
            if (world.worldSettings.getAnnounceSignup()) {
                String message = Localizer.INSTANCE.getString("hunt.signup.playerAdded", sender.getName(), world.name);
                Util.Broadcast(message);
            } else {
                String message = Localizer.INSTANCE.getString("personal.signup.afterStart", world.name);
                Util.Message(message, sender);
            }

            world.Score.put(sender.getName(), 0);
        } else {
            Util.Message(Localizer.INSTANCE.getString("personal.signup.error.late"), sender);
        }
        return true;
    }

}
