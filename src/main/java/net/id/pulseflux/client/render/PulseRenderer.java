package net.id.pulseflux.client.render;

import net.id.pulseflux.systems.Polarity;
import net.id.pulseflux.systems.PulseIo;
import net.id.pulseflux.systems.IoProvider;
import net.id.pulseflux.util.ColorHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

import static net.id.incubus_core.render.RenderHelper.*;

public class PulseRenderer<E extends BlockEntity & IoProvider & PulseIo> extends IoRenderer<E> {

    public PulseRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(E entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);

        final Polarity polarity = entity.getPolarity();

        if(polarity != Polarity.NONE) {
            entity.getChildren().forEach(child -> {
                if(child.isValid()) {
                    matrices.push();
                    drawLaser(vertexConsumers, matrices, ColorHelper.hexToVec(polarity.hex), 0.45F, child.distance() - 1, 0.15F, 0.5F, child.direction());
                    matrices.pop();
                    matrices.push();
                    drawLaser(vertexConsumers, matrices, ColorHelper.hexToVec(polarity.hex), 0.325F, child.distance() - 1, 0.25F, 0.5F, child.direction());
                    matrices.pop();
                }
            });
        }
    }
}
