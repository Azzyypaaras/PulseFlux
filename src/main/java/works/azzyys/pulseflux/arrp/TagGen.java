package works.azzyys.pulseflux.arrp;

import net.devtech.arrp.json.tags.JTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import works.azzyys.pulseflux.PulseFlux;

import java.util.Arrays;
import java.util.List;

public class TagGen {

    public static void requireTool(Tool tool, Identifier id) {
        requireTool(tool, null, id);
    }

    public static void requireTool(Tool tool, @Nullable Tier tier, Identifier id) {
        tool.add(id);
        if(tier != null)
            tier.add(id);
    }

    public static void categorize(CATEGORY category, Identifier id) {
        category.add(id, true);
    }

    public static void addTags() {
        Arrays.stream(Tool.values()).forEach(Tag::register);
        Arrays.stream(Tier.values()).forEach(Tag::register);
    }

    public enum Tool implements Tag {
        AXE(PulseFluxResources.vanilla("blocks/mineable/axe")),
        PICKAXE(PulseFluxResources.vanilla("blocks/mineable/pickaxe")),
        SHOVEL(PulseFluxResources.vanilla("blocks/mineable/shovel")),
        HOE(PulseFluxResources.vanilla("blocks/mineable/hoe")),
        SWORD(PulseFluxResources.fabric("blocks/mineable/sword")),
        SHEARS(PulseFluxResources.fabric("blocks/mineable/shears")),
        WRENCH(PulseFlux.locate("blocks/mineable/wrench"));

        public final JTag tag = JTag.tag();
        public final Identifier id;

        Tool(Identifier id) {
            this.id = id;
        }

        public void add(Identifier block) {
            tag.add(block);
        }

        public void register() {
            PulseFluxResources.PACK.addTag(id, tag);
        }
    }

    public enum Tier implements Tag {
        STONE(PulseFluxResources.vanilla("blocks/needs_stone_tool")),
        IRON(PulseFluxResources.vanilla("blocks/needs_iron_tool")),
        DIAMOND(PulseFluxResources.vanilla("blocks/needs_diamond_tool")),
        NETHERITE(PulseFluxResources.fabric("blocks/needs_tool_level_4"));

        public final JTag tag = JTag.tag();
        public final Identifier id;

        Tier(Identifier id) {
            this.id = id;
        }

        public void add(Identifier block) {
            tag.add(block);
        }

        @Override
        public void register() {
            PulseFluxResources.PACK.addTag(id, tag);
        }
    }

    public enum CATEGORY implements Tag {
        PLANKS(PulseFluxResources.vanilla("block/planks"), Tool.AXE.tag),
        WALLS(PulseFluxResources.vanilla("block/walls"));

        public final JTag tag = JTag.tag();
        public final List<JTag> parents;
        public final Identifier id;

        CATEGORY(Identifier id, JTag ... parents) {
            this.id = id;
            this.parents = Arrays.asList(parents);
        }

        public void add(Identifier block, boolean allowParents) {
            tag.add(block);
            if(allowParents) {
                parents.forEach(parent -> parent.add(block));
            }
        }

        @Override
        public void register() {
            PulseFluxResources.PACK.addTag(id, tag);
        }
    }

    private interface Tag {
        void register();
    }
}
