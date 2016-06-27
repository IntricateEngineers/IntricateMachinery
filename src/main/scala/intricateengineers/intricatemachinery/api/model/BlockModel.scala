package intricateengineers.intricatemachinery.api.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract class BlockModel extends ModelBase {

  val bakedModel: IMBakedModel

}

trait IMBakedModel extends IBakedModel {

  def initTextures() : Unit

}
