package net.id.pulseflux.network;

import net.id.pulseflux.block.transport.LogisticComponentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.LevelProperties;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class LogisticNetwork<T extends LogisticNetwork<?>> {

    public final UUID networkId;
    public final Predicate<BlockState> componentValidator;
    public final List<BlockPos> invalidComponents;

    private final List<BlockPos> components;
    private final World world;
    private boolean revalidationCached;
    private boolean revalidationRequestTick;


    public LogisticNetwork(World world, UUID networkId, Predicate<BlockState> componentValidator) {
        this.world = world;
        this.networkId = networkId;
        this.componentValidator = componentValidator;
        this.invalidComponents = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    public LogisticNetwork(World world, NbtCompound nbt, Predicate<BlockState> componentValidator) {
        this.world = world;
        this.componentValidator = componentValidator;
        this.networkId = nbt.getUuid("networkId");
        components = Arrays.stream(nbt.getLongArray("components")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
        invalidComponents = Arrays.stream(nbt.getLongArray("invalid")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
        revalidationCached = nbt.getBoolean("revalidating");
    }

    public void tick() {
        if(revalidationCached && !revalidationRequestTick) {
            revalidateComponents();
            revalidateCapacity();
            revalidationCached = false;
        }

        if(revalidationRequestTick) {
            revalidationRequestTick = false;
        }
    }

    public void revalidateComponents() {
        List<BlockPos> invalidatedComponents = components
                .stream()
                .filter(pos -> !componentValidator.test(world.getBlockState(pos)))
                .collect(Collectors.toList());
        
        if(!invalidatedComponents.isEmpty()) {
            for (BlockPos component : invalidatedComponents) {
                int adjacency = 0;

                for (Direction direction : Direction.values()) {
                    var pos = component.offset(direction);
                    var state = world.getBlockState(pos);
                    if(state.getBlock() instanceof LogisticComponentBlock logisticComponent && componentValidator.test(state) && logisticComponent.isConnectedToComponent(world, pos, direction.getOpposite()))
                        adjacency++;
                }

                if(adjacency > 1) {
                    invalidComponents.add(component);
                }

                components.remove(component);
            }
        }
    }

    abstract void revalidateCapacity();

    public void requestNetworkRevalidation() {
        revalidationCached = true;
        revalidationRequestTick = true;
    }

    abstract void yieldTo(T network, NetworkManager manager);

    public void appendComponent(BlockPos pos) {
        if(!components.contains(pos)) {
            components.add(pos);
            postAppend(pos);
        }
    }

    abstract void postAppend(BlockPos pos);

    public boolean containsComponent(BlockPos pos, BlockState component) {
        return componentValidator.test(component) && components.contains(pos);
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    public int getConnectedComponents() {
        return components.size();
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putUuid("networkId", networkId);
        nbt.putLongArray("components", components.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putLongArray("invalid", invalidComponents.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putBoolean("revalidating", revalidationCached);
        return nbt;
    }

    @Override
    public String toString() {
        LevelProperties properties = world.getLevelProperties() instanceof LevelProperties ? (LevelProperties) world.getLevelProperties() : null;
        List<Chunk> chunks = components.stream().map(world::getChunk).distinct().collect(Collectors.toList());
        StringBuilder text = new StringBuilder("Power network: " + networkId.toString() + " - type: " + this.getClass().getCanonicalName() + "\nlevel: " + (properties == null ? "UNAVAILABLE" : properties.getLevelName()) + " - world: " + world.getRegistryKey().getValue().toString() + " - chunks: ");
        chunks.forEach(chunk -> text.append(chunk.getPos().toString()).append(" "));
        return text.toString();
    }
}
