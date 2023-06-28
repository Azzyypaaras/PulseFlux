package works.azzyys.pulseflux.block.transport;

import works.azzyys.pulseflux.block.base.PFBlockWithEntity;
import works.azzyys.pulseflux.network.NetworkManager;
import works.azzyys.pulseflux.network.TransferNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public interface LogisticComponentBlock<T extends TransferNetwork<T>> {

    default boolean isConnectedToComponent(World world, BlockPos pos, Direction direction) {
        return false;
    }

    boolean isCompatibleWith(TransferNetwork<?> network);

    void switchNetwork(BlockPos pos, T network, NetworkManager manager);

    Optional<T> getParentNetwork(World world, BlockPos pos);

    T createNetwork(World world, UUID id);
}
