package ml.codeboy.bukkitbootstrap.gui;

import org.bukkit.entity.Player;

public interface Action {
    Action none = (p) -> {
    };

    void execute(Player player);

    default void click(Player player,boolean leftClick,boolean shiftClick){
        execute(player);
    }
}