package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.{IMBakedModel, ModelBase}
import net.minecraft.client.renderer.block.model.IBakedModel

object BlockModel {

  trait IMBakedModel extends IBakedModel {
    def initQuads(): Unit

    def initTextures(): Unit
  }

}

abstract class BlockModel extends ModelBase {
  private var bakedModel: IMBakedModel = null

  def getBakedModel: IMBakedModel = {
    if (this.bakedModel == null) {
      this.bakedModel = this.initBakedModel
    }
    this.bakedModel
  }

  def initBakedModel: IMBakedModel
}