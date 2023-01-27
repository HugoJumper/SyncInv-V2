package fr.HugoJumper.sync.database.manager;

import fr.HugoJumper.sync.SyncInv.SyncInv;
import fr.HugoJumper.sync.utils.Base64Save;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DataBaseManager {
    private final SyncInv syncInv;

    public DataBaseManager(SyncInv syncInv) {
        this.syncInv = syncInv;
    }

    public void addPlayerToDatabase(Player player) {
        try {
            PreparedStatement preparedStatement = this.syncInv.getDb().prepareWrappedStatement("INSERT INTO `" + this.syncInv.getDb().getSyncTableName() + "` (`player_name`, `inventory`, `armor`, `enderchest`, `exp`, `life`, `feed`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");
            preparedStatement.setString(4, "");
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerTable(Player player, String inventory, String armor, String enderChest, int exp, int life, int feed) {
        try {
            PreparedStatement preparedStatement = this.syncInv.getDb().prepareWrappedStatement("UPDATE `" + this.syncInv.getDb().getSyncTableName() + "` SET inventory='" + inventory + "', armor='" + armor + "', enderchest='" + enderChest + "', exp=" + exp + ", life=" + life + ", feed=" + feed + " WHERE player_name='" + player
                    .getName() + "'");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPlayer(Player player) {
        try {
            PreparedStatement preparedStatement = this.syncInv.getDb().prepareWrappedStatement("SELECT * FROM " + this.syncInv.getDb().getSyncTableName() + " WHERE player_name='" + player.getName() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String inventoryFromBase = resultSet.getString("inventory");
                String armorFromBase = resultSet.getString("armor");
                String enderChestFromBase = resultSet.getString("enderchest");
                int exp = resultSet.getInt("exp");
                int life = resultSet.getInt("life");
                int feed = resultSet.getInt("feed");
                try {
                    ItemStack[] inventory = Base64Save.itemStackArrayFromBase64(inventoryFromBase);
                    ItemStack[] armor = Base64Save.itemStackArrayFromBase64(armorFromBase);
                    ItemStack[] enderChest = Base64Save.itemStackArrayFromBase64(enderChestFromBase);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
                    player.getEnderChest().clear();
                    player.getInventory().setContents(inventory);
                    player.getInventory().setArmorContents(armor);
                    player.getEnderChest().setContents(enderChest);
                    player.setTotalExperience(exp);
                    player.setHealthScale(life);
                    player.setFoodLevel(feed);
                    player.sendMessage(this.syncInv.getConfigManager().getString("title-name"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(Player player) {
        String baseStuff = Base64Save.itemStackArrayToBase64(player.getInventory().getContents());
        String baseArmor = Base64Save.itemStackArrayToBase64(player.getInventory().getArmorContents());
        String baseEnderChest = Base64Save.itemStackArrayToBase64(player.getEnderChest().getContents());
        int exp = player.getTotalExperience();
        int life = (int)player.getHealth();
        int feed = player.getFoodLevel();
        if (this.syncInv.getDatabasemanager().isOnTable(player)) {
            updatePlayerTable(player, baseStuff, baseArmor, baseEnderChest, exp, life, feed);
        } else {
            addPlayerToDatabase(player);
            updatePlayerTable(player, baseStuff, baseArmor, baseEnderChest, exp, life, feed);
        }
    }

    public boolean isOnTable(Player player) {
        try {
            PreparedStatement preparedStatement = this.syncInv.getDb().prepareWrappedStatement("SELECT * FROM " + this.syncInv.getDb().getSyncTableName() + " WHERE player_name='" + player.getName() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
