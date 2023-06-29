package works.azzyys.pulseflux.automata;

public abstract class Automaton {

    protected boolean configured = false;

    abstract void tick();

    abstract boolean canTick();

    /**
     * CALL ME
     */
    protected void markConfigured() {
        configured = true;
    }

    public boolean hasBeenConfigured() {
        return configured;
    }
}
