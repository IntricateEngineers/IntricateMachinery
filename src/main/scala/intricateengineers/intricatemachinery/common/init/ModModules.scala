package intricateengineers.intricatemachinery.common.init

import intricateengineers.intricatemachinery.api.module.Modules
import intricateengineers.intricatemachinery.common.module.{DummyModel, DummyModule, FurnaceModel, FurnaceModule}

object ModModules {

  // Register Modules
  def init(): Unit = {
    Modules.registerModule(DummyModel.Name, (frame) => new DummyModule(frame))
    Modules.registerModule(FurnaceModel.Name, (frame) => new FurnaceModule(frame))
  }
}
