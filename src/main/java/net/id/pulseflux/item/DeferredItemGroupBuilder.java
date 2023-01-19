package net.id.pulseflux.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.id.incubus_core.util.RegistryQueue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class DeferredItemGroupBuilder {

    public final RegistryQueue.Action<Item> grouper = (identifier, item) -> add(item);
    private final ItemGroup.Builder builder;
    private final List<EntryData> entries = new ArrayList<>();
    private final Optional<Comparator<EntryData>> sorter = Optional.empty();

    public DeferredItemGroupBuilder(Identifier id, Optional<Comparator<EntryData>> sorter) {
        builder = FabricItemGroup.builder(id);
        builder.displayName(Text.translatable("itemGroup." + id.toTranslationKey()));
    }

    public static DeferredItemGroupBuilder of(Identifier id) {
        return of(id, null);
    }

    public static DeferredItemGroupBuilder of(Identifier id, @Nullable Comparator<EntryData> sorter) {
        return new DeferredItemGroupBuilder(id, Optional.ofNullable(sorter));
    }

    public ItemGroup build(ItemStack stack) {
        return build(() -> stack);
    }

    public ItemGroup build(Supplier<ItemStack> icon) {
        sorter.ifPresent(entries::sort);
        builder.entries((enabledFeatures, entryList, operatorEnabled) ->
                entries.forEach(data -> data.apply(enabledFeatures, entryList, operatorEnabled)));
        builder.icon(icon);
        return builder.build();
    }

    public void add(ItemConvertible item) {
        entries.add(EntryData.of(item));
    }

    public void add(ItemStack stack) {
        entries.add(EntryData.of(stack));
    }

    public void add(EntryData data) {
        entries.add(data);
    }

    public record EntryData(ItemStack stack, ItemGroup.StackVisibility visibility, Optional<BiPredicate<FeatureSet, Boolean>> filter) {

        private void apply(FeatureSet enabledFeatures, ItemGroup.Entries entries, boolean opEnabled) {
            if (filter.orElse((f, b) -> true).test(enabledFeatures, opEnabled))
                entries.add(stack);
        }

        public static EntryData of(@NotNull ItemConvertible item) {
            return new EntryData(new ItemStack(item.asItem()),  ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, Optional.empty());
        }

        public static EntryData of(@NotNull ItemStack stack) {
            return new EntryData(stack,  ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, Optional.empty());
        }

        public static EntryData of(@NotNull ItemConvertible item, @NotNull BiPredicate<FeatureSet, Boolean> filter) {
            return new EntryData(new ItemStack(item.asItem()),  ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, Optional.of(filter));
        }

        public static EntryData of(@NotNull ItemConvertible item, @NotNull ItemGroup.StackVisibility visibility) {
            return new EntryData(new ItemStack(item.asItem()), visibility, Optional.empty());
        }

        public static EntryData of(@NotNull ItemConvertible item, @NotNull ItemGroup.StackVisibility visibility, @NotNull BiPredicate<FeatureSet, Boolean> filter) {
            return new EntryData(new ItemStack(item.asItem()), visibility, Optional.of(filter));
        }
    }
}
