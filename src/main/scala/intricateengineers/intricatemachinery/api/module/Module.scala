package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.{Box, ModuleModel}
import intricateengineers.intricatemachinery.api.util.{Cache, IHasDebugInfo}
import main.scala.intricateengineers.intricatemachinery.api.module.ModuleCapability
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
import scala.util.Random

abstract class Module(frame: MachineryFrame) extends ICapabilitySerializable[NBTTagCompound]
  with IHasDebugInfo {

  val name: ResourceLocation
  val model: ModuleModel
  val boxCache: Cache[List[Box]] = Cache(() => model.boxes.map(_.offset(pos.ints)))
  val bbCache: Cache[List[AxisAlignedBB]] = Cache(() => boxCache().map(_.aabb))
  // Temporary hardcoded values
  private var _pos: ModulePos = ModulePos(0, 0, 0)
  private var _rotation: Byte = Random.nextInt(3).toByte

  val capabilities = new Traversable[ModuleCapability] {
    private val listBuf = ListBuffer[ModuleCapability]()

    def += (cap: ModuleCapability): Unit = {
      listBuf += cap
      frame.modules.invalidate()
    }

    def -= (cap: ModuleCapability): Unit = {
      listBuf -= cap
      frame.modules.invalidate()
    }

    override def toList(): List[ModuleCapability] = listBuf.toList

    override def foreach[U](f: (ModuleCapability) => U) = listBuf.foreach(f)
  }

  /* ------======================------
           START Getters/Setters
     ------======================------ */

  def pos: ModulePos = _pos

  def pos_=(newPos: ModulePos): Unit = {
    _pos = newPos
    boxCache.invalidate()
    bbCache.invalidate()
    debugInfo.invalidate()
  }

  def rotation = _rotation

  def rotation_=(rotation: Byte): Unit = {
    _rotation = rotation
    boxCache.invalidate()
    bbCache.invalidate()
    debugInfo.invalidate()
  }

  /* ------======================------
           END Getters/Setters
     ------======================------ */

  def invalidateAllCaches(): Unit = {
    boxCache.invalidate()
    bbCache.invalidate()
    debugInfo.invalidate()
  }

  /* ------======================------
                 OVERRIDES
     ------======================------ */

  override def updateDebugInfo(): ListMap[String, String] = {
    ListMap[String, String](
      "Type" -> name.toString,
      "posX" -> pos.iX.toString,
      "posY" -> pos.iY.toString,
      "posZ" -> pos.iZ.toString,
      "rotation" -> rotation.toString
    )
  }

  override def serializeNBT: NBTTagCompound = {
    val tag = new NBTTagCompound
    val pos = new NBTTagCompound
    pos.setInteger("x", _pos.iX)
    pos.setInteger("y", _pos.iY)
    pos.setInteger("z", _pos.iZ)
    pos.setByte("rot", rotation)
    tag.setString("module_type", name.toString)
    tag.setTag("module_pos", pos)
    tag
  }

  override def deserializeNBT(tag: NBTTagCompound) {
    val pos: NBTTagCompound = tag.getCompoundTag("module_pos")
    pos_=(ModulePos(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z")))
    _rotation = pos.getByte("rot")
  }

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    false
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = capability.asInstanceOf
}

object Module {
  final val GRID_SIZE: Int = 16
}
