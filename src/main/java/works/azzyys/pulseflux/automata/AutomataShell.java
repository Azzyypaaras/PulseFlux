package works.azzyys.pulseflux.automata;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import works.azzyys.pulseflux.registry.PulseFluxRegistries;

import java.util.*;

public abstract class AutomataShell<A extends Automaton> {

    public static final StateFilter PASS = (state, pos, world) -> true;

    protected final Map<UUID, A> automatons = new HashMap<>();
    protected final List<BlockPos> trackedPositions = new ArrayList<>();
    protected final RegistryEntry.Reference<AutomataShell<?>> registryEntry;

    public AutomataShell() {
        registryEntry = PulseFluxRegistries.AUTOMATA_SHELLS.createEntry(this);
    }

    abstract boolean processBlockSwap(World world, BlockPos pos, BlockState oldState, BlockState newState);

    abstract void tick();

    abstract Set<Block> getBlocksOfInterest();

    abstract List<Candidate<?>> generateMappingCandidatesFor(Block block);

    public record Candidate<T extends AutomataShell<?>>(T shell, int priority, boolean blocking, StateFilter filter) implements Comparable<Candidate<?>> {

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
}
