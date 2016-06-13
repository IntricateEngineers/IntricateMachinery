package intricateengineers.intricatemachinery.api.model

import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.fml.relauncher.Side
import intricateengineers.intricatemachinery.api.client.QuadHandler

abstract class ModuleModel extends ModelBase {

  @SideOnly(Side.CLIENT)
  lazy val quadHandler = new QuadHandler(this)

}
