package intricateengineers.intricatemachinery.common.module

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.model.ModuleModel
import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module, ModuleCompanion}
import intricateengineers.intricatemachinery.common.util.IMRL

class DummyModule(val parentFrame: MachineryFrame) extends {
  val model = DummyModel
  val name = DummyModule.Name
} with Module(parentFrame) {

}

object DummyModule extends ModuleCompanion {
  val Name = IMRL("dummy")
}

object DummyModel extends ModuleModel {

  private val texture = IMRL("blocks/dummy")

  define {
    |#|:(0, 0, 0)(8, 8, 8) {
      |*(texture, UV.fill)
    }
  }
}
