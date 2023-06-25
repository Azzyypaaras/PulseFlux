package works.azzyys.pulseflux.block.transport;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.id.incubus_core.systems.Material;
import net.id.incubus_core.systems.MaterialProvider;
import works.azzyys.pulseflux.network.FluidNetwork;
import works.azzyys.pulseflux.network.InvalidatedComponent;
import works.azzyys.pulseflux.network.NetworkManager;
import works.azzyys.pulseflux.network.TransferNetwork;
import works.azzyys.pulseflux.util.BlockReference;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class FluidPipeBlock extends PipeBlock<FluidNetwork> implements MaterialProvider {

    private final Material material;
    private final long volume;

    public FluidPipeBlock(Settings settings, Material material, long volume) {
        super(settings, FluidStorage.SIDED);
        this.material = material;
        this.volume = volume;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return FluidPipeBlockEntity::tick;
    }

    @Override
    void postProcessWrenchHit(World world, Direction direction, BlockReference reference, boolean disconnecting) {

        if (world.isClient())
            return;

        reference = reference.tryRecreateWithBE(world);
        if (!reference.validateBE())
            return;

        var entity = (FluidPipeBlockEntity) reference.tryGetBlockEntity().get();
        var manager = NetworkManager.getNetworkManager(world);
        var checkPos = reference.pos.offset(direction);
        var check = world.getBlockState(checkPos);

        if (disconnecting) {
            if (check.getBlock() instanceof PipeBlock<?> pipe && pipe.lookup == this.lookup && pipe.isConnectedToComponent(world, checkPos, direction.getOpposite())) {
                BlockReference finalReference = reference;
                entity.getParentNetwork().ifPresent(network -> {
                    network.invalidComponents.add(new InvalidatedComponent(InvalidatedComponent.Reason.WRENCHED, finalReference.pos));
                    network.requestNetworkRevalidation();
                });
            }

        } else {
            if (check.getBlock() instanceof PipeBlock<?> pipe && pipe.lookup == this.lookup) {

                pipe.notifyConnectionAttempt(world, check, checkPos, direction.getOpposite());
                var network = entity.getParentNetwork();
                var otherNetwork = pipe.getParentNetwork(world, checkPos);

                if (network.isEmpty() || otherNetwork.isEmpty() || network.get() == otherNetwork.get())
                    return;

                otherNetwork.ifPresent(transferNetwork -> ((FluidNetwork) transferNetwork).yieldTo(network.get(), manager));

            }

        }
    }

    @Override
    public void switchNetwork(BlockPos pos, FluidNetwork network, NetworkManager manager) {
        if(manager.world.getBlockEntity(pos) instanceof FluidPipeBlockEntity pipe) {
            pipe.trySwitchNetwork(network, manager);
        }
    }

    @Override
    public Optional<FluidNetwork> getParentNetwork(World world, BlockPos pos) {
        return ((FluidPipeBlockEntity) world.getBlockEntity(pos)).getParentNetwork();
    }

    @Override
    public boolean isCompatibleWith(TransferNetwork<?> network) {
        return network instanceof FluidNetwork;
    }

    @Override
    public FluidNetwork createNetwork(World world, UUID id) {
        return new FluidNetwork(world, id);
    }

    @Override
    public Material getMaterial(@Nullable Direction direction) {
        return material;
    }

    public long volume() {
        return volume;
    }
}
