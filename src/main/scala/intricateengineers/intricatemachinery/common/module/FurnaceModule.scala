package intricateengineers.intricatemachinery.common.module

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.model.ModuleModel
import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module}
import intricateengineers.intricatemachinery.common.util.IMRL
import net.minecraft.util.EnumFacing._

class FurnaceModule(val parentFrame: MachineryFrame) extends {
  val model = FurnaceModel
  val name = FurnaceModel.Name
} with Module(parentFrame) {

}

object FurnaceModel extends ModuleModel {

  val Name = IMRL("furnace")

  val topTexture = IMRL("blocks/furnace_top")
  val sideTexture = IMRL("blocks/furnace_side")
  val frontTexture = IMRL("blocks/furnace_front_on")
  val frameTexture = IMRL("blocks/furnace_top")

  define {
    |#|:(1, 1, 1)(5, 5, 5) {
      |-(NORTH, sideTexture, UV.fill)
      |-(EAST, sideTexture, UV.fill)
      |-(SOUTH, frontTexture, UV.fill)
      |-(WEST, sideTexture, UV.fill)
      |-(UP, topTexture, UV.fill)
      |-(DOWN, topTexture, UV.fill)
    }
    |#|:(0, 0, 0)(1, 6, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(0, 0, 5)(1, 6, 6) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 0, 0)(6, 6, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 0, 5)(6, 6, 6) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 0, 0)(5, 1, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 5, 0)(5, 6, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(0, 0, 1)(1, 1, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(0, 5, 1)(1, 6, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 0, 1)(6, 1, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 5, 1)(6, 6, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 0, 5)(5, 1, 6) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 5, 5)(5, 6, 6) {
      |*(frameTexture, UV.auto(6))
    }
  }
}
