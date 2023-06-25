package works.azzyys.pulseflux.block.transport;

import works.azzyys.pulseflux.block.base.PFBlockWithEntity;
import works.azzyys.pulseflux.network.NetworkManager;
import works.azzyys.pulseflux.network.TransferNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public abstract class LogisticComponentBlock<T extends TransferNetwork<T>> extends PFBlockWithEntity {

    public LogisticComponentBlock(Settings settings, boolean loggable) {
        super(settings, loggable);
    }

    public boolean isConnectedToComponent(World world, BlockPos pos, Direction direction) {
        return false;
    }

    public abstract boolean isCompatibleWith(TransferNetwork<?> network);

    public abstract void switchNetwork(BlockPos pos, T network, NetworkManager manager);

    public abstract Optional<T> getParentNetwork(World world, BlockPos pos);

    public abstract T createNetwork(World world, UUID id);
}
