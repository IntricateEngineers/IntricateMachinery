package intricateengineers.intricatemachinery.common.init

import intricateengineers.intricatemachinery.api.module.Modules
import intricateengineers.intricatemachinery.common.module.{DummyModule, FurnaceModule}

object ModModules {

  // Register Modules
  def init(): Unit = {
    Modules.registerModule(DummyModule, new DummyModule(_))
    Modules.registerModule(FurnaceModule, new FurnaceModule(_))
  }
}
