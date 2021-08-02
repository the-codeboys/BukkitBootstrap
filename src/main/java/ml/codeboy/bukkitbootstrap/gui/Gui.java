package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;

public class Gui implements InventoryHolder, Listener {

    protected final HashMap<Integer, Action> actions = new HashMap<>();
    private final Inventory inventory;
    private final Plugin plugin;

    public Gui(Plugin plugin, int size, String title) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ItemStack createItem(Material type, String name, String... lore) {
        return createItem(type, name, false, lore);
    }

    public static ItemStack createItem(Material type, String name, boolean glow, String... lore) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        if (glow) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this)
            return;
        event.setCancelled(true);
        Action action = actions.getOrDefault(event.getRawSlot(), Action.none);
        action.execute((Player) event.getWhoClicked());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() == this)
            event.setCancelled(true);
    }

    public void addItem(ItemStack item, Action action) {
        addItem(item, getInventory().firstEmpty(), action);
    }

    public void addItem(ItemStack item, int index, Action action) {
        inventory.setItem(index, item);
        actions.put(index, action);
    }

    public Gui open(Player player) {
        player.openInventory(getInventory());
        return this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected Plugin getPlugin() {
        return plugin;
    }
}
