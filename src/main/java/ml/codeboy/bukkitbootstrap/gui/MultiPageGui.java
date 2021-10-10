package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class MultiPageGui {

    private final int sizePerPage;
    private int size;

    private final Consumer<Gui> onPageCreation;

    private final ArrayList<Gui> pages = new ArrayList<>();

    public MultiPageGui(Plugin plugin, int size, String title) {
        this(plugin, size, 54, title, null);
    }

    public MultiPageGui(Plugin plugin, int sizePerPage, String title, Consumer<Gui> onPageCreation) {
        this(plugin, Math.min(sizePerPage, 54), Math.min(sizePerPage, 54), title, onPageCreation);
    }

    public MultiPageGui(Plugin plugin, int size, int sizePerPage, String title, Consumer<Gui> onPageCreation) {
        this.sizePerPage = Math.min(sizePerPage, 54);
        this.size = size;
        this.onPageCreation = onPageCreation;
        initialize();
    }

    private void initialize() {
        int sizeTmp = this.size;
        int index = 0;
        while (sizeTmp > 0) {
            Gui page = createPage();
            if (index != 0) {
                addPrevButton(page);
                sizeTmp++;
            }
            if (sizeTmp > sizePerPage) {
                addNextButton(page);
                sizeTmp++;
            }
            sizeTmp -= sizePerPage;
            pages.add(page);
            index++;
        }
    }

    protected Gui createPage() {
        Gui page = new Gui(this.sizePerPage, getPageName(pages.size()));
        if (onPageCreation != null) {
            onPageCreation.accept(page);
        }
        return page;
    }

    protected void addNextButton(Gui page) {
        page.addItem(getNextButton(), getNextButtonIndex(), player -> {
            player.closeInventory();
            this.open(player, pages.indexOf(page) + 1);
        });
    }

    protected void addPrevButton(Gui page) {
        page.addItem(getPreviousButton(), getPrevButtonIndex(), player -> {
            player.closeInventory();
            this.open(player, pages.indexOf(page) - 1);
        });
    }

    protected int getNextButtonIndex() {
        return this.sizePerPage - 1;
    }

    protected int getPrevButtonIndex() {
        return this.sizePerPage - 9;
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

    public void addItem(ItemStack item, Action action) {
        for (Gui page : pages) {
            if (page.hasFreeSlot()) {
                if (page.getFreeSlot() == getNextButtonIndex()) {
                    addNextButton(page);
                    Gui newPage = createPage();
                    addPrevButton(newPage);
                    pages.add(newPage);
                    size += sizePerPage;
                    newPage.addItem(item, action);
                }else {
                    page.addItem(item, action);
                }
                return;
            }
        }
    }

    private int getId(Inventory inventory) {
        for (int i = 0; i < pages.size(); i++) {
            if (inventory.equals(pages.get(i).getInventory()))
                return i;
        }
        return -1;
    }

    public MultiPageGui open(Player player, int index) {
        player.openInventory(pages.get(index).getInventory());
        return this;
    }

    public MultiPageGui open(Player player) {
        return this.open(player, 0);
    }

    public static ItemStack createItem(Material type, String name, boolean glow, String... lore) {
        return Gui.createItem(type, name, glow, lore);
    }

    public static ItemStack createItem(Material type, String name, String... lore) {
        return createItem(type, name, false, lore);
    }
}
