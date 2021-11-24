package net.id.pulseflux.client.render;

import net.id.pulseflux.systems.IoProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import static net.id.pulseflux.systems.IoProvider.Type.*;
import static net.id.incubus_core.render.RenderHelper.*;

public class IoRenderer<E extends BlockEntity & IoProvider> implements BlockEntityRenderer<E> {

    public IoRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(E entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var type = PULSE;
        var mixed = entity.getMixed(type);

        float transMult = Math.min(1 - ((float) entity.getRenderProgress() / entity.getMaxRenderProgress()) - (tickDelta / entity.getMaxRenderProgress()), 0.8F);

        if(transMult > 0.01) {
            entity.getInputs(type)
                    .stream()
                    .filter(direction -> !mixed.contains(direction))
                    .forEach(direction -> renderIo(matrices, vertexConsumers, direction, type.inputColor, transMult, type.scale));

            entity.getOutputs(type)
                    .stream()
                    .filter(direction -> !mixed.contains(direction))
                    .forEach(direction -> renderIo(matrices, vertexConsumers, direction, type.outputColor, transMult, type.scale));

            entity.getMixed(type)
                    .forEach(direction -> renderIo(matrices, vertexConsumers, direction, type.inputColor, transMult, type.scale));
        }
    }

    private void renderIo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Direction direction, Vec3f color, float transMult, float scale) {

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        directionalMatrixMultiply(matrices, direction);
        matrices.translate(-0.5, -0.5, -0.5);
        matrices.translate(0, 0, 1);

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(scale, scale, scale);
        matrices.translate(-0.5, -0.5, -0.5);

        drawEmissiveCube(vertexConsumers, matrices, color, 0.35F * transMult);

        var secScale = 0.925F;

        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(secScale, secScale, secScale);
        matrices.translate(-0.5, -0.5, -0.5);

        drawEmissiveCube(vertexConsumers, matrices, color, 0.8F * transMult);
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(E blockEntity) {
        return true;
    }
}
