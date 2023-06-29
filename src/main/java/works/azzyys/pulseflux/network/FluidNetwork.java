package works.azzyys.pulseflux.network;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import works.azzyys.pulseflux.block.transport.FluidPipeBlock;
import works.azzyys.pulseflux.block.transport.FluidPipeBlockEntity;
import works.azzyys.pulseflux.systems.energy.PressureHolder;
import works.azzyys.pulseflux.util.FluidTextHelper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class FluidNetwork extends TransferNetwork<FluidNetwork> implements PressureHolder {

    public final NetworkTank internalTank = new NetworkTank();
    protected long pressure;
    protected long volumeThreshold;

    protected static final List<String> titles;

    public FluidNetwork(World world, UUID networkId) {
        super(world, networkId);
    }

    public FluidNetwork(World world, NbtCompound nbt) {
        super(world, nbt);
        internalTank.variant = FluidVariant.fromNbt(nbt.getCompound("fluidVariant"));
        internalTank.amount = nbt.getLong("droplets");
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
        long dropsPastThreshold = getDroplets() - volumeThreshold;

        if(dropsPastThreshold <= 0) {
            pressure = 0;
            return;
        }

        pressure = Math.round((Math.pow(dropsPastThreshold / FluidConstants.BUCKET, 1.75) * 20));
    }

    @Override
    public long queryPressure() {
        return pressure;
    }

    @Override
    public void yieldTo(FluidNetwork network, NetworkManager manager) {
        manager.managedNetworks.remove(networkId);
        components.stream()
                .map(world::getBlockEntity)
                .filter(Objects::nonNull)
                .forEach(pipe -> {
                    ((FluidPipeBlockEntity) pipe).trySwitchNetwork(network, manager);
                    network.appendComponent(pipe.getPos());
                });
        if (network.isFluidBlank()) {
            network.setFluid(this.getFluid());
        }
        network.setDroplets(network.getDroplets() + this.getDroplets());
        components.clear();
    }

    @Override
    void processDescendants(List<TransferNetwork<?>> castme, NetworkManager manager) {

        castme = castme.stream().filter(network -> network.components.size() > 0).collect(Collectors.toList());

        if(components.size() == 0 || castme.size() == 0)
            return;

        var networks = (List<FluidNetwork>) (Object) castme;
        if (networks.size() == 1) {
            var network = networks.get(0);
            name.ifPresent(network::setName);
            network.setDroplets(this.getDroplets());
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
            var splitDroplets = getDroplets() / percent;
            network.setDroplets((long) (i % 2 == 0 ? Math.floor(splitDroplets) : Math.ceil(splitDroplets)));
            if (network.getDroplets() > 0) {
                network.setFluid(this.getFluid());
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
                Text.literal("fluid - " + this.getFluid().getFluid().getDefaultState().getBlockState().getBlock().getName().getString()).setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("amount - " + FluidTextHelper.getUnicodeMillibuckets(getDroplets(), true) + "ml").setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("pressure - " + pressure + "KPa").setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal("size - " + getConnectedComponents()).setStyle(Style.EMPTY.withColor(0xffb41f)),
                Text.literal(" ")
        );
    }

    public long insert(FluidVariant insertedFluid, long maxAmount, TransactionContext transaction) {
        return internalTank.insert(insertedFluid, maxAmount, transaction);
    }

    public long extract(FluidVariant requestedFluid, long maxAmount, TransactionContext transaction) {
        return internalTank.extract(requestedFluid, maxAmount, transaction);
    }

    public long getDroplets() {
        return internalTank.amount;
    }

    public void setDroplets(long amount) {
        internalTank.amount = amount;
    }

    public boolean isFluidBlank() {
        return internalTank.isResourceBlank();
    }

    public FluidVariant getFluid() {
        return internalTank.getResource();
    }

    public void setFluid(FluidVariant fluid) {
        internalTank.variant = fluid;
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
    public NbtCompound save(NbtCompound nbt, boolean soft) {
        nbt.put("fluidVariant", getFluid().toNbt());
        nbt.putLong("droplets", getDroplets());
        nbt.putLong("pressure", pressure);
        nbt.putLong("volumeThreshold", volumeThreshold);
        return super.save(nbt, soft);
    }

    @Override
    public void softSync(NbtCompound nbt) {
        super.softSync(nbt);
        internalTank.variant = FluidVariant.fromNbt(nbt.getCompound("fluidVariant"));
        internalTank.amount = nbt.getLong("droplets");
        pressure = nbt.getLong("pressure");
        volumeThreshold = nbt.getLong("volumeThreshold");
    }

    public class NetworkTank extends SingleVariantStorage<FluidVariant> {

        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        public long insert(FluidVariant insertedFluid, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedFluid, maxAmount);
            maxAmount = Math.min(maxAmount, Long.MAX_VALUE - amount);

            if (maxAmount > 0 && (variant.isBlank() || insertedFluid.equals(variant))) {
                updateSnapshots(transaction);
                variant = insertedFluid;
                amount += maxAmount;

                recalculatePressure();
            }
            return maxAmount;
        }

        @Override
        public long extract(FluidVariant requestedFluid, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(requestedFluid, maxAmount);

            if(amount > 0 && maxAmount > 0 && requestedFluid.equals(variant)) {
                updateSnapshots(transaction);
                var extracted = maxAmount + Math.min(amount - maxAmount, 0);
                amount -= extracted;

                if(amount == 0)
                    variant = FluidVariant.blank();

                recalculatePressure();
                return extracted;
            }

            return 0;
        }

        @Override
        public ResourceAmount<FluidVariant> createSnapshot() {
            return super.createSnapshot();
        }

        @Override
        public void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
            super.readSnapshot(snapshot);
            recalculatePressure();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return Long.MAX_VALUE;
        }
    }
}
