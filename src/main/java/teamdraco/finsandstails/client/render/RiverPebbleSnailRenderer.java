
package teamdraco.finsandstails.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.finsandtails.geckolib3.renderers.geo.GeoEntityRenderer;
import teamdraco.finsandstails.client.model.RiverPebbleSnailModel;
import teamdraco.finsandstails.common.entities.RiverPebbleSnailEntity;

public class RiverPebbleSnailRenderer extends GeoEntityRenderer<RiverPebbleSnailEntity> {

    public RiverPebbleSnailRenderer(EntityRendererProvider.Context context) {
        super(context, new RiverPebbleSnailModel());
        this.shadowRadius = 0.2F;
    }
}