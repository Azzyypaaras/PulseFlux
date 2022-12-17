package net.id.pulseflux.arrp;

import net.devtech.arrp.json.tags.JTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static net.id.pulseflux.PulseFlux.locate;
import static net.id.pulseflux.arrp.PulseFluxResources.*;

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
        AXE(vanilla("blocks/mineable/axe")),
        PICKAXE(vanilla("blocks/mineable/pickaxe")),
        SHOVEL(vanilla("blocks/mineable/shovel")),
        HOE(vanilla("blocks/mineable/hoe")),
        SWORD(fabric("blocks/mineable/sword")),
        SHEARS(fabric("blocks/mineable/shears")),
        WRENCH(locate("blocks/mineable/wrench"));

        public final JTag tag = JTag.tag();
        public final Identifier id;

        Tool(Identifier id) {
            this.id = id;
        }

        public void add(Identifier block) {
            tag.add(block);
        }

        public void register() {
            PACK.addTag(id, tag);
        }
    }

    public enum Tier implements Tag {
        STONE(vanilla("blocks/needs_stone_tool")),
        IRON(vanilla("blocks/needs_iron_tool")),
        DIAMOND(vanilla("blocks/needs_diamond_tool")),
        NETHERITE(fabric("blocks/needs_tool_level_4"));

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
            PACK.addTag(id, tag);
        }
    }

    public enum CATEGORY implements Tag {
        PLANKS(vanilla("block/planks"), Tool.AXE.tag),
        WALLS(vanilla("block/walls"));

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
            PACK.addTag(id, tag);
        }
    }

    private interface Tag {
        void register();
    }
}
