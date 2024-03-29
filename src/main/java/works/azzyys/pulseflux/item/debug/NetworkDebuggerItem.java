package works.azzyys.pulseflux.item.debug;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.joml.Vector3f;
import works.azzyys.pulseflux.block.transport.LogisticComponentBlock;
import works.azzyys.pulseflux.network.FluidNetwork;
import works.azzyys.pulseflux.packets.PulseFluxPackets;
import works.azzyys.pulseflux.util.FluidTextHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import works.azzyys.pulseflux.util.Shorthands;

import java.util.UUID;

public class NetworkDebuggerItem extends Item {

    public NetworkDebuggerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var player = context.getPlayer();
        var pos = context.getBlockPos();
        var world = context.getWorld();
        var state = world.getBlockState(pos);
        var storage = FluidStorage.SIDED.find(world, pos, context.getSide());

        if (player == null || world.isClient())
            return super.useOnBlock(context);

        if (state.getBlock() instanceof LogisticComponentBlock<?> component) {
            var parentNetwork = component.getParentNetwork(world, pos);

            if (parentNetwork.isPresent() && parentNetwork.get() instanceof FluidNetwork fluidNetwork) {
                var buf = PacketByteBufs.create();
                buf.writeUuid(fluidNetwork.networkId);
                Shorthands.vecToPacket(pos.toCenterPos().toVector3f().add(0, 0.5F, 0), buf);
                buf.writeRegistryKey(world.getRegistryKey());
                buf.writeUuid(UUID.randomUUID());

                ServerPlayNetworking.send((ServerPlayerEntity) player, PulseFluxPackets.CREATE_NETWORK_IUI, buf);
            }
            else {
                parentNetwork.ifPresent(network -> network.getNetworkInfo().forEach(text -> player.sendMessage(text, false)));
            }

            world.playSound(context.getPlayer(), pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1 + world.getRandom().nextFloat() * 0.2F);
            return ActionResult.success(world.isClient);
        }
        else if(storage instanceof SingleVariantStorage<FluidVariant> tank) {
            player.sendMessage(Text.literal(" "));
            player.sendMessage(state.getBlock().getName());
            player.sendMessage(Text.literal("fluid - " + tank.getResource().getFluid().getDefaultState().getBlockState().getBlock().getName().getString()));
            player.sendMessage(Text.literal("amount - " + FluidTextHelper.getUnicodeMillibuckets(tank.getAmount(), true) + " ml"));
            player.sendMessage(Text.literal("raw amount - " + tank.getAmount() + " drrrrroplets"));
            player.sendMessage(Text.literal(" "));
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            if (!world.isClient())
                ServerPlayNetworking.send((ServerPlayerEntity) user, PulseFluxPackets.DISMISS_ALL_IUI, PacketByteBufs.create());
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }
}
