package ml.codeboy.bukkitbootstrap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.management.InstanceAlreadyExistsException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class CustomItem {

    private static final ItemFlag[] flags = {ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE};

    private static final HashMap<String, CustomItem> customItems = new HashMap<>();


    public static CustomItem getItem(String name) {
        if (!customItems.containsKey(name)) {
            return null;
        }
        return new CustomItem(customItems.get(name));
    }

    public static CustomItem createItem(String name, Material material, short durability, Consumer<ItemStack> onCreation, String... lore) throws InstanceAlreadyExistsException {
        if (customItems.containsKey(name)) {
            throw new InstanceAlreadyExistsException("Item with this name already exists");
        }
        for (CustomItem customItem : customItems.values()) {
            if (customItem.material == material && customItem.durability == durability) {
                throw new InstanceAlreadyExistsException("Item with this name already exists");
            }
        }
        CustomItem item = new CustomItem(name, material, durability, onCreation, lore);
        customItems.put(name, item);
        return item;
    }

    public static CustomItem createItem(String name, Material material, short durability, String... lore) throws InstanceAlreadyExistsException {
        return createItem(name, material, durability, null, lore);
    }

    public static CustomItem createItem(String name, Material material, short durability) throws InstanceAlreadyExistsException {
        return createItem(name, material, durability, "");
    }

    private String name;
    private String[] lore;
    private Material material;
    private short durability;


    private Consumer<ItemStack> onCreation;

    private CustomItem(String name, Material material, short durability, Consumer<ItemStack> onCreation, String... lore) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.durability = durability;
        this.onCreation = onCreation;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        item.setDurability(durability);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
        onCreation(item);
        return item;
    }

    private void onCreation(ItemStack item) {
        if (this.onCreation != null) {
            this.onCreation.accept(item);
        }
    }

    private CustomItem(CustomItem customItem) {
        this.name = customItem.name;
        this.lore = new String[customItem.lore.length];
        System.arraycopy(customItem.lore, 0, this.lore, 0, customItem.lore.length);
        this.material = customItem.material;
        this.durability = customItem.durability;
        this.onCreation = customItem.onCreation;
    }


    public boolean itemIsInstance(ItemStack itemStack) {
        return itemStack.getData().getItemType().equals(material) &&
                itemStack.getItemMeta().getItemFlags().containsAll(Arrays.asList(flags)) &&
                itemStack.getDurability() == this.durability;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CustomItem) {
            CustomItem customItem = (CustomItem) o;
            return customItem.name.equals(this.name);
        }
        return false;
    }

}
