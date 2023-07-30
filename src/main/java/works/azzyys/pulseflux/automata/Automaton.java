package works.azzyys.pulseflux.automata;

import net.minecraft.world.World;

public abstract class Automaton {

    protected final World world;

    public Automaton(World world) {
        this.world = world;
    }

    abstract void tick();

    public boolean canTick() {
        return true;
    }
}
