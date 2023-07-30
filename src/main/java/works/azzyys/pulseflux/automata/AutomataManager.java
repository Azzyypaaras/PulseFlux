package works.azzyys.pulseflux.automata;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import works.azzyys.pulseflux.PulseFluxComponents;
import works.azzyys.pulseflux.registry.PulseFluxRegistries;
import works.azzyys.pulseflux.util.BlockReference;

import java.util.*;

public final class AutomataManager implements AutoSyncedComponent, CommonTickingComponent {

    private final ListMultimap<Block, AutomataShell.Candidate<?>> mappings;
    private final Map<AutomataShell<?>.Signature, AutomataShell<?>> shells = new HashMap<>();
    private final Set<Block> blocksOfInterest = new HashSet<>();
    private final List<AutomataShell<?>.Signature> signatures = new ArrayList<>();
    private final World world;


    public AutomataManager(World world) {
        this.world = world;

        mappings = MultimapBuilder
                .hashKeys(128)
                .arrayListValues(1)
                .build();

        PulseFluxRegistries.SHELL_SIGNATURE
                .stream()
                .map(AutomataShell.Signature::instance)
                .forEach(this::manage);
    }

    public void processBlockSwap(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        if (blocksOfInterest.contains(newState.getBlock())) {
            var interestedShells = getShellsFor(world, pos, newState);
            interestedShells.forEach(automataShell -> automataShell.processNew(BlockReference.of(newState, pos), world));
        }
        else if(blocksOfInterest.contains(oldState.getBlock())) {
            var listeningShells = getShellsFor(world, pos, oldState);
            listeningShells
                    .stream()
                    .filter(automataShell -> automataShell.isBlockTracked(pos))
                    .forEach(automataShell -> automataShell.processChange(BlockReference.of(oldState, pos), newState, world));
        }
    }

    public List<AutomataShell<?>> getShellsFor(World world, BlockPos pos, BlockState state) {
        List<AutomataShell<?>> processShells = new ArrayList<>();
        var block = state.getBlock();

        for (AutomataShell.Candidate<?> candidate : mappings.get(block)) {
            if (!candidate.test(state, Optional.of(pos), Optional.of(world)))
                continue;

            processShells.add(candidate.shell());

            if (candidate.blocking()) {
                break;
            }
        }
        return processShells;
    }

    @Override
    public void tick() {
        for (AutomataShell<?> shell : shells.values()) {
            shell.tickAutomata();
            shell.tick();
        }
    }

    public void manage(AutomataShell<?> shell) {
        var signature = shell.getSignature();

        if (signatures.contains(signature)) {
            throw new InputMismatchException("Shell Signature " + signature.getId() + " already exists in the " + world.getRegistryKey().getValue().toString() + " AutomataManager!");
        }

        signatures.add(signature);
        shells.put(shell.getSignature(), shell);

        var trackedBlocks = shell.getBlocksOfInterest();
        for (Block block : trackedBlocks) {
            var candidates = shell.generateMappingCandidatesFor(block);

            blocksOfInterest.add(block);
            mappings.putAll(block, candidates);
            Collections.sort(mappings.get(block));
        }
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        AutoSyncedComponent.super.applySyncPacket(buf);
    }

    public static AutomataManager get(World world) {
        return PulseFluxComponents.AUTOMATA_MANAGER_KEY.get(world);
    }
}
