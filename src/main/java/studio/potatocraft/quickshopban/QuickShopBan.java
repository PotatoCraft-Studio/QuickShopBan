package studio.potatocraft.quickshopban;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.api.command.CommandContainer;
import org.maxgamer.quickshop.api.event.ShopPurchaseEvent;
import org.maxgamer.quickshop.api.shop.Shop;
import org.maxgamer.quickshop.util.MsgUtil;

import java.util.List;

public final class QuickShopBan extends JavaPlugin implements Listener {

    private QuickShopAPI quickShopAPI=null;

    public QuickShopAPI getQuickShopAPI() {
        return quickShopAPI;
    }

    @Override
    public void onEnable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("QuickShop");
        if(plugin != null){
            quickShopAPI= (QuickShopAPI)plugin;
        }
        // Plugin startup logic
        quickShopAPI.getCommandManager().registerCmd(CommandContainer.builder()
                .executor(new BanCommand(this))
                .description(getConfig().getString("lang.ban-desc"))
                .permission("quickshop.ban")
                .hidden(false)
                .prefix("ban")
                .build());
        quickShopAPI.getCommandManager().registerCmd(CommandContainer.builder()
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
            MsgUtil.sendDirectMessage(event.getPlayer(),getConfig().getString("lang.banned"));
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
