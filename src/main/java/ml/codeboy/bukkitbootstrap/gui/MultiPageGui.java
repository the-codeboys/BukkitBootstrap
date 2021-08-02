package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class MultiPageGui extends Gui {// TODO: Finish MultiPageGui
    private final int sizePerPage;

    private final ArrayList<Inventory> pages = new ArrayList<>();

    public MultiPageGui(Plugin plugin, int sizePerPage, String title) {
        super(plugin, Math.min(sizePerPage, 54), title);
        this.sizePerPage = sizePerPage;
        createNextPage();
    }

    protected ItemStack getLastButton() {
        return createItem(Material.ARROW, "[DEBUG] last page");
    }

    protected ItemStack getNextButton() {
        return createItem(Material.ARROW, "[DEBUG] next page");
    }

    protected String getPageName(int id) {
        return "page " + id;
    }

    protected void createNextPage() {
        Inventory inventory = Bukkit.createInventory(this, sizePerPage, getPageName(pages.size()));

    }

    protected void addItem(ItemStack item, int page, int slot, Action action) {
        Inventory inventory = pages.get(page);
        inventory.setItem(slot, item);
        actions.put(slot + page * sizePerPage, action);
    }


    @EventHandler
    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this)
            return;
        event.setCancelled(true);
        Action action = actions.getOrDefault(event.getRawSlot() + getId(event.getInventory()) * sizePerPage, Action.none);
        action.execute((Player) event.getWhoClicked());
    }

    private int getId(Inventory inventory) {
        int id = -1;
        for (int i = 0; i < pages.size(); i++) {
            if (inventory.equals(pages.get(i)))
                return i;
        }
        return id;
    }

}
