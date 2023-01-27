package fr.HugoJumper.sync.listerner;

import fr.HugoJumper.sync.SyncInv.SyncInv;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final SyncInv syncInv;

    public PlayerListener(SyncInv syncInv) {
        this.syncInv = syncInv;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.syncInv.getDatabasemanager().isOnTable(player)){
            this.syncInv.getDatabasemanager().loadPlayer(player);
        }else{
            this.syncInv.getDatabasemanager().addPlayerToDatabase(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.syncInv.getDatabasemanager().savePlayer(player);
    }
}