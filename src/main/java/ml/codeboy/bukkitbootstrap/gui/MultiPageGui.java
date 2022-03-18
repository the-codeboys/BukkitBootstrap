package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MultiPageGui {

    private final int sizePerPage;
    private final Consumer<Gui> onPageCreation;
    private final ArrayList<Gui> pages = new ArrayList<>();
    private int size;

    public MultiPageGui() {
        this(54);
    }

    public MultiPageGui(int sizePerPage) {
        this(sizePerPage, null);
    }

    public MultiPageGui(int sizePerPage, Consumer<Gui> onPageCreation) {
        this.sizePerPage = Math.min(sizePerPage, 54);
        this.onPageCreation = onPageCreation;
        initialize();
    }

    /**
     * @deprecated use {@link MultiPageGui#MultiPageGui()}
     */
    @Deprecated
    public MultiPageGui(Plugin plugin, int size, String title) {
        this();
    }

    /**
     * @deprecated use {@link MultiPageGui#MultiPageGui(int, Consumer)}
     */
    @Deprecated
    public MultiPageGui(Plugin plugin, int sizePerPage, String title, Consumer<Gui> onPageCreation) {
        this(sizePerPage, onPageCreation);
    }

    /**
     * @deprecated use {@link MultiPageGui#MultiPageGui(int, Consumer)}
     */
    public MultiPageGui(Plugin plugin, int size, int sizePerPage, String title, Consumer<Gui> onPageCreation) {
        this(sizePerPage, onPageCreation);
    }

    public static ItemStack createItem(Material type, String name, boolean glow, String... lore) {
        return Gui.createItem(type, name, glow, lore);
    }

    public static ItemStack createItem(Material type, String name, String... lore) {
        return createItem(type, name, false, lore);
    }

    private void initialize() {
        int sizeTmp = this.size;
        if (sizeTmp == 0)//I'm not sure if the value is ever not 0 but if it is the Gui does not work
            sizeTmp = 1;
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

    protected ItemStack getPreviousButton() {
        return createItem(Material.ARROW, "prev page");
    }

    protected ItemStack getNextButton() {
        return createItem(Material.ARROW, "next page");
    }

    protected String getPageName(int id) {
        return "page " + id;
    }

    public void addItem(ItemStack item, AdvancedAction action) {
        addItem(item, (Action) action);
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
                } else {
                    page.addItem(item, action);
                }
                return;
            }
        }
    }

    protected ArrayList<Gui> getPages() {
        return pages;
    }

    private int getId(Inventory inventory) {
        for (int i = 0; i < pages.size(); i++) {
            if (inventory.equals(pages.get(i).getInventory()))
                return i;
        }
        return -1;
    }

    public int getOpenPage(Player player) {
        ArrayList<Gui> guis = getPages();
        for (int i = 0; i < guis.size(); i++) {
            Gui gui = guis.get(i);
            if (gui.getInventory().getViewers().contains(player)) {
                return i;
            }
        }
        return -1;
    }

    public MultiPageGui open(Player player, int index) {
        if (index < 0 || index >= getPages().size())
            throw new IllegalArgumentException("This page does not exist");
        player.openInventory(pages.get(index).getInventory());
        return this;
    }

    public MultiPageGui open(Player player) {
        return this.open(player, 0);
    }
}
