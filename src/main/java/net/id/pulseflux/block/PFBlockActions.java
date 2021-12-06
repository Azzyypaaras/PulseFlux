package net.id.pulseflux.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.HoeItem;
import net.minecraft.util.Identifier;

import static net.id.incubus_core.util.RegistryQueue.Action;
import static net.id.incubus_core.util.RegistryQueue.onClient;

class PFBlockActions {
    protected static final AbstractBlock.ContextPredicate never = (state, view, pos) -> false;
    protected static final AbstractBlock.ContextPredicate always = (state, view, pos) -> true;

    protected static Action<Block> flammable(int spread, int burn) { return (id, block) -> FlammableBlockRegistry.getDefaultInstance().add(block, spread, burn);}
    protected static final Action<Block> flammableLog = flammable(5, 5);
    protected static final Action<Block> flammablePlanks = flammable(20, 5);
    protected static final Action<Block> flammableLeaves = flammable(60, 30);
    protected static final Action<Block> flammablePlant = flammable(60, 100);

    protected static final Action<Block> translucentRenderLayer = onClient((id, block) -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent()));
    protected static final Action<Block> cutoutRenderLayer = onClient((id, block) -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout()));
    protected static final Action<Block> cutoutMippedRenderLayer = onClient((id, block) -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutoutMipped()));
    
    protected static final Action<AbstractSignBlock> signBlockEntity = (id, block) -> ((BlockEntityTypeAccessor) BlockEntityType.SIGN).getBlocks().add(block);

    protected static Action<Block> strippedFrom(Block original) { return (id, stripped) -> StrippableBlockRegistry.register(original, stripped);}
}
