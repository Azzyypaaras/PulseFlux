package works.azzyys.pulseflux.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.id.incubus_core.util.RegistryQueue.Action;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import works.azzyys.pulseflux.arrp.AssetGen;
import works.azzyys.pulseflux.arrp.DataGen;
import works.azzyys.pulseflux.arrp.TagGen;

import static net.id.incubus_core.util.RegistryQueue.onClient;
import static works.azzyys.pulseflux.arrp.TagGen.requireTool;

class PulseFluxBlockActions {
    protected static final AbstractBlock.ContextPredicate never = (state, view, pos) -> false;
    protected static final AbstractBlock.ContextPredicate always = (state, view, pos) -> true;


    /**
     * QUALITIES
     */

    protected static Action<Block> flammable(int spread, int burn) { return (id, block) -> FlammableBlockRegistry.getDefaultInstance().add(block, spread, burn);}
    protected static final Action<Block> flammableLog = flammable(5, 5);
    protected static final Action<Block> flammablePlanks = flammable(20, 5);
    protected static final Action<Block> flammableLeaves = flammable(60, 30);
    protected static final Action<Block> flammablePlant = flammable(60, 100);


    /**
     * TOOLS
     */

    public static final Action<Block> useAxe = (id, block) -> TagGen.requireTool(TagGen.Tool.AXE, id);
    public static final Action<Block> usePickaxe = (id, block) -> TagGen.requireTool(TagGen.Tool.PICKAXE, id);
    public static final Action<Block> useShovel = (id, block) -> TagGen.requireTool(TagGen.Tool.SHOVEL, id);
    public static final Action<Block> useHoe = (id, block) -> TagGen.requireTool(TagGen.Tool.HOE, id);
    public static final Action<Block> useSword = (id, block) -> TagGen.requireTool(TagGen.Tool.SWORD, id);
    public static final Action<Block> useShears = (id, block) -> TagGen.requireTool(TagGen.Tool.SHEARS, id);

    public static final Action<Block> useWrench = (id, block) -> TagGen.requireTool(TagGen.Tool.WRENCH, id);

    public static Action<Block> tier(TagGen.Tier tier) { return (identifier, block) -> tier.add(identifier); }


    /**
     * MATERIAL
     */

    public static final Action<Block> planks = (identifier, block) -> TagGen.categorize(TagGen.CATEGORY.PLANKS, identifier);


    /**
     * CLIENT
     */

    protected static final Action<Block> translucentRenderLayer = onClient((id, block) -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent()));
    protected static final Action<Block> cutoutRenderLayer = onClient((id, block) -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout()));
    protected static final Action<Block> cutoutMippedRenderLayer = onClient((id, block) -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutoutMipped()));


    /**
     * ASSETS
     */

    public static final Action<Block> generateDiodeAssets = (id, block) -> AssetGen.createDiodeAssets(id);
    public static final Action<Block> generateAssets = (id, block) -> AssetGen.createBlockAssets(id);
    public static final Action<Block> generatePipeAssets = (id, block) -> AssetGen.createPipeAssets(id);

    public static final Action<Block> selfDrop = (identifier, block) -> DataGen.lootSelfDrop(identifier);


    /**
     * MISC
     */

    protected static final Action<AbstractSignBlock> signBlockEntity = (id, block) -> ((BlockEntityTypeAccessor) BlockEntityType.SIGN).getBlocks().add(block);

    protected static Action<Block> strippedFrom(Block original) { return (id, stripped) -> StrippableBlockRegistry.register(original, stripped);}
}
