package io.papermc.paper.adventure;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import org.bukkit.craftbukkit.CraftRegistry;

final class WrapperAwareSerializer implements ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> {
    @Override
    public Component deserialize(final net.minecraft.network.chat.Component input) {
        if (input instanceof AdventureComponent) {
            return ((AdventureComponent) input).adventure;
        }
        final RegistryOps<Object> ops = CraftRegistry.getMinecraftRegistry().createSerializationContext(JavaOps.INSTANCE);
        final Object obj = ComponentSerialization.CODEC.encodeStart(ops, input)
            .getOrThrow(s -> new RuntimeException("Failed to encode Minecraft Component: " + input + "; " + s));
        final Pair<Component, Object> converted = AdventureCodecs.COMPONENT_CODEC.decode(ops, obj)
            .getOrThrow(s -> new RuntimeException("Failed to decode to adventure Component: " + obj + "; " + s));
        return converted.getFirst();
    }

    @Override
    public net.minecraft.network.chat.Component serialize(final Component component) {
        final RegistryOps<Object> ops = CraftRegistry.getMinecraftRegistry().createSerializationContext(JavaOps.INSTANCE);
        final Object obj = AdventureCodecs.COMPONENT_CODEC.encodeStart(ops, component)
            .getOrThrow(s -> new RuntimeException("Failed to encode adventure Component: " + component + "; " + s));
        final Pair<net.minecraft.network.chat.Component, Object> converted = ComponentSerialization.CODEC.decode(ops, obj)
            .getOrThrow(s -> new RuntimeException("Failed to decode to Minecraft Component: " + obj + "; " + s));
        return converted.getFirst();
    }
}
