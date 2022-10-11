package net.id.pulseflux.network;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.id.pulseflux.PulseFlux;
import net.id.pulseflux.block.transport.LogisticComponentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BiomeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static net.id.pulseflux.util.Shorthands.*;

public abstract class TransferNetwork<T extends TransferNetwork<T, V>, V extends TransferVariant<?>> extends SnapshotParticipant<ResourceAmount<V>> {

    public final UUID networkId;
    public final List<BlockPos> invalidComponents;

    protected final List<BlockPos> components;
    protected final World world;
    protected Optional<String> name = Optional.empty();

    private boolean revalidationCached;
    private boolean revalidationRequestTick;

    protected static final List<Pair<Float, String>> randomPrefixes;
    protected static final List<Pair<Float, String>> randomAffixes;
    protected static final List<String> people;
    //protected static final Map<Biome.Category, List<String>> biomePrefixes;
    protected static final List<String> deepPrefixPrefixes;


    public TransferNetwork(World world, UUID networkId) {
        this.world = world;
        this.networkId = networkId;
        this.invalidComponents = new ArrayList<>();
        this.components = new ArrayList<>();

        PulseFlux.LOG.info("Created a new transfer network of type " + this.getClass().getCanonicalName());
    }

    public TransferNetwork(World world, NbtCompound nbt) {
        this.world = world;
        this.networkId = nbt.getUuid("networkId");
        name = Optional.ofNullable(nbt.getString("name"));
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
                .filter(pos -> !isComponentValid(pos, world.getBlockState(pos)))
                .toList();
        
        if(!invalidatedComponents.isEmpty()) {
            for (BlockPos component : invalidatedComponents) {
                int adjacency = 0;

                for (Direction direction : Direction.values()) {
                    var offPos = component.offset(direction);
                    var offState = world.getBlockState(offPos);
                    if(offState.getBlock() instanceof LogisticComponentBlock logisticComponent) {
                        if(!components.contains(offPos))
                            continue;

                        if(!isComponentValid(offPos, offState) || !logisticComponent.isConnectedToComponent(world, offPos, direction.getOpposite()))
                            continue;

                        adjacency++;
                    }
                }

                if(adjacency > 1) {
                    invalidComponents.add(component);
                }

                components.remove(component);
            }
        }
    }

    public void  setName(@NotNull String newName) {
        name = Optional.of(newName);
    }

    public String getOrCreateName(@NotNull World world, @NotNull BlockPos pos) {
        var random = world.getRandom();
        var finalName = getNetworkTitle();
        var biome = world.getBiome(pos);
        boolean namedPerson = false, prefixed = false;
        var person = getPerson(random);

        if(random.nextBoolean()) {
            finalName = getPrefix(random, world.getBiome(pos), pos.getY(), world.isSkyVisible(pos)) + " " + finalName;
            prefixed = true;
        }

        getPerson: {
            if(random.nextFloat() < (prefixed ? 0.175 : 0.5)) {
                namedPerson = true;

                if(person.equals("Azazel's Own") || person.equals("§kCummy§r"))
                    break getPerson;

                if(person.charAt(person.length() - 1) == 's' || person.charAt(person.length() - 1) == 'z') {
                    person += "'";
                }
                else {
                    person += "'s";
                }

                finalName = person + " " + finalName;
            }
        }

        if(!prefixed || random.nextBoolean()) {
            if(!namedPerson || random.nextBoolean())
                finalName += getAffix(random);
        }

        return finalName;
    }

    abstract String getNetworkTitle();

    protected String getPrefix(Random random, RegistryEntry<Biome> biomeEntry, int y, boolean surface) {
        String out = "";
        var nether = biomeEntry.isIn(BiomeTags.IS_NETHER);

        if(random.nextFloat() < 0.334 && !surface &&  y < 100 && !nether) {
            Collections.shuffle(deepPrefixPrefixes);
            out = deepPrefixPrefixes.get(0) + " ";
        }

        //if(random.nextBoolean()) {
        //    var prefixes = biomePrefixes.get(category);
        //    Collections.shuffle(prefixes);
        //    out += prefixes.get(0);
        //    return out;
        //}
        //else {
            Collections.shuffle(randomPrefixes);
            for (Pair<Float, String> prefix : randomPrefixes) {
                if(random.nextFloat() < prefix.getLeft())
                    return out + prefix.getRight();
            }

            return out + randomPrefixes.get(randomPrefixes.size() - 1).getRight();
        //}
    }

    protected String getAffix(Random random) {
        Collections.shuffle(randomAffixes);
        for (Pair<Float, String> affix : randomAffixes) {
            if(random.nextFloat() < affix.getLeft())
                return affix.getRight();
        }
        return randomAffixes.get(randomAffixes.size() - 1).getRight();
    }

    protected String getPerson(Random random) {
        for (String person : people) {
            if (random.nextFloat() < 0.1F) {
                return person;
            }
        }
        return people.get(people.size() - 1);
    }

    public void clearAndIntegrate(T network) {
        components.clear();
        components.addAll(network.components);
        requestNetworkRevalidation();
    }

    abstract void revalidateCapacity();

    public void requestNetworkRevalidation() {
        revalidationCached = true;
        revalidationRequestTick = true;
    }

    abstract void yieldTo(T network, NetworkManager manager);

    abstract void processDescendants(List<TransferNetwork<?, ?>> networks, NetworkManager manager);

    public void appendComponent(BlockPos pos) {
        if(!components.contains(pos)) {
            components.add(pos);
            postAppend(pos);
        }
    }

    abstract void postAppend(BlockPos pos);

    public boolean containsComponent(BlockPos pos, BlockState component) {
        return isComponentValid(pos, component) && components.contains(pos);
    }

    public abstract boolean isComponentValid(BlockPos pos, BlockState state);

    public boolean isSameKind(TransferNetwork<?, ?> other) {
        return this.getClass() == other.getClass();
    }

    public boolean isComponentless() {
        return components.isEmpty();
    }

    public boolean removeIfEmpty() {
        return true;
    }

    abstract void postRemove();

    public int getConnectedComponents() {
        return components.size();
    }

    abstract NetworkReconstructor<T> getReconstructor();

    public NbtCompound save(NbtCompound nbt) {
        nbt.putUuid("networkId", networkId);
        name.ifPresent(str -> nbt.putString("name", str));
        nbt.putLongArray("components", components.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putLongArray("invalid", invalidComponents.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putBoolean("revalidating", revalidationCached);
        return nbt;
    }

    public abstract List<Text> getNetworkInfo();

    @Override
    public String toString() {
        LevelProperties properties = world.getLevelProperties() instanceof LevelProperties ? (LevelProperties) world.getLevelProperties() : null;
        //List<Chunk> chunks = components.stream().map(world::getChunk).distinct().toList();
        StringBuilder text = new StringBuilder("Transfer network: " + name + " - uuid: " + networkId.toString() + " - type: " + this.getClass().getCanonicalName() + "\nlevel: " + (properties == null ? "UNAVAILABLE" : properties.getLevelName()) + " - world: " + world.getRegistryKey().getValue().toString() + " - chunks: ");
        //chunks.forEach(chunk -> text.append(chunk.getPos().toString()).append(" "));
        return text.toString();
    }

    @FunctionalInterface
    public interface NetworkReconstructor<N extends TransferNetwork<N, ?>> {
        N assemble(World world, UUID networkId, NbtCompound nbt);
    }

    static {
        randomPrefixes = new ArrayList<>();
        randomPrefixes.add(new Pair<>(0.7F, "Quaint"));
        randomPrefixes.add(new Pair<>(0.7F, "Tiny"));
        randomPrefixes.add(new Pair<>(0.7F, "Workshop"));
        randomPrefixes.add(new Pair<>(0.7F, "Industrial"));
        randomPrefixes.add(new Pair<>(0.7F, "Immortal"));
        randomPrefixes.add(new Pair<>(0.6F, "24"));
        randomPrefixes.add(new Pair<>(0.5F, "Century"));
        randomPrefixes.add(new Pair<>(0.5F, "Ol'"));
        randomPrefixes.add(new Pair<>(0.5F, "Nutty"));
        randomPrefixes.add(new Pair<>(0.5F, "Grand"));
        randomPrefixes.add(new Pair<>(0.5F, "Silvered"));
        randomPrefixes.add(new Pair<>(0.5F, "Over-engineered"));
        randomPrefixes.add(new Pair<>(0.45F, "Royal"));
        randomPrefixes.add(new Pair<>(0.45F, "Black"));
        randomPrefixes.add(new Pair<>(0.45F, "White"));
        randomPrefixes.add(new Pair<>(0.45F, "Totally Ordinary"));
        randomPrefixes.add(new Pair<>(0.4F, "Professional Killstreak"));
        randomPrefixes.add(new Pair<>(0.4F, "Strange"));
        randomPrefixes.add(new Pair<>(0.3F, "Sprawling"));
        randomPrefixes.add(new Pair<>(0.3F, "Ungodly"));
        randomPrefixes.add(new Pair<>(0.3F, "Slutty"));
        randomPrefixes.add(new Pair<>(0.3F, "Lewd"));
        randomPrefixes.add(new Pair<>(0.3F, "Godly"));
        randomPrefixes.add(new Pair<>(0.3F, "Ass"));
        randomPrefixes.add(new Pair<>(0.25F, "Golden"));
        randomPrefixes.add(new Pair<>(0.25F, "Grilled"));
        randomPrefixes.add(new Pair<>(0.25F, "Valkyrie"));
        randomPrefixes.add(new Pair<>(0.25F, "Abyssal"));
        randomPrefixes.add(new Pair<>(0.25F, "Hot Mess of a"));
        randomPrefixes.add(new Pair<>(0.25F, "Unusual"));
        randomPrefixes.add(new Pair<>(0.25F, "Horned"));
        randomPrefixes.add(new Pair<>(0.25F, "Horny"));
        randomPrefixes.add(new Pair<>(0.25F, "Ivory"));
        randomPrefixes.add(new Pair<>(0.25F, "Ebony"));
        randomPrefixes.add(new Pair<>(0.2F, "Envious"));
        randomPrefixes.add(new Pair<>(0.2F, "Spunked"));
        randomPrefixes.add(new Pair<>(0.2F, "Burning Team Captain"));
        randomPrefixes.add(new Pair<>(0.2F, "Terra"));
        randomPrefixes.add(new Pair<>(0.2F, "Moist"));
        randomPrefixes.add(new Pair<>(0.2F, "Murky"));
        randomPrefixes.add(new Pair<>(0.2F, "Gay"));
        randomPrefixes.add(new Pair<>(0.2F, "Prideful"));
        randomPrefixes.add(new Pair<>(0.2F, "Lean Filled"));
        randomPrefixes.add(new Pair<>(0.2F, "Stuffed"));
        randomPrefixes.add(new Pair<>(0.2F, "Fucked"));
        randomPrefixes.add(new Pair<>(0.2F, "Fucked Up"));
        randomPrefixes.add(new Pair<>(0.15F, "Gregged"));
        randomPrefixes.add(new Pair<>(0.15F, "Maiden"));
        randomPrefixes.add(new Pair<>(0.15F, "Tran gener"));
        randomPrefixes.add(new Pair<>(0.15F, "Pent-up"));
        randomPrefixes.add(new Pair<>(0.15F, "Femboy"));
        randomPrefixes.add(new Pair<>(0.15F, "Tomboy"));
        randomPrefixes.add(new Pair<>(0.1F, "Dripping"));
        randomPrefixes.add(new Pair<>(0.1F, "Dribbling"));
        randomPrefixes.add(new Pair<>(0.1F, "Hotrod"));
        randomPrefixes.add(new Pair<>(0.1F, "Delirious"));
        randomPrefixes.add(new Pair<>(0.1F, "Cum-stained"));
        randomPrefixes.add(new Pair<>(0.1F, "Delirious"));
        randomPrefixes.add(new Pair<>(0.1F, "Moaning"));
        randomPrefixes.add(new Pair<>(0.1F, "Fingered"));
        randomPrefixes.add(new Pair<>(0.1F, "Fingered!"));
        randomPrefixes.add(new Pair<>(0.1F, "Bonered"));
        randomPrefixes.add(new Pair<>(0.1F, "Forgotten"));
        randomPrefixes.add(new Pair<>(0.1F, "Forgoren"));
        randomPrefixes.add(new Pair<>(0.05F, "Huh? That was weird..."));
        randomPrefixes.add(new Pair<>(0.05F, "Arch"));
        randomPrefixes.add(new Pair<>(0.05F, "Strawberry"));
        randomPrefixes.add(new Pair<>(0.05F, "Normal"));
        randomPrefixes.add(new Pair<>(0.05F, "Huh?"));
        randomPrefixes.add(new Pair<>(0.05F, "Ubercharged"));
        randomPrefixes.add(new Pair<>(0.05F, "Lovecraftian"));
        randomPrefixes.add(new Pair<>(0.025F, "..."));
        randomPrefixes.add(new Pair<>(0.025F, "???"));
        randomPrefixes.add(new Pair<>(0.025F, "!!!"));
        randomPrefixes.add(new Pair<>(0.025F, "What are you looking at?"));
        randomPrefixes.add(new Pair<>(0.025F, "Why are you reading this"));
        randomPrefixes.add(new Pair<>(0.025F, "Update TF2"));
        randomPrefixes.add(new Pair<>(0.025F, "You have no right."));
        randomPrefixes.add(new Pair<>(0.025F, "Does Not Open From This Side"));
        randomPrefixes.add(new Pair<>(0.01F, "Delirium with Fat Fucking Tits"));
        randomPrefixes.add(new Pair<>(0.01F, "High Incubus"));

        //var prefixBuilder = ImmutableMap.<TagKey<Biome>, List<String>>builder();
        //prefixBuilder.put(Biome.Category.NONE, list("Odd", "Suspicious", "Wayward", "Estranged"));
        //prefixBuilder.put(Biome.Category.TAIGA, list("Wooded", "Frosty", "Coniferous", "Spiky", "Jolly", "Boreal", "Wintry"));
        //prefixBuilder.put(Biome.Category.EXTREME_HILLS, list("Mountanious", "Hill", "Crags", "Mound", "Rocky", "Hilltop"));
        //prefixBuilder.put(Biome.Category.JUNGLE, list("Tropical", "Monsoon", "Rainforest", "Overgrown", "Greenbound", "Lush", "Melon"));
        //prefixBuilder.put(Biome.Category.MESA, list("Western", "Mesa", "Baked", "Heat-struck", "Scorching", "Terracotta", "Plateau"));
        //prefixBuilder.put(Biome.Category.PLAINS, list("Flat", "Floodplain", "Praire", "Grassy", "Plains"));
        //prefixBuilder.put(Biome.Category.SAVANNA, list("Dry", "Subtropical", "Arid", "Rainless", "Acacia"));
        //prefixBuilder.put(Biome.Category.ICY, list("Shivering", "Pale", "Icy", "Frosty", "Snowy", "Cold"));
        //prefixBuilder.put(Biome.Category.THEEND, list("Ender", "Resonant", "Void", "Null", "Finis", "Chorus"));
        //prefixBuilder.put(Biome.Category.BEACH, list("Wayward", "Breezeful", "Coastal", "Coast", "Beach"));
        //prefixBuilder.put(Biome.Category.FOREST, list("Wooded", "Forest", "Grove", "Glade", "Thicket", "Leafy"));
        //prefixBuilder.put(Biome.Category.OCEAN, list("Oceanic", "Transatlantic", "Briny", "Blue", "Seasick", "Salty", "Sailing", "Ocean", "Sea"));
        //prefixBuilder.put(Biome.Category.DESERT, list("Dune", "Sandy", "Desert", "Blistering", "Dusty", "Cactus", "Bazaar"));
        //prefixBuilder.put(Biome.Category.RIVER, list("Stream", "Cool", "Wet", "River", "Creek", "Brook"));
        //prefixBuilder.put(Biome.Category.SWAMP, list("Mangrove", "Murky", "Muddy", "Cattail", "Brackish", "Bog"));
        //prefixBuilder.put(Biome.Category.MUSHROOM, list("Wooded", "Forest", "Grove", "Glade", "Thicket", "Leafy"));
        //prefixBuilder.put(Biome.Category.NETHER, list("Arduous", "Sintering", "Hellish", "Demon", "Succubus", "Incubus", "Blazing", "Sulfur", "Piglin", "Wither", "Hearth"));
        //prefixBuilder.put(Biome.Category.UNDERGROUND, list("Cavernous", "Cave", "Mithraeum", "Marble Forest", "Deep", "Subterranean", "Dripstone", "Mossy", "Echoing", "Depths", "Dwarven", "Tunnelling", "Bedrock", "Diamond", "Malachite", "Azurite", "Limonite", "Steel", "Gravel"));
        //prefixBuilder.put(Biome.Category.MOUNTAIN, list("Celeste", "High", "Altitude", "Peak", "Hewn", "Frozen", "Mountainous", "Range"));
        //biomePrefixes = prefixBuilder.build();

        deepPrefixPrefixes = list(
                "Deep",
                "Subterranean",
                "Under",
                "Umbral",
                "Sunless",
                "Cavernous",
                "Stone",
                "Iron",
                "Dwarf"
        );

        randomAffixes = new ArrayList<>();
        randomAffixes.add(new Pair<>(0.7F, " Sal"));
        randomAffixes.add(new Pair<>(0.7F, " Imibis"));
        randomAffixes.add(new Pair<>(0.7F, " Stal"));
        randomAffixes.add(new Pair<>(0.7F, " of Path"));
        randomAffixes.add(new Pair<>(0.7F, ", Overengineered"));
        randomAffixes.add(new Pair<>(0.7F, " Belfry"));
        randomAffixes.add(new Pair<>(0.6F, " Luna"));
        randomAffixes.add(new Pair<>(0.6F, " Sol"));
        randomAffixes.add(new Pair<>(0.5F, " Bell"));
        randomAffixes.add(new Pair<>(0.5F, " o' Gold"));
        randomAffixes.add(new Pair<>(0.5F, " of Harpies"));
        randomAffixes.add(new Pair<>(0.5F, " Alto"));
        randomAffixes.add(new Pair<>(0.5F, " o' Steel"));
        randomAffixes.add(new Pair<>(0.5F, " o' Bronze"));
        randomAffixes.add(new Pair<>(0.5F, " o' Iron"));
        randomAffixes.add(new Pair<>(0.5F, ", hot hot so hot it could fry an egg"));
        randomAffixes.add(new Pair<>(0.5F, " o' Drakeblood"));
        randomAffixes.add(new Pair<>(0.5F, " of Brillyg, and the Slythy Toves"));
        randomAffixes.add(new Pair<>(0.45F, " of Bloodstone"));
        randomAffixes.add(new Pair<>(0.45F, " of Titanite"));
        randomAffixes.add(new Pair<>(0.3F, " Seh"));
        randomAffixes.add(new Pair<>(0.3F, " of Dragons"));
        randomAffixes.add(new Pair<>(0.3F, " of Gods"));
        randomAffixes.add(new Pair<>(0.3F, " of Demons"));
        randomAffixes.add(new Pair<>(0.3F, " in White"));
        randomAffixes.add(new Pair<>(0.3F, " in Black"));
        randomAffixes.add(new Pair<>(0.25F, " of the Incubus"));
        randomAffixes.add(new Pair<>(0.25F, " approaches the Roche limit"));
        randomAffixes.add(new Pair<>(0.2F, " of the Scourge"));
        randomAffixes.add(new Pair<>(0.2F, " the Redmane"));
        randomAffixes.add(new Pair<>(0.2F, " the Vileblood"));
        randomAffixes.add(new Pair<>(0.2F, " the Dreiton"));
        randomAffixes.add(new Pair<>(0.2F, ", Grace-given"));
        randomAffixes.add(new Pair<>(0.2F, ", Omen"));
        randomAffixes.add(new Pair<>(0.2F, ", Forgiven"));
        randomAffixes.add(new Pair<>(0.15F, ", Lost"));
        randomAffixes.add(new Pair<>(0.15F, ", the Last"));
        randomAffixes.add(new Pair<>(0.15F, " Cinder"));
        randomAffixes.add(new Pair<>(0.15F, " Curse"));
        randomAffixes.add(new Pair<>(0.15F, " Old Blood"));
        randomAffixes.add(new Pair<>(0.1F, " of the vile Jabberwocky"));
        randomAffixes.add(new Pair<>(0.1F, " o' you don't have the right, therefore you don't have the right o' you don't have the right"));
        randomAffixes.add(new Pair<>(0.05F, " by Azazelthedemonlord"));


        people = new ArrayList<>();
        people.add("Jeb");
        people.add("Bdogz");
        people.add("Kalucky");
        people.add("Eir");
        people.add("Gwyn");
        people.add("Larry");
        people.add("Avelyn");
        people.add("Jack Papel");
        people.add("Jappa");
        people.add("Sunsette");
        people.add("Oz");
        people.add("CDA");
        people.add("Isaac");
        people.add("Gud");
        people.add("Neco Arc");
        people.add("Daniel");
        people.add("Red");
        people.add("Breadmund");
        people.add("Minus8");
        people.add("Player");
        people.add("Engineer Gaming");
        people.add("Dafuqs");
        people.add("ReikaKalseki");
        people.add("Deez");
        people.add("Gherman");
        people.add("Crap-Man");
        people.add("Raugrior");
        people.add("§kCummy§r");
        people.add("Lorian");
        people.add("Phoneph");
        people.add("The Crow");
        people.add("Sans Undertale");
        people.add("Azazel's Own");
    }
}
