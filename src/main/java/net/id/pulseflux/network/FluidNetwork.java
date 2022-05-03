package net.id.pulseflux.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.id.pulseflux.block.transport.FluidPipeBlock;
import net.id.pulseflux.blockentity.transport.FluidPipeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FluidNetwork extends TransferNetwork<FluidNetwork> implements SingleSlotStorage<FluidVariant> {

    public FluidNetwork(World world, UUID networkId) {
        super(world, networkId);
    }

    public FluidNetwork(World world, NbtCompound nbt) {
        super(world, nbt);
    }

    @Override
    void revalidateCapacity() {

    }

    @Override
    void yieldTo(TransferNetwork<?> network, NetworkManager manager) {
        manager.managedNetworks.remove(networkId);
        components.stream()
                .map(world::getBlockEntity)
                .filter(Objects::nonNull)
                .forEach(pipe -> {
                    ((FluidPipeEntity) pipe).trySwitchNetwork((FluidNetwork) network, manager);
                    network.appendComponent(pipe.getPos());
                });
        components.clear();
    }

    @Override
    void postAppend(BlockPos pos) {

    }

    @Override
    public boolean isComponentValid(BlockPos pos, BlockState state) {
        return world.getBlockState(pos).getBlock() instanceof FluidPipeBlock && world.getBlockEntity(pos) instanceof FluidPipeEntity;
    }

    @Override
    void postRemove() {

    }

    @Override
    NetworkReconstructor<FluidNetwork> getReconstructor() {
        return Reconstructors.FLUID_RECONSTRUCTOR;
    }

    @Override
    public List<Text> getNetworkInfo() {
        return List.of(
                new LiteralText(" "),
                new LiteralText("Fluid Network " + networkId).setStyle(Style.EMPTY.withColor(0xffb41f)),
                new LiteralText("fluid - EMPTY").setStyle(Style.EMPTY.withColor(0xffb41f)),
                new LiteralText("amount - 0mb").setStyle(Style.EMPTY.withColor(0xffb41f)),
                new LiteralText("pressure - 0KPa").setStyle(Style.EMPTY.withColor(0xffb41f)),
                new LiteralText("size - " + getConnectedComponents()).setStyle(Style.EMPTY.withColor(0xffb41f)),
                new LiteralText(" ")
        );
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return false;
    }

    @Override
    public FluidVariant getResource() {
        return null;
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return 0;
    }

    @Override
    String getNetworkTitle() {
        return "Waterworks";
    }
}
