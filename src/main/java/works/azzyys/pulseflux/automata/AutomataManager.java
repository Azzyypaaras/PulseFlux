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

import java.util.*;

public final class AutomataManager implements AutoSyncedComponent, CommonTickingComponent {

    private final ListMultimap<Block, AutomataShell.Candidate<?>> mappings;
    private final List<AutomataShell<?>> shells = new ArrayList<>();
    private final Set<Block> blocksOfInterest = new HashSet<>();
    private final Set<BlockPos> tracked = new HashSet<>();

    public AutomataManager(World world) {
        mappings = MultimapBuilder
                .hashKeys(128)
                .arrayListValues(1)
                .build();
    }

    public void processBlockSwap(World world, BlockPos pos, BlockState oldState, BlockState newState) {
    }

    @Override
    public void tick() {
        for (AutomataShell<?> shell : shells) {
            shell.tick();
        }
    }

    public void track(AutomataShell<?> shell) {
        shells.add(shell);
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
