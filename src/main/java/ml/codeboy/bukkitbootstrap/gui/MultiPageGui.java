package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class MultiPageGui extends Gui {

    private final int sizePerPage;
    private final int size;
    private int currentPage=0;

    private final ArrayList<Gui> pages = new ArrayList<>();

    public MultiPageGui(Plugin plugin, int size, String title) {
        this(plugin, size, 54, title);
    }

    public MultiPageGui(Plugin plugin, int size, int sizePerPage, String title) {
        super(plugin, Math.min(sizePerPage, 54), title);
        this.sizePerPage = Math.min(sizePerPage, 54);
        this.size = size;
        initialize();
    }

    private void initialize() {
        int sizeTmp = this.size;
        int index = 0;
        while (sizeTmp > 0) {
            Gui page = new Gui(this.sizePerPage, getPageName(pages.size()));
            if (index != 0) {
                page.addItem(getPreviousButton(),this.sizePerPage-9, player -> {
                    player.closeInventory();
                    this.currentPage--;
                    this.open(player);
                });
                sizeTmp++;
            }
            if (sizeTmp > sizePerPage) {
                page.addItem(getNextButton(),this.sizePerPage-1, player -> {
                    player.closeInventory();
                    this.currentPage++;
                    this.open(player);
                });
            }
            sizeTmp -= sizePerPage;
            pages.add(page);
            index++;
        }

        System.out.println("NumberOfPages"+pages.size());
    }

    private ItemStack getPreviousButton() {
        return createItem(Material.ARROW, "[DEBUG] prev page");
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

    @Override
    public void addItem(ItemStack item, Action action) {
        for(Gui page:pages){
            if(page.hasFreeSlot()){
                page.addItem(item,action);
                return;
            }
        }
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
        for (int i = 0; i < pages.size(); i++) {
            if (inventory.equals(pages.get(i).getInventory()))
                return i;
        }
        return -1;
    }

    @Override
    public Gui open(Player player){
        player.openInventory(pages.get(currentPage).getInventory());
        return this;
    }

}
