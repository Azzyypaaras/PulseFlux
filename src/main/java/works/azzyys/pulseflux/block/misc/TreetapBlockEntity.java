package works.azzyys.pulseflux.block.misc;

import net.id.incubus_core.be.IncubusLazyBlockEntity;
import works.azzyys.pulseflux.block.PulseFluxBlockEntities;
import works.azzyys.pulseflux.util.transfer.SingleFluidStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class TreetapBlockEntity extends IncubusLazyBlockEntity {

    private final SingleFluidStorage tank = new SingleFluidStorage();

    public TreetapBlockEntity(BlockPos pos, BlockState state) {
        super(PulseFluxBlockEntities.TREETAP_TYPE, pos, state, 10);
    }

    @Override
    protected void tick(BlockPos pos, BlockState state) {
        if(allowTick()) {

        }
    }

    @Override
    public void save(NbtCompound nbt) {
        super.save(nbt);
        tank.save(nbt);
    }

    @Override
    public void load(NbtCompound nbt) {
        super.load(nbt);
        tank.load(nbt);
    }

    @Override
    public void saveClient(NbtCompound nbt) {
        save(nbt);
    }

    @Override
    public void loadClient(NbtCompound nbt) {
        load(nbt);
    }

    public SingleFluidStorage getTank() {
        return tank;
    }
}
