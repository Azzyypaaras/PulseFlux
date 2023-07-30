package works.azzyys.pulseflux.automata;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import works.azzyys.pulseflux.registry.PulseFluxRegistries;
import works.azzyys.pulseflux.util.BlockReference;
import works.azzyys.pulseflux.util.sync.Synced;

import java.util.*;

public abstract class AutomataShell<A extends Automaton> {

    public static final StateFilter PASS = (state, pos, world) -> true;

    protected final Map<UUID, A> automatons = new HashMap<>();
    protected final List<BlockPos> trackedPositions = new ArrayList<>();
    protected final RegistryEntry.Reference<AutomataShell<?>.Signature> registryEntry;
    protected final AutomataManager manager;

    @ApiStatus.Internal
    public AutomataShell(AutomataManager manager) {
        registryEntry = PulseFluxRegistries.SHELL_SIGNATURE.createEntry(getSignature());
        this.manager = manager;
    }

    @ApiStatus.OverrideOnly
    abstract void processNew(BlockReference refence, World world);

    @ApiStatus.OverrideOnly
    abstract void processChange(BlockReference reference, BlockState newState, World world);

    abstract boolean isBlockTracked(BlockPos pos);

    /**
     * Tick the shell - process things such as connections and synchronization here. <p>
     * This runs <i>after</i> {@link AutomataShell#tickAutomata()}
     */
    @ApiStatus.OverrideOnly
    abstract void tick();

    /**
     * Tick the automata belonging to this shell instance here. <p>
     * You probably won't need to override this.
     */
    public void tickAutomata() {
        for (A automaton : automatons.values()) {
            if (automaton.canTick())
                automaton.tick();
        }
    }

    abstract Set<Block> getBlocksOfInterest();


    /**
     * Generate the various mappings for your Shell here.<p>
     * Mappings are the way the main manager knows how to delegate
     * @param block The block that is being mapped
     * @return A list of mapping candidates
     */
    @ApiStatus.OverrideOnly
    abstract List<Candidate<?>> generateMappingCandidatesFor(Block block);

    /**
     * Now, I am not going to tell you that Signatures should singletons, but if you have touched
     * registries for more than five seconds you should know.
     * <p>
     * I will, however, tell you that these should be unique per Shell class
     */
    abstract Signature getSignature();

    public record Candidate<T extends AutomataShell<?>>(T shell, int priority, boolean blocking, StateFilter filter) implements Comparable<Candidate<?>> {
        @ApiStatus.Internal
        public boolean test(@NotNull BlockState state, Optional<BlockPos> pos, Optional<World> world) {
            return filter.test(state, pos, world);
        }

        @Override
        public int compareTo(@NotNull Candidate<?> candidate) {
            if (blocking() && !candidate.blocking()) {
                return -1;
            }
            else if (candidate.blocking() && !blocking()) {
                return 1;
            }


            return -(priority() - candidate.priority());
        }
    }

    @FunctionalInterface
    interface StateFilter {
        boolean test(@NotNull BlockState state, Optional<BlockPos> pos, Optional<World> world);
    }

    public abstract class Signature {

        private final Identifier id;

        public Signature(Identifier id) {
            this.id = id;
        }

        @ApiStatus.OverrideOnly
        abstract AutomataShell<A> instance();

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AutomataShell<?>.Signature signature) {
                return this.id.equals(signature.id);
            }

            return false;
        }

        public Identifier getId() {
            return id;
        }
    }
}
