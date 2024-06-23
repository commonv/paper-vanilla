package io.papermc.paper.registry;

import io.papermc.paper.registry.entry.RegistryEntry;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.CraftMusicInstrument;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import static io.papermc.paper.registry.entry.RegistryEntry.apiOnly;
import static io.papermc.paper.registry.entry.RegistryEntry.entry;

@DefaultQualifier(NonNull.class)
public final class PaperRegistries {

    @Deprecated(forRemoval = true)
    @org.jetbrains.annotations.VisibleForTesting
    public static final RegistryKey<io.papermc.paper.world.structure.ConfiguredStructure> CONFIGURED_STRUCTURE_REGISTRY_KEY = RegistryKeyImpl.createInternal("worldgen/structure");
    @Deprecated(forRemoval = true)
    static final RegistryEntry<Structure, io.papermc.paper.world.structure.ConfiguredStructure, ?> CONFIGURED_STRUCTURE_REGISTRY_ENTRY = entry(Registries.STRUCTURE, CONFIGURED_STRUCTURE_REGISTRY_KEY, io.papermc.paper.world.structure.ConfiguredStructure.class, io.papermc.paper.world.structure.PaperConfiguredStructure::minecraftToBukkit).delayed();

    static final List<RegistryEntry<?, ?, ?>> REGISTRY_ENTRIES;
    private static final Map<RegistryKey<?>, RegistryEntry<?, ?, ?>> BY_REGISTRY_KEY;
    private static final Map<ResourceKey<?>, RegistryEntry<?, ?, ?>> BY_RESOURCE_KEY;
    static {
        REGISTRY_ENTRIES = List.of(
            // built-ins
            entry(Registries.ENCHANTMENT, RegistryKey.ENCHANTMENT, Enchantment.class, CraftEnchantment::new).withSerializationUpdater(FieldRename.ENCHANTMENT_RENAME),
            entry(Registries.GAME_EVENT, RegistryKey.GAME_EVENT, GameEvent.class, CraftGameEvent::new),
            entry(Registries.INSTRUMENT, RegistryKey.INSTRUMENT, MusicInstrument.class, CraftMusicInstrument::new),
            entry(Registries.MOB_EFFECT, RegistryKey.MOB_EFFECT, PotionEffectType.class, CraftPotionEffectType::new),
            entry(Registries.STRUCTURE_TYPE, RegistryKey.STRUCTURE_TYPE, StructureType.class, CraftStructureType::new),
            entry(Registries.BLOCK, RegistryKey.BLOCK, BlockType.class, CraftBlockType::new),
            entry(Registries.ITEM, RegistryKey.ITEM, ItemType.class, CraftItemType::new),

            // data-drivens
            entry(Registries.STRUCTURE, RegistryKey.STRUCTURE, Structure.class, CraftStructure::new).delayed(),
            entry(Registries.TRIM_MATERIAL, RegistryKey.TRIM_MATERIAL, TrimMaterial.class, CraftTrimMaterial::new).delayed(),
            entry(Registries.TRIM_PATTERN, RegistryKey.TRIM_PATTERN, TrimPattern.class, CraftTrimPattern::new).delayed(),
            entry(Registries.DAMAGE_TYPE, RegistryKey.DAMAGE_TYPE, DamageType.class, CraftDamageType::new).delayed(),
            entry(Registries.WOLF_VARIANT, RegistryKey.WOLF_VARIANT, Wolf.Variant.class, CraftWolf.CraftVariant::new).delayed(),

            // api-only
            apiOnly(Registries.BIOME, RegistryKey.BIOME, () -> org.bukkit.Registry.BIOME),
            apiOnly(Registries.PAINTING_VARIANT, RegistryKey.PAINTING_VARIANT, () -> org.bukkit.Registry.ART),
            apiOnly(Registries.ATTRIBUTE, RegistryKey.ATTRIBUTE, () -> org.bukkit.Registry.ATTRIBUTE),
            apiOnly(Registries.BANNER_PATTERN, RegistryKey.BANNER_PATTERN, () -> org.bukkit.Registry.BANNER_PATTERN),
            apiOnly(Registries.CAT_VARIANT, RegistryKey.CAT_VARIANT, () -> org.bukkit.Registry.CAT_VARIANT),
            apiOnly(Registries.ENTITY_TYPE, RegistryKey.ENTITY_TYPE, () -> org.bukkit.Registry.ENTITY_TYPE),
            apiOnly(Registries.PARTICLE_TYPE, RegistryKey.PARTICLE_TYPE, () -> org.bukkit.Registry.PARTICLE_TYPE),
            apiOnly(Registries.POTION, RegistryKey.POTION, () -> org.bukkit.Registry.POTION),
            apiOnly(Registries.SOUND_EVENT, RegistryKey.SOUND_EVENT, () -> org.bukkit.Registry.SOUNDS),
            apiOnly(Registries.VILLAGER_PROFESSION, RegistryKey.VILLAGER_PROFESSION, () -> org.bukkit.Registry.VILLAGER_PROFESSION),
            apiOnly(Registries.VILLAGER_TYPE, RegistryKey.VILLAGER_TYPE, () -> org.bukkit.Registry.VILLAGER_TYPE),
            apiOnly(Registries.MEMORY_MODULE_TYPE, RegistryKey.MEMORY_MODULE_TYPE, () -> (org.bukkit.Registry<MemoryKey<?>>) (org.bukkit.Registry) org.bukkit.Registry.MEMORY_MODULE_TYPE),
            apiOnly(Registries.FLUID, RegistryKey.FLUID, () -> org.bukkit.Registry.FLUID),
            apiOnly(Registries.FROG_VARIANT, RegistryKey.FROG_VARIANT, () -> org.bukkit.Registry.FROG_VARIANT),
            apiOnly(Registries.MAP_DECORATION_TYPE, RegistryKey.MAP_DECORATION_TYPE, () -> org.bukkit.Registry.MAP_DECORATION_TYPE)
        );
        final Map<RegistryKey<?>, RegistryEntry<?, ?, ?>> byRegistryKey = new IdentityHashMap<>(REGISTRY_ENTRIES.size());
        final Map<ResourceKey<?>, RegistryEntry<?, ?, ?>> byResourceKey = new IdentityHashMap<>(REGISTRY_ENTRIES.size());
        for (final RegistryEntry<?, ?, ?> entry : REGISTRY_ENTRIES) {
            byRegistryKey.put(entry.apiKey(), entry);
            byResourceKey.put(entry.mcKey(), entry);
        }
        BY_REGISTRY_KEY = Collections.unmodifiableMap(byRegistryKey);
        BY_RESOURCE_KEY = Collections.unmodifiableMap(byResourceKey);
    }

    @SuppressWarnings("unchecked")
    public static <M, T extends Keyed, R extends org.bukkit.Registry<T>> @Nullable RegistryEntry<M, T, R> getEntry(final ResourceKey<? extends Registry<M>> resourceKey) {
        return (RegistryEntry<M, T, R>) BY_RESOURCE_KEY.get(resourceKey);
    }

    @SuppressWarnings("unchecked")
    public static <M, T extends Keyed, R extends org.bukkit.Registry<T>> @Nullable RegistryEntry<M, T, R> getEntry(final RegistryKey<? super T> registryKey) {
        return (RegistryEntry<M, T, R>) BY_REGISTRY_KEY.get(registryKey);
    }

    @SuppressWarnings("unchecked")
    public static <M, T> RegistryKey<T> fromNms(final ResourceKey<? extends Registry<M>> registryResourceKey) {
        return (RegistryKey<T>) Objects.requireNonNull(BY_RESOURCE_KEY.get(registryResourceKey), registryResourceKey + " doesn't have an api RegistryKey").apiKey();
    }

    @SuppressWarnings("unchecked")
    public static <M, T> ResourceKey<? extends Registry<M>> toNms(final RegistryKey<T> registryKey) {
        return (ResourceKey<? extends Registry<M>>) Objects.requireNonNull(BY_REGISTRY_KEY.get(registryKey), registryKey + " doesn't have an mc registry ResourceKey").mcKey();
    }

    private PaperRegistries() {
    }
}
