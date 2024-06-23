package org.bukkit.craftbukkit.enchantments;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.BindingCurseEnchantment;
import net.minecraft.world.item.enchantment.VanishingCurseEnchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

public class CraftEnchantment extends Enchantment implements Handleable<net.minecraft.world.item.enchantment.Enchantment> {

    public static Enchantment minecraftToBukkit(net.minecraft.world.item.enchantment.Enchantment minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.ENCHANTMENT, Registry.ENCHANTMENT);
    }

    public static Enchantment minecraftHolderToBukkit(Holder<net.minecraft.world.item.enchantment.Enchantment> minecraft) {
        return CraftEnchantment.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.world.item.enchantment.Enchantment bukkitToMinecraft(Enchantment bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static String bukkitToString(Enchantment bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return bukkit.getKey().toString();
    }

    public static Enchantment stringToBukkit(String string) {
        Preconditions.checkArgument(string != null);

        // We currently do not have any version-dependent remapping, so we can use current version
        // First convert from when only the names where saved
        string = FieldRename.convertEnchantmentName(ApiVersion.CURRENT, string);
        string = string.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(string);

        // Now also convert from when keys where saved
        return CraftRegistry.get(Registry.ENCHANTMENT, key, ApiVersion.CURRENT);
    }

    private final NamespacedKey key;
    private final net.minecraft.world.item.enchantment.Enchantment handle;
    private final int id;

    public CraftEnchantment(NamespacedKey key, net.minecraft.world.item.enchantment.Enchantment handle) {
        this.key = key;
        this.handle = handle;
        this.id = BuiltInRegistries.ENCHANTMENT.getId(handle);
    }

    @Override
    public net.minecraft.world.item.enchantment.Enchantment getHandle() {
        return this.handle;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public int getMaxLevel() {
        return this.handle.getMaxLevel();
    }

    @Override
    public int getStartLevel() {
        return this.handle.getMinLevel();
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        throw new UnsupportedOperationException("Method no longer applicable. Use Tags instead.");
    }

    @Override
    public boolean isTreasure() {
        return this.handle.isTreasureOnly();
    }

    @Override
    public boolean isCursed() {
        return this.handle.isCurse(); // Paper - More Enchantment API
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return this.handle.canEnchant(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public String getName() {
        // PAIL: migration paths
        return switch (this.id) {
            case 0 -> "PROTECTION_ENVIRONMENTAL";
            case 1 -> "PROTECTION_FIRE";
            case 2 -> "PROTECTION_FALL";
            case 3 -> "PROTECTION_EXPLOSIONS";
            case 4 -> "PROTECTION_PROJECTILE";
            case 5 -> "OXYGEN";
            case 6 -> "WATER_WORKER";
            case 7 -> "THORNS";
            case 8 -> "DEPTH_STRIDER";
            case 9 -> "FROST_WALKER";
            case 10 -> "BINDING_CURSE";
            case 11 -> "SOUL_SPEED";
            case 12 -> "SWIFT_SNEAK";
            case 13 -> "DAMAGE_ALL";
            case 14 -> "DAMAGE_UNDEAD";
            case 15 -> "DAMAGE_ARTHROPODS";
            case 16 -> "KNOCKBACK";
            case 17 -> "FIRE_ASPECT";
            case 18 -> "LOOT_BONUS_MOBS";
            case 19 -> "SWEEPING_EDGE";
            case 20 -> "DIG_SPEED";
            case 21 -> "SILK_TOUCH";
            case 22 -> "DURABILITY";
            case 23 -> "LOOT_BONUS_BLOCKS";
            case 24 -> "ARROW_DAMAGE";
            case 25 -> "ARROW_KNOCKBACK";
            case 26 -> "ARROW_FIRE";
            case 27 -> "ARROW_INFINITE";
            case 28 -> "LUCK";
            case 29 -> "LURE";
            case 30 -> "LOYALTY";
            case 31 -> "IMPALING";
            case 32 -> "RIPTIDE";
            case 33 -> "CHANNELING";
            case 34 -> "MULTISHOT";
            case 35 -> "QUICK_CHARGE";
            case 36 -> "PIERCING";
            case 37 -> "DENSITY";
            case 38 -> "BREACH";
            case 39 -> "WIND_BURST";
            case 40 -> "MENDING";
            case 41 -> "VANISHING_CURSE";
            default -> this.getKey().toString();
        };
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        if (other instanceof EnchantmentWrapper) {
            other = ((EnchantmentWrapper) other).getEnchantment();
        }
        if (!(other instanceof CraftEnchantment)) {
            return false;
        }
        CraftEnchantment ench = (CraftEnchantment) other;
        return !this.handle.isCompatibleWith(ench.getHandle());
    }
    // Paper start
    @Override
    public net.kyori.adventure.text.Component displayName(int level) {
        return io.papermc.paper.adventure.PaperAdventure.asAdventure(this.handle.getFullname(level));
    }

    @Override
    public String translationKey() {
        return this.handle.getDescriptionId();
    }

    @Override
    public boolean isTradeable() {
        return this.handle.isTradeable();
    }

    @Override
    public boolean isDiscoverable() {
        return this.handle.isDiscoverable();
    }

    @Override
    public int getMinModifiedCost(int level) {
        return this.handle.getMinCost(level);
    }

    @Override
    public int getMaxModifiedCost(int level) {
        return this.handle.getMaxCost(level);
    }

    @Override
    public int getAnvilCost() {
        return this.handle.getAnvilCost();
    }

    @Override
    public io.papermc.paper.enchantments.EnchantmentRarity getRarity() {
        throw new UnsupportedOperationException("Enchantments don't have a rarity anymore in 1.20.5+.");
    }

    @Override
    public float getDamageIncrease(int level, org.bukkit.entity.EntityCategory entityCategory) {
        return this.handle.getDamageBonus(level, this.guessEntityTypeFromEnchantmentCategory(entityCategory));
    }

    @Override
    public float getDamageIncrease(int level, org.bukkit.entity.EntityType entityType) {
        return this.handle.getDamageBonus(level, org.bukkit.craftbukkit.util.CraftMagicNumbers.getEntityTypes(entityType));
    }

    @Deprecated(forRemoval = true)
    private net.minecraft.world.entity.EntityType<?> guessEntityTypeFromEnchantmentCategory(
        final org.bukkit.entity.EntityCategory entityCategory
    ) {
        final net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> tag = switch (entityCategory) {
            case ARTHROPOD -> net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS;
            case UNDEAD -> net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_SMITE;
            case WATER -> net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_IMPALING;
            default -> null;
        };
        if (tag == null) return null;

        return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getTag(tag)
            .map(e -> e.size() > 0 ? e.get(0).value() : null)
            .orElse(null);
    }

    @Override
    public java.util.Set<org.bukkit.inventory.EquipmentSlot> getActiveSlots() {
        return java.util.stream.Stream.of(this.handle.definition.slots()).map(org.bukkit.craftbukkit.CraftEquipmentSlot::getSlot).collect(java.util.stream.Collectors.toSet());
    }
    // Paper end

    @Override
    public String getTranslationKey() {
        return this.handle.getDescriptionId();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftEnchantment)) {
            return false;
        }

        return this.getKey().equals(((Enchantment) other).getKey());
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

    @Override
    public String toString() {
        return "CraftEnchantment[" + this.getKey() + "]";
    }
}
