package fr.HugoJumper.sync.utils.command;

import fr.HugoJumper.sync.SyncInv.SyncInv;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class ACommand implements CommandExecutor {
    private final String commandName;

    private final String permission;

    private final boolean consoleCanExecute;

    private final SyncInv syncInv;

    public ACommand(SyncInv syncInv, String commandName, String permission, boolean consoleCanExecute) {
        this.permission = permission;
        this.commandName = commandName;
        this.consoleCanExecute = consoleCanExecute;
        this.syncInv = syncInv;
        syncInv.getCommand(commandName).setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getLabel().equalsIgnoreCase(this.commandName))
            return true;
        if (!this.consoleCanExecute && !(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage(this.syncInv.getConfigManager().getString("not-player"));
            return true;
        }
        if (!sender.hasPermission(this.permission)) {
            sender.sendMessage(this.syncInv.getConfigManager().getString("no-permission"));
            return true;
        }
        return execute(sender, args);
    }

    public abstract boolean execute(CommandSender paramCommandSender, String[] paramArrayOfString);
}
