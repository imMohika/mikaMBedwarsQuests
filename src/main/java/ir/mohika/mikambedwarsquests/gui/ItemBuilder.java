package ir.mohika.mikambedwarsquests.gui;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@Getter
public class ItemBuilder {
  private final ItemStack itemStack;
  private final ItemMeta meta;

  @Builder
  private ItemBuilder(
      Material material,
      int amount,
      short damage,
      @Nullable String name,
      @Singular List<String> lores,
      @Singular List<ItemFlag> flags) {
    if (((amount > material.getMaxStackSize()) || (amount <= 0))) amount = 1;

    itemStack = new ItemStack(material, amount, damage);
    this.meta = itemStack.getItemMeta();

    if (name != null) {
      meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
    }

    if (!lores.isEmpty()) {
      meta.setLore(
          lores.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
    }

    if (!flags.isEmpty()) {
      flags.forEach(meta::addItemFlags);
    }

    itemStack.setItemMeta(meta);
  }
}
