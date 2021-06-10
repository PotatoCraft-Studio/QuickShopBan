package studio.potatocraft.quickshopban;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.command.CommandContainer;
import org.maxgamer.quickshop.event.ShopPurchaseEvent;
import org.maxgamer.quickshop.shop.Shop;
import org.maxgamer.quickshop.util.MsgUtil;

import java.util.List;

public final class QuickShopBan extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // Plugin startup logic
        QuickShopAPI.getCommandManager().registerCmd(CommandContainer.builder()
                .executor(new BanCommand(this))
                .description(getConfig().getString("lang.ban-desc"))
                .permission("quickshop.ban")
                .hidden(false)
                .prefix("ban")
                .build());
        QuickShopAPI.getCommandManager().registerCmd(CommandContainer.builder()
                .executor(new UnbanCommand(this))
                .description(getConfig().getString("lang.unban-desc"))
                .permission("quickshop.unban")
                .hidden(false)
                .prefix("unban")
                .build());
        Bukkit.getPluginManager().registerEvents(this,this);
    }
    @EventHandler(ignoreCancelled = true)
    public void onShopPurchaseEvent(ShopPurchaseEvent event){
        Shop shop = event.getShop();
        ConfigurationSection extra = shop.getExtra(this);
        List<String> bannedPlayers = extra.getStringList("bannedplayers");
        if(bannedPlayers.contains(event.getPurchaser().toString())){
            MsgUtil.sendMessage(event.getPlayer(),getConfig().getString("lang.banned"));
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
