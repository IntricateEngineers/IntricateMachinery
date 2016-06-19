package intricateengineers.intricatemachinery.api.model

import net.minecraft.client.renderer.block.model.IBakedModel

abstract class BlockModel extends ModelBase {
  
  lazy val bakedModel = this.initBakedModel

  def initBakedModel : IMBakedModel

}

trait IMBakedModel extends IBakedModel {

  def initQuads() : Unit

  def initTextures() : Unit

}
