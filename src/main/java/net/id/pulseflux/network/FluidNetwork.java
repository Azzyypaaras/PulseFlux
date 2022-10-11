package net.id.pulseflux.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.id.pulseflux.block.transport.FluidPipeBlock;
import net.id.pulseflux.block.transport.FluidPipeBlockEntity;
import net.id.pulseflux.util.FluidTextHelper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class FluidNetwork extends TransferNetwork<FluidNetwork, FluidVariant> implements SingleSlotStorage<FluidVariant> {

    protected FluidVariant fluid = FluidVariant.blank();
    protected long droplets;
    protected long pressure;
    protected long volumeThreshold;

    protected static final List<String> titles;

    public FluidNetwork(World world, UUID networkId) {
        super(world, networkId);
    }

    public FluidNetwork(World world, NbtCompound nbt) {
        super(world, nbt);
        fluid = FluidVariant.fromNbt(nbt.getCompound("fluidVariant"));
        droplets = nbt.getLong("droplets");
        pressure = nbt.getLong("pressure");
        volumeThreshold = nbt.getLong("volumeThreshold");
        recalculatePressure();
    }

    @Override
    void revalidateCapacity() {
        volumeThreshold = 0;
        for (BlockPos component : components) {
            var componentBlock = (FluidPipeBlock) world.getBlockState(component).getBlock();
            volumeThreshold += componentBlock.volume();
        }
        recalculatePressure();
    }

    public void recalculatePressure() {
        long dropsPastThreshold = droplets - volumeThreshold;

        if(dropsPastThreshold <= 0) {
            pressure = 0;
            return;
        }

        pressure = Math.round((Math.pow(dropsPastThreshold / FluidConstants.BUCKET, 1.75) * 20));
    }

    @Override
    void yieldTo(FluidNetwork network, NetworkManager manager) {
        manager.managedNetworks.remove(networkId);
        components.stream()
                .map(world::getBlockEntity)
                .filter(Objects::nonNull)
                .forEach(pipe -> {
                    ((FluidPipeBlockEntity) pipe).trySwitchNetwork(network, manager);
                    network.appendComponent(pipe.getPos());
                });
        if (network.isResourceBlank()) {
            network.fluid = this.fluid;
        }
        network.droplets += this.droplets;
        components.clear();
    }

    @Override
    void processDescendants(List<TransferNetwork<?, ?>> castme, NetworkManager manager) {

        if(components.size() <= 0 || castme.size() == 0)
            return;

        var networks = (List<FluidNetwork>) (Object) castme;
        if (networks.size() == 1) {
            var network = networks.get(0);
            name.ifPresent(network::setName);
            network.droplets = droplets;
            network.revalidateCapacity();
            return;
        }

        long size = 0;

        for (FluidNetwork network : networks) {
            size += network.components.size();
        }

        for (int i = 0; i < networks.size(); i++) {
            var network = networks.get(i);

            if (i == 0)
                name.ifPresent(network::setName);

            var comp = network.components.size();

            var percent = (double) size / comp;
            var splitDroplets = droplets / percent;
            network.droplets = (long) (i % 2 == 0 ? Math.floor(splitDroplets) : Math.ceil(splitDroplets));
            if (network.droplets > 0) {
                network.fluid = this.fluid;
            }
            network.revalidateCapacity();
        }
    }

    @Override
    void postAppend(BlockPos pos) {
        var componentBlock = (FluidPipeBlock) world.getBlockState(pos).getBlock();
        volumeThreshold += componentBlock.volume();
        recalculatePressure();
    }

    @Override
    public boolean isComponentValid(BlockPos pos, BlockState state) {
        return world.getBlockState(pos).getBlock() instanceof FluidPipeBlock && world.getBlockEntity(pos) instanceof FluidPipeBlockEntity;
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
                Text.literal(" "),
                Text.literal("Fluid Network " + name.orElse("https://azazelthedemonlord.newgrounds.com/")).setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("uuid - " + networkId).setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("fluid - " + this.getResource().getFluid().getDefaultState().getBlockState().getBlock().getName().getString()).setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("amount - " + FluidTextHelper.getUnicodeMillibuckets(droplets, true) + "ml").setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("pressure - " + pressure + "KPa").setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("size - " + getConnectedComponents()).setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal(" ")
        );
    }

    @Override
    public long insert(FluidVariant insertedFluid, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(insertedFluid, maxAmount);
        maxAmount = Math.min(maxAmount, Long.MAX_VALUE - droplets);

        if(maxAmount > 0 && (fluid.isBlank() || insertedFluid.equals(fluid))) {
            updateSnapshots(transaction);
            fluid = insertedFluid;
            droplets += maxAmount;

            recalculatePressure();
        }
        return maxAmount;
    }

    @Override
    public long extract(FluidVariant requestedFluid, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(requestedFluid, maxAmount);

        if(droplets > 0 && maxAmount > 0 && requestedFluid.equals(fluid)) {
            updateSnapshots(transaction);
            var extracted = maxAmount + Math.min(droplets - maxAmount, 0);
            droplets -= extracted;

            if(droplets == 0)
                fluid = FluidVariant.blank();

            recalculatePressure();
            return extracted;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return fluid.isBlank();
    }

    @Override
    public FluidVariant getResource() {
        return fluid;
    }

    @Override
    public long getAmount() {
        return droplets;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    String getNetworkTitle() {
        Collections.shuffle(titles);
        return titles.get(0);
    }

    static {
        titles = new ArrayList<>();
        titles.add("Waterworks");
        titles.add("Pipeworks");
        titles.add("Pipes");
        titles.add("Aquaduct");
        titles.add("Fluiduct");
        titles.add("Ducts");
        titles.add("Piping");
        titles.add("Plumbing");
        titles.add("Fluidway");
        titles.add("Culvert");
        titles.add("Canal");
        titles.add("Pipeline");
    }

    @Override
    public NbtCompound save(NbtCompound nbt) {
        nbt.put("fluidVariant", fluid.toNbt());
        nbt.putLong("droplets", droplets);
        nbt.putLong("pressure", pressure);
        nbt.putLong("volumeThreshold", volumeThreshold);
        return super.save(nbt);
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot() {
        return new ResourceAmount<>(fluid, droplets);
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
        fluid = snapshot.resource();
        droplets = snapshot.amount();
    }
}
