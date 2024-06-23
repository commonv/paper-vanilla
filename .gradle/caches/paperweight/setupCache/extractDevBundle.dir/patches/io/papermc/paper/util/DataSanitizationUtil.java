package io.papermc.paper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemContainerContents;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class DataSanitizationUtil {

    private static final ThreadLocal<DataSanitizer> DATA_SANITIZER = ThreadLocal.withInitial(DataSanitizer::new);

    public static DataSanitizer start(final boolean sanitize) {
        final DataSanitizer sanitizer = DATA_SANITIZER.get();
        if (sanitize) {
            sanitizer.start();
        }
        return sanitizer;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> CHARGED_PROJECTILES = codec(ChargedProjectiles.STREAM_CODEC, DataSanitizationUtil::sanitizeChargedProjectiles);
    public static final StreamCodec<RegistryFriendlyByteBuf, BundleContents> BUNDLE_CONTENTS = codec(BundleContents.STREAM_CODEC, DataSanitizationUtil::sanitizeBundleContents);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainerContents> CONTAINER = codec(ItemContainerContents.STREAM_CODEC, contents -> ItemContainerContents.EMPTY);

    private static ChargedProjectiles sanitizeChargedProjectiles(final ChargedProjectiles projectiles) {
        if (projectiles.isEmpty()) {
            return projectiles;
        }
        final List<ItemStack> items = projectiles.getItems();
        final List<ItemStack> sanitized = new ArrayList<>();
        for (int i = 0; i < Math.min(items.size(), 3); i++) {
            // we want to preserve item type as vanilla client can change visuals based on type
            sanitized.add(new ItemStack(items.get(i).getItemHolder()));
        }
        return ChargedProjectiles.of(sanitized);
    }

    private static BundleContents sanitizeBundleContents(final BundleContents contents) {
        // Bundles change their texture based on their fullness.
        int sizeUsed = 0;
        for (final ItemStack item : contents.items()) {
            final int scale = 64 / item.getMaxStackSize();
            sizeUsed += scale * item.getCount();
        }
        // Now we add a single fake item that uses the same amount of slots as all other items.
        final List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Items.PAPER, sizeUsed));
        return new BundleContents(items);
    }

    private static <B, A> StreamCodec<B, A> codec(final StreamCodec<B, A> delegate, final UnaryOperator<A> sanitizer) {
        return new DataSanitizationCodec<>(delegate, sanitizer);
    }

    private record DataSanitizationCodec<B, A>(StreamCodec<B, A> delegate, UnaryOperator<A> sanitizer) implements StreamCodec<B, A> {

        @Override
        public @NonNull A decode(final @NonNull B buf) {
            return this.delegate.decode(buf);
        }

        @Override
        public void encode(final @NonNull B buf, final @NonNull A value) {
            if (!DATA_SANITIZER.get().value().get()) {
                this.delegate.encode(buf, value);
            } else {
                this.delegate.encode(buf, this.sanitizer.apply(value));
            }
        }
    }

    public record DataSanitizer(AtomicBoolean value) implements AutoCloseable {

        public DataSanitizer() {
            this(new AtomicBoolean(false));
        }

        public void start() {
            this.value.compareAndSet(false, true);
        }

        @Override
        public void close() {
            this.value.compareAndSet(true, false);
        }
    }

    private DataSanitizationUtil() {
    }

}
