package studio.potatocraft.quickshopban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.command.CommandProcesser;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.util.MsgUtil;

import java.util.List;
import java.util.Optional;

public class UnbanCommand implements CommandProcesser {
    private final QuickShopBan plugin;
    public UnbanCommand(QuickShopBan plugin){
        this.plugin = plugin;
    }
    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
        if(strings.length < 1){
            MsgUtil.sendMessage(commandSender,MsgUtil.getMessage("wrong-args", commandSender));
            return;
        }
        if(!(commandSender instanceof LivingEntity)){
            commandSender.sendMessage("Only player can execute this command.");
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);
        if(!player.hasPlayedBefore()){
            MsgUtil.sendMessage(commandSender,plugin.getConfig().getString("lang.player-not-exists"));
            return;
        }

        final BlockIterator bIt = new BlockIterator((LivingEntity) commandSender, 10);

        if (!bIt.hasNext()) {
            MsgUtil.sendMessage(commandSender, MsgUtil.getMessage("not-looking-at-shop", commandSender));
            return;
        }


        while (bIt.hasNext()) {
            Block b = bIt.next();
            Optional<Shop> shop = QuickShopAPI.getShopAPI().getShop(b.getLocation());
            if (shop.isPresent()) {
                Shop qshop = shop.get();
                ConfigurationSection extra = qshop.getExtra(plugin);
                List<String> bannedPlayers = extra.getStringList("bannedplayers");
                bannedPlayers.remove(player.getUniqueId().toString());
                qshop.setExtra(plugin,extra);
                MsgUtil.sendMessage(commandSender,MsgUtil.fillArgs(plugin.getConfig().getString("lang.unban-success"), player.getName()));
                return;
            }
        }
        MsgUtil.sendMessage(commandSender, MsgUtil.getMessage("not-looking-at-shop", commandSender));

    }
}
