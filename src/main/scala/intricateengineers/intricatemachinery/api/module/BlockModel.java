package intricateengineers.intricatemachinery.api.module;

import net.minecraft.client.renderer.block.model.IBakedModel;

/**
 * A type of model that has a BakedModel associated with it.
 * Used primarily for blocks and multiparts
 */
public abstract class BlockModel extends ModelBase {

    private IMBakedModel bakedModel;

    public IMBakedModel getBakedModel() {
        if (this.bakedModel == null) {
            this.bakedModel = this.initBakedModel();
        }
        return this.bakedModel;
    }

    public abstract IMBakedModel initBakedModel();

    public static interface IMBakedModel extends IBakedModel {
        
        void initQuads();

        void initTextures();
   }
}
