package com.matejdro.bukkit.monsterhunt.commands;


import de.geistlande.monsterhunt.db.DbManager;
import kotlin.Pair;
import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.monsterhunt.Util;
import org.bukkit.entity.Player;

public class HuntScoreCommand extends BaseCommand {

    public HuntScoreCommand() {
        needPlayer = true;
        permission = "monsterhunt.usercmd.huntscore";
        adminCommand = false;
    }


    public Boolean run(CommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equals("rank") && sender instanceof Player player) {
            int rank = DbManager.INSTANCE.getPlayerHighscore(player.getUniqueId());
            if (rank >= 0)
                Util.Message("Your current high score rank is " + rank, sender);
            else
                Util.Message("You do not have your high score yet.", sender);
        } else if (args.length > 0 && args[0].equals("top")) {
            int number = 5;
            if (args.length > 1) number = Integer.parseInt(args[1]);

            var scores = DbManager.INSTANCE.getHighScores(number);
            int counter = 0;
            Util.Message("Top high scores:", sender);
            for (Pair<String, Integer> playerScore : scores) {
                counter++;
                Util.Message(counter + ". &6" + playerScore.component1() + "&f - &a" + playerScore.component2() + "&f points", sender);
            }
        } else if (args.length > 0) {
            var score = DbManager.INSTANCE.getPlayerHighscore(args[0]);
            if (score >= 0) {
                Util.Message("High score of player &6" + args[0] + "&f is &6" + score + "&f points.", sender);
            } else {
                Util.Message("Player &6" + args[0] + "&f do not have high score yet.", sender);
            }
        } else {
            if (sender instanceof Player player) {
                var score = DbManager.INSTANCE.getPlayerHighscore(player.getUniqueId());
                if (score >= 0) {
                    Util.Message("Your high score is &6" + score + "&f points.", sender);
                } else {
                    Util.Message("You do not have your high score yet.", sender);
                }
            }
        }
        return true;
    }

}
