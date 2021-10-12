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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;

public class Gui implements InventoryHolder, Listener {

    protected final HashMap<Integer, Action> actions = new HashMap<>();
    private final Inventory inventory;
    private final Plugin plugin;

    public Gui(int size, String title) {
        this(JavaPlugin.getProvidingPlugin(Gui.class), size, title);
    }

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
        if (event.getInventory().getHolder() != this || event.getInventory() == event.getView().getBottomInventory())
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

    public void addItemLast(ItemStack item, Action action) {
        int index = getInventory().getSize() - 1;
        while (getInventory().getItem(index) != null) {
            index--;
        }
        addItem(item, index, action);
    }

    public boolean hasFreeSlot() {
        return getFreeSlot() != -1;
    }


    public int getFreeSlot() {
        return getInventory().firstEmpty();
    }

    public void addItem(ItemStack item, int index, Action action) {
        inventory.setItem(index, item);
        actions.put(index, action);
    }

    /**
     * @param slot the slot of the item to remove
     * @return true if the slot was associated with an action. false if not.
     * This deos not mean there wasn´t an item there it could just mean that the action was null
     */
    public boolean removeItem(int slot){
        getInventory().setItem(slot,null);
        return actions.remove(slot)!=null;
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
