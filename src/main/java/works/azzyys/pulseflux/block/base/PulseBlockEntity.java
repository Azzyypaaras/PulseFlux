package works.azzyys.pulseflux.block.base;

import net.id.incubus_core.be.IncubusLazyBlockEntity;
import net.id.incubus_core.systems.Material;
import net.id.incubus_core.systems.Simulation;
import works.azzyys.pulseflux.systems.Polarity;
import works.azzyys.pulseflux.systems.PulseIo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public abstract class PulseBlockEntity extends IncubusLazyBlockEntity implements PulseIo {

    protected final Material material;
    @NotNull protected Polarity polarity = Polarity.NONE;
    protected long frequency, inductance;
    protected double dissonance;

    public PulseBlockEntity(BlockEntityType<?> type, Material material, BlockPos pos, BlockState state, int tickSpacing) {
        super(type, pos, state, tickSpacing);
        this.material = material;
    }

    public PulseBlockEntity(BlockEntityType<?> type, Material material, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.material = material;
    }

    @Override
    public long getFrequency() {
        return frequency;
    }

    @Override
    public long getInductance() {
        return inductance;
    }

    @Override
    public double getDissonance() {
        return dissonance;
    }

    @NotNull
    @Override
    public Polarity getPolarity() {
        return polarity;
    }

    @Override
    public Material getMaterial(Direction direction) {
        return material;
    }

    @Override
    public long getFailureDissonance() {
        return 100;
    }

    @Override
    public long transferFrequency(long amount, Simulation simulation) {
        if(simulation.isActing()) {
            frequency = amount;
        }
        return 0;
    }

    @Override
    public long transferInductance(long amount, Simulation simulation) {
        if(simulation.isActing()) {
            inductance = amount;
        }
        return 0;
    }

    @Override
    public void setPolarity(@NotNull Polarity polarity) {
        this.polarity = polarity;
    }

    @Override
    public void load(NbtCompound nbt) {
        super.load(nbt);
        polarity = Polarity.valueOf(nbt.getString("polarity"));
        frequency = nbt.getLong("frequency");
        inductance = nbt.getLong("inductance");
        dissonance = nbt.getDouble("dissonance");
    }

    @Override
    public void save(NbtCompound nbt) {
        super.save(nbt);
        nbt.putString("polarity", polarity.name());
        nbt.putLong("frequency", frequency);
        nbt.putLong("inductance", inductance);
        nbt.putDouble("dissonance", dissonance);
    }

    @Override
    public void loadClient(NbtCompound nbt) {
        load(nbt);
    }

    @Override
    public void saveClient(NbtCompound nbt) {
        save(nbt);
    }
}
