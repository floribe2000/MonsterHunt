package com.matejdro.bukkit.monsterhunt;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.random.RandomGenerator;

import de.geistlande.monsterhunt.Localizer;
import de.geistlande.monsterhunt.RewardElement;
import de.geistlande.monsterhunt.RewardGroup;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class RewardManager {

    private static MonsterHunt plugin = MonsterHunt.instance;

    public static Economy economy = null;

    public static void rewardWinners(MonsterHuntWorld world) {

        HashMap<String, Integer>[] Winners = getWinners(world);
        if (Winners[0].size() < 1) {

            String message = Localizer.INSTANCE.getString("hunt.finish.notEnoughPlayers", world.name);
            Util.Broadcast(message);
            return;
        }
        int num = world.worldSettings.getRewardSettings().getNumberOfWinners();

        int score = Winners[0].get(Winners[0].keySet().toArray()[0]);
        if (score < world.worldSettings.getRewardSettings().getMinimumPointsPlace()) {
            String message = world.worldSettings.getString(Setting.FinishMessageNotEnoughPoints);
            message = message.replace("<World>", world.name);
            Util.Broadcast(message);
            return;

        }
        String RewardString;

        //Normal reward
        if (world.worldSettings.getRewardSettings().getEnabled()) {
            for (int place = 0; place < num; place++) {
                if (Winners[place].size() < 1) continue;
                score = Winners[place].get(Winners[place].keySet().toArray()[0]);
                Util.Debug(String.valueOf(score));
                Util.Debug(String.valueOf(world.worldSettings.getPlaceInt(Setting.MinimumPointsPlace, place + 1)));
                if (score < world.worldSettings.getPlaceInt(Setting.MinimumPointsPlace, place + 1)) Winners[place].clear();
                for (String i : Winners[place].keySet()) {
                    RewardString = world.worldSettings.getPlaceString(Setting.RewardParametersPlace, place + 1);
                    if (RewardString.contains(";"))
                        RewardString = PickRandom(RewardString);
                    reward(i, RewardString, world, score);
                }
            }
        }

        //RewardEveryone
        if (!(!world.worldSettings.getRewardSettings().getEnableRewardEveryonePermission() && !world.worldSettings.getRewardSettings().getRewardEveryone())) {
            for (Entry i : world.Score.entrySet()) {
                if (((Integer) i.getValue()) < world.worldSettings.getRewardSettings().getMinimumPointsEveryone()) continue;
                Player player = plugin.getServer().getPlayer((String) i.getKey());
                if (player == null) continue;
                RewardString = world.worldSettings.getRewardSettings().getRewardParametersEveryone();
                if (RewardString.contains(";"))
                    RewardString = PickRandom(RewardString);
                if (world.worldSettings.getRewardSettings().getRewardEveryone() || (Util.permission(player, "monsterhunt.rewardeverytime", PermissionDefault.FALSE) && world.worldSettings.getRewardSettings().getEnableRewardEveryonePermission())) {
                    reward((String) i.getKey(), RewardString, world, (Integer) i.getValue());
                }
            }
        }

        //Broadcast winner message
        Util.Debug("[MonterHunt][DEBUG - NEVEREND]Broadcasting Winners");
        String message;



        message = Localizer.INSTANCE.getString("hunt.finish.winners", world.name);

        var messageParams = new ArrayList<>();

        for (int place = 0; place < num; place++) {
            String players = "";
            if (Winners[place].size() < 1) {
                players = "Nobody";
                score = 0;
            } else {
                score = Winners[place].get(Winners[place].keySet().toArray()[0]);
                for (String i : Winners[place].keySet()) {
                    players += i + ", ";
                }
                players = players.substring(0, players.length() - 2);
            }

            message = message.replace("<NamesPlace" + (place + 1) + ">", players);
            message = message.replace("<PointsPlace" + (place + 1) + ">", String.valueOf(score));


        }
        Util.Broadcast(message);
    }

    private static void reward(String playerstring, MonsterHuntWorld world, int score) {
        List<RewardGroup> allRewardGroups = world.worldSettings.getRewardSettings().getAvailableRewards();



        //String[] split = rewardString.split(",");
        Player player = plugin.getServer().getPlayer(playerstring);
        if (player == null) return;
        String items = "";
        for (RewardGroup rewardGroup : allRewardGroups) {
            Util.Debug(rewardGroup.getName()); //TODO hoffen, dass es im log sinn macht,

            //////

            //Parse block ID
            //String BlockIdString = i2.substring(0, i2.indexOf(" "));



            //short data;

            //one from each revardGroup
            // one item in the revardGroup at random

            List<RewardElement> rewardGroupItemList = rewardGroup.getItems();

            //TODO randmon funktion bauen.

            Random random = new Random();

            Material blockId = rewardGroup;

            int all = rewardGroupItemList.size();

                for (int i = 0; i < all; ++i) {
                    int weight = rewardGroupItemList.get(i).getStochasticWeight()

                    if (weight >0){

                    }
                }
            if (BlockIdString.contains(":")) {
                blockId = Integer.valueOf(BlockIdString.substring(0, rewardGroup.indexOf(":")));
                data = Short.valueOf(BlockIdString.substring(rewardGroup.indexOf(":") + 1));
            } else {
                blockId = Integer.valueOf(BlockIdString);
                data = 0;
            }


            //Parse block amount
            String rv = rewardGroup.substring(rewardGroup.indexOf(" ") + 1);
            boolean RelativeReward = false;
            if (rv.startsWith("R")) {
                RelativeReward = true;
                rv = rv.substring(1);
            }
            int StartValue, EndValue;
            if (rv.contains("-")) {
                StartValue = (int) Math.round(Double.parseDouble(rv.substring(0, rv.indexOf("-"))) * 100.0);
                EndValue = (int) Math.round(Double.parseDouble(rv.substring(rv.indexOf("-") + 1)) * 100.0);
            } else {
                StartValue = (int) Math.round(Double.parseDouble(rv) * 100.0);
                EndValue = StartValue;

            }
            int random;
            if (EndValue == StartValue)
                random = EndValue;
            else {
                random = new Random().nextInt(EndValue - StartValue) + StartValue;
            }
            double number = random / 100.0;
            if (RelativeReward)
                number *= score;
            int amount = (int) Math.round(number);

            //give reward
            if (blockId == 0) {
                String item = iConomyReward(playerstring, amount);
                if (amount > 0) items += item + ", ";

            } else {
                addItemFix(player, blockId, amount, data);
                if (amount > 0)
                    items += String.valueOf(amount) + "x " + getMaterialName(Material.getMaterial(blockId)) + ", ";
                //plugin.getServer().getPlayer(i).giveItem(BlockId,amount);
            }
        }
        if (items.trim() == "") return;
        items = items.substring(0, items.length() - 2);
        String message = Localizer.INSTANCE.getString("personal.rewards.received", items);
        Util.Message(message, player);
    }

    private static String iConomyReward(String player, int number) {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Vault");
        if (test != null) {
            if (!setupEconomy()) {
                MonsterHunt.log.log(Level.WARNING, "[MonsterHunt] You have economy rewards enabled, but don't have any economy plugin installed!");
                return "";
            }
            economy.depositPlayer(player, number);
            return economy.format(number);
        } else {
            MonsterHunt.log.log(Level.WARNING, "[MonsterHunt] You have economy rewards enabled, but don't have Vault plugin installed! Some players may not get their reward! See http://dev.bukkit.org/server-mods/vault/");
            return "";
        }
    }

    private static String PickRandom(String RewardString) {
        String[] split = RewardString.split(";");
        int[] chances = new int[split.length];

        int totalchances = 0, numnochances = 0;
        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith(":")) {
                chances[i] = Integer.parseInt(split[i].substring(1, split[i].indexOf(" ")));
                split[i] = split[i].substring(split[i].indexOf(" ") + 1);
                totalchances += chances[i];
            } else {
                chances[i] = -1;
                numnochances++;
            }

        }

        if (totalchances > (100 - numnochances)) {
            plugin.log.warning("[MonsterHunt]Invalid Rewards configuration! Sum of all percentages should be exactly 100! MonsterHunt will now throw error and disable itself.");
            plugin.getPluginLoader().disablePlugin(plugin);
            return null;
        }

        if (numnochances > 0) {
            int averagechance = (100 - totalchances) / numnochances;
            for (int i = 0; i < chances.length; i++) {
                chances[i] = averagechance;
            }
        }

        int total = 0;

        for (int i = 0; i < split.length; i++) {
            total += chances[i];
            chances[i] = total;
        }

        int random = new Random().nextInt(100);
        for (int i = 0; i < split.length; i++) {
            if (random < chances[i] && (i < 1 || random >= chances[i - 1])) return split[i];
        }
        return "";

    }

    private static HashMap<String, Integer>[] getWinners(MonsterHuntWorld world) {
        HashMap<String, Integer> scores = new HashMap<>(world.Score);
        int num = world.worldSettings.getRewardSettings().getNumberOfWinners();
        HashMap<String, Integer>[] winners = new HashMap[num];
        for (int place = 0; place < num; place++) {
            winners[place] = new HashMap<>();
            int tmp = 0;
            for (String i : scores.keySet()) {
                int value = scores.get(i);
                if (value > tmp) {
                    winners[place].clear();
                    winners[place].put(i, value);
                    tmp = value;
                } else if (value == tmp) {
                    winners[place].put(i, value);
                }
            }

            for (String i : winners[place].keySet()) {
                scores.remove(i);
            }
        }

        return winners;
    }

    // Material name snippet by TechGuard
    private static String getMaterialName(Material material) {
        String name = material.toString();
        name = name.replaceAll("_", " ");
        if (name.contains(" ")) {
            String[] split = name.split(" ");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].substring(0, 1).toUpperCase() + split[i].substring(1).toLowerCase();
            }
            name = "";
            for (String s : split) {
                name += " " + s;
            }
            name = name.substring(1);
        } else {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        return name;
    }

    //add item color by fabe
    private static void addItemFix(Player players, int ID, int amount, short dur) {
        if (players.getInventory().firstEmpty() == -1) {
            players.getLocation().getWorld().dropItem(players.getLocation(), new ItemStack(ID, amount, dur));
            return;
        }
        if (players.getInventory().contains(ID) && (ID == 35 || ID == 351)) { // Wool or Dye
            HashMap<Integer, ? extends ItemStack> invItems = players.getInventory()
                .all(ID);

            int restAmount = amount;
            for (Map.Entry<Integer, ? extends ItemStack> entry : invItems.entrySet()) {

                int index = entry.getKey();
                ItemStack item = entry.getValue();
                int stackAmount = item.getAmount();

                // e.g. same wool in inventory => put in to stack
                if (dur == item.getDurability()) {

                    if (stackAmount < 64) {
                        // Add to stack
                        int canGiveAmount = 64 - stackAmount;
                        int giveAmount;

                        if (canGiveAmount >= restAmount) {
                            giveAmount = restAmount;
                            restAmount = 0;
                        } else {
                            giveAmount = canGiveAmount;
                            restAmount = restAmount - giveAmount;
                        }

                        players.getInventory()
                            .setItem(
                                index,
                                new ItemStack(ID, stackAmount
                                    + giveAmount, dur));

                    }
                }
            }
            // If there is still a rest, add the rest to the inventory
            if (restAmount > 0) {
                int emptySlot = players.getInventory().firstEmpty();
                players.getInventory().setItem(emptySlot,
                    new ItemStack(ID, restAmount, dur));
            }
        } else {
            // Standard usage of addItem
            players.getInventory().addItem(new ItemStack(ID, amount, dur));
        }
    }

    private static Boolean setupEconomy() {
        if (economy != null) return true;

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

}
