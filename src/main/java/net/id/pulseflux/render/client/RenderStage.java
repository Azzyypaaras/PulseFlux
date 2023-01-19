package net.id.pulseflux.render.client;

/**
 * @see net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
 */
public enum RenderStage {
    PRE_ENTITIES(true, false, false),
    POST_ENTITIES(true, false, false),
    PRE_OUTLINES(true, true, false),
    OUTLINES(true, false, true),
    TRANSLUCENT(true, false, false),
    LAST(true, false, false),
    NONE(false, false, false);

    public final boolean shouldRender, hitAvailable, outlineAvailable;

    RenderStage(boolean shouldRender, boolean hitAvailable, boolean outlineAvailable) {
        this.shouldRender = shouldRender;
        this.hitAvailable = hitAvailable;
        this.outlineAvailable = outlineAvailable;
    }

}
