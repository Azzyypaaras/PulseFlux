package net.id.pulseflux.item.debug;

import net.id.pulseflux.block.transport.LogisticComponentBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

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

        if(player == null)
            return super.useOnBlock(context);

        if(!(state.getBlock() instanceof LogisticComponentBlock<?> component))
            return super.useOnBlock(context);

        var parentNetwork = component.getParentNetwork(world, pos);

        parentNetwork.ifPresent(network -> network.getNetworkInfo().forEach(text -> player.sendMessage(text, false)));

        world.playSound(context.getPlayer(), pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1 + world.getRandom().nextFloat() * 0.2F);
        return ActionResult.success(world.isClient);
    }
}
