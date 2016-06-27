package intricateengineers.intricatemachinery.common.module

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.model.ModuleModel
import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module}
import intricateengineers.intricatemachinery.common.util.IMRL

class DummyModule(val parentFrame: MachineryFrame) extends {
  val model = DummyModel
  val name = DummyModel.Name
} with Module(parentFrame) {

}

object DummyModel extends ModuleModel {

  val Name = IMRL("dummy")

  private val texture = IMRL("blocks/dummy")

  define {
    |#|:(0, 0, 0)(8, 8, 8) {
      |*(texture, UV.fill)
    }
  }
}
