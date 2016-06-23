package intricateengineers.intricatemachinery.api.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract class BlockModel extends ModelBase {

  @SideOnly(Side.CLIENT)
  val bakedModel: IMBakedModel

}

trait IMBakedModel extends IBakedModel {

  def initTextures() : Unit

}
