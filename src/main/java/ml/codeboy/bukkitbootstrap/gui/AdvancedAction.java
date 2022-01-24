package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.entity.Player;

/**
 * This is just so you can easier define actions which also accept booleans
 */
public interface AdvancedAction extends Action {

    @Override
    default void execute(Player player){}

    @Override
    void click(Player player, boolean leftClick, boolean shiftClick);
}
