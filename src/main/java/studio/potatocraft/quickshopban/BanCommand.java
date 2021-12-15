package studio.potatocraft.quickshopban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.maxgamer.quickshop.api.command.CommandHandler;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.util.MsgUtil;

import java.util.List;

public class BanCommand implements CommandHandler<Player> {
    private final QuickShopBan plugin;
    public BanCommand (QuickShopBan plugin){
        this.plugin = plugin;
    }
    @Override
    public void onCommand(Player commandSender, String s, String[] strings) {
        if(strings.length < 1){
            plugin.getQuickShopAPI().getTextManager().of(commandSender,"wrong-args").send();
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(strings[0]);
        if(!player.hasPlayedBefore()){
            MsgUtil.sendDirectMessage(commandSender,plugin.getConfig().getString("lang.player-not-exists"));
            return;
        }

        final BlockIterator bIt = new BlockIterator(commandSender, 10);

        if (!bIt.hasNext()) {
            plugin.getQuickShopAPI().getTextManager().of(commandSender, "not-looking-at-shop").send();
            return;
        }

        while (bIt.hasNext()) {
            Block b = bIt.next();
           Shop shop = plugin.getQuickShopAPI().getShopManager().getShop(b.getLocation());
            if (shop!=null) {
                ConfigurationSection extra = shop.getExtra(plugin);
                List<String> bannedPlayers = extra.getStringList("bannedplayers");
                if(!bannedPlayers.contains(player.getUniqueId().toString())){
                    bannedPlayers.add(player.getUniqueId().toString());
                }
                shop.setExtra(plugin,extra);
                MsgUtil.sendDirectMessage(commandSender,MsgUtil.fillArgs(plugin.getConfig().getString("lang.ban-success"), player.getName()));
                return;
            }
        }
        plugin.getQuickShopAPI().getTextManager().of(commandSender, "not-looking-at-shop").send();
    }
}
