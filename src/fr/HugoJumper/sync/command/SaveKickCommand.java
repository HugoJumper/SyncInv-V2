package fr.HugoJumper.sync.command;

import fr.HugoJumper.sync.SyncInv.SyncInv;
import fr.HugoJumper.sync.utils.command.ACommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SaveKickCommand extends ACommand {
    private final SyncInv syncInv;

    public SaveKickCommand(SyncInv syncInv) {
        super(syncInv, "saveandkick", "command.saveandkick", true);
        this.syncInv = syncInv;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/saveandkick <player>");
            return true;

        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(this.syncInv.getConfigManager().getString("Le joueur n'est pas connecté sur le serveur "));
                return true;
            }
            this.syncInv.getDatabasemanager().savePlayer(target);
            target.kickPlayer(this.syncInv.getConfigManager().getString("Raison du Kick :"));
        }
        return true;
    }
}