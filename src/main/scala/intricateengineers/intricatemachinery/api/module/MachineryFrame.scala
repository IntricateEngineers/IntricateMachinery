package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.client.BakedModelFrame
import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.model.{BlockModel, Box, BoxFace}
import intricateengineers.intricatemachinery.api.util.{Cache, IHasDebugInfo, Logger}
import intricateengineers.intricatemachinery.common.util.IMRL
import main.scala.intricateengineers.intricatemachinery.api.module.ModuleCapability
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.{INormallyOccludingPart, Multipart}
import mcmultipart.raytrace.{PartMOP, RayTraceUtils}
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.{AxisAlignedBB, RayTraceResult, Vec3d}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConversions._
import scala.collection.immutable.ListMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object MachineryFrame {
  val NAME = IMRL("machinery_frame")
}

class MachineryFrame extends Multipart
  with INormallyOccludingPart
  with IHasDebugInfo {

  val quadCache: Cache[java.util.List[BakedQuad]] = Cache(updateQuads)
  val bbCache: Cache[java.util.List[AxisAlignedBB]] = Cache(updateAABBs)

  def moduleHit(start: Vec3d, end: Vec3d): Option[Module] = {
    val framePos: Vec3d = new Vec3d(this.getPos.getX, this.getPos.getY, this.getPos.getZ)

    // Gather all hit vectors (positions of the hit) for every module (in the current MF) our eye can raytrace
    // Add them with the associated Module in a Tuple, so that we know to pick the closest one later
    val modulesHit: Map[Vec3d, Module] =
      modules.flatMap(module =>
        module.bbCache().map(bounds =>
          Option(     // At times like this I wish Forge was in Scala
            bounds.calculateIntercept(start.subtract(framePos), end.subtract(framePos)))
                  .map(rayTraceResult => (rayTraceResult.hitVec, module))
      )
    ).flatten.toMap[Vec3d, Module]

    // Get the module for which the associated hit vector is closest
    modulesHit get modulesHit.keys.minBy(_.distanceTo(start))
  }


  @SideOnly(Side.CLIENT)
  def moduleHitFromEyes(): Option[Module] = {
    val mc = Minecraft.getMinecraft
    val eyes: Vec3d = mc.thePlayer.getPositionEyes(1)

    // 5 is the range that AABBs get highlighted (in blocks)
    moduleHit(eyes, eyes.add(mc.thePlayer.getLookVec.scale(5)))
  }

  def wasUpdated(): Unit = {
    modules.invalidate()
  }

  def updateAABBs(): java.util.List[AxisAlignedBB] = {
    val bbs = ListBuffer[AxisAlignedBB]()
    bbs ++= modules.flatMap(_.bbCache())
    bbs ++= FrameModel.aabbs
  }

  @SideOnly(Side.CLIENT)
  private def updateQuads(): java.util.List[BakedQuad] = {
    val buffer = ListBuffer[BakedQuad]()
    buffer ++= FrameModel.boxes.flatMap(_.quads)
    modules.foreach(_.boxCache.invalidate())
    bbCache.invalidate()
    buffer ++= modules.flatMap(_.boxCache().flatMap(_.quads))
    buffer
  }

  private def breakModule(module: Option[Module]): Unit = {
    if(module.isEmpty) Logger.warn("Couldn't find module")
    module.foreach(m =>
      modules -= m)
  }

  /* ------======================------
               OVERRIDES
     ------======================------ */

  override def getType: ResourceLocation = MachineryFrame.NAME

  override def onRemoved(): Unit = {}

  override def harvest(player: EntityPlayer, hit: PartMOP) = {
    val maybeModule = moduleHitFromEyes()
    maybeModule match {
      case Some(module) =>
        breakModule(maybeModule)
      case None =>
        super.harvest(player, hit)
    }
  }

  override def collisionRayTrace(start: Vec3d, end: Vec3d): RayTraceUtils.AdvancedRayTraceResultPart = {
    val result: RayTraceUtils.AdvancedRayTraceResult = RayTraceUtils.collisionRayTrace(getWorld, getPos, start, end,
      bbCache())
    if (result == null) null
    else new RayTraceUtils.AdvancedRayTraceResultPart(result, this)
  }

  override def updateDebugInfo(): ListMap[String, String] = {
    ListMap[String, String](
      "Modules" -> modules.toList.length.toString
    )
  }

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(tag)
    val modules: NBTTagList = new NBTTagList
    for (i <- this.modules.toList().indices) {
      try {
        modules.appendTag(this.modules(i).serializeNBT)
      } catch {
        case e: Exception => Logger.warn("Couldn't write to NBT tag")
      }
    }
    tag.setTag("modules", modules)
    tag
  }

  override def readFromNBT(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    val moduleTag: NBTTagList = tag.getTagList("modules", 10)
    modules.clear()
    for (i <- 0 until moduleTag.tagCount) {
      try {
        val mTag = moduleTag.getCompoundTagAt(i)
        val mType = new ResourceLocation(mTag.getString("module_type"))
        (modules += Modules.createModule(mType)(this)).deserializeNBT(mTag)
      } catch {
        case e: Exception => Logger.warn("Couldn't read from NBT tag")
      }
    }
  }

  override def writeUpdatePacket(buf: PacketBuffer) {
    val tag = new NBTTagCompound
    buf.writeNBTTagCompoundToBuffer(writeToNBT(tag))
  }

  override def readUpdatePacket(buf: PacketBuffer) {
    readFromNBT(buf.readNBTTagCompoundFromBuffer())
  }

  override def createBlockState: BlockStateContainer = {
    new ExtendedBlockState(MCMultiPartMod.multipart, new Array[IProperty[_]](0), Array[IUnlistedProperty[_]]
      (FrameProperty))
  }

  override def getActualState(state: IBlockState): IBlockState = state

  override def getExtendedState(state: IBlockState): IBlockState = {
    state.asInstanceOf[IExtendedBlockState].withProperty(FrameProperty, this)
  }

  override def addOcclusionBoxes(list: java.util.List[AxisAlignedBB]): Unit = addSelectionBoxes(list)

  override def addSelectionBoxes(list: java.util.List[AxisAlignedBB]) {
    list.appendAll(bbCache())
  }

  val modules = new Traversable[Module] {
    private val positions = mutable.Map[(Int, Int, Int), Module]()
    private val capabilities = mutable.Map[(Int, Int, Int), ModuleCapability]()
    private val modules = ListBuffer[Module]()

    def apply(i: Int): Module = {
      modules(i)
    }

    def +=(m: Module): Module = {
      modules += m
      forEachCoord(m, (b, f, x, y, z) =>
        positions((x + m.pos.iX, y + m.pos.iY, z + m.pos.iZ)) = m)
      invalidate()
      m
    }

    def -=(m: Module): Unit = {
      modules -= m
      invalidate()
    }

    def clear(): Unit = {
      modules.clear()
    }

    def invalidate(): Unit = {
      bbCache.invalidate()
      quadCache.invalidate()
      debugInfo.invalidate()
      sendUpdatePacket()
    }

    def forEachCoord(m: Module, f: (Box, BoxFace, Int, Int, Int) => Unit): Unit = {
      for (b <- m.boxCache())
        forEachCoord(b, (face, x, y, z) => f(b, face, x, y, z))
    }

    def forEachCoord(b: Box, f: (BoxFace, Int, Int, Int) => Unit): Unit = {
      for (face <- b.faces) {
        val (from, to) = b.vecs(face)
        for (x <- from.x.toInt until to.x.toInt;
          y <- from.y.toInt until to.y.toInt;
          z <- from.z.toInt until to.z.toInt)
          f(face, x, y, z)
      }
    }

    def forEachAdjacent(b: Box, f: (BoxFace, Int, Int, Int) => Unit): Unit = {
      forEachCoord(b, (face, x, y, z) => {
        val offset = face.side.getDirectionVec
        f(face, x + offset.getX, y + offset.getY, z + offset.getZ)
      })
    }

    def forEachAdjacent(m: Module, f: (Box, BoxFace, Int, Int, Int) => Unit): Unit = {
      for (b <- m.boxCache())
        forEachAdjacent(b, (face, x, y, z) => f(b, face, x, y, z))
    }

    override def foreach[U](f: (Module) => U) = modules.foreach(f)

    override def toList(): List[Module] = modules.toList
  }
}

object FrameProperty extends IUnlistedProperty[MachineryFrame] {
  def getName: String = "machinery_frame"

  def isValid(value: MachineryFrame): Boolean = true

  def getType: Class[MachineryFrame] = classOf[MachineryFrame]

  def valueToString(value: MachineryFrame): String = value.toString
}

object FrameModel extends BlockModel {

  lazy val aabbs = boxes.map(_.aabb)
  @SideOnly(Side.CLIENT)
  val bakedModel = BakedModelFrame
  val frameTexture: ResourceLocation = new ResourceLocation("minecraft", "blocks/furnace_top")


  define {
    |#|:(0, 0, 0)(1, 16, 1) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(0, 0, 15)(1, 16, 16) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(15, 0, 0)(16, 16, 1) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(15, 0, 15)(16, 16, 16) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(1, 0, 0)(15, 1, 1) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(1, 15, 0)(15, 16, 1) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(0, 0, 1)(1, 1, 15) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(0, 15, 1)(1, 16, 15) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(15, 0, 1)(16, 1, 15) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(15, 15, 1)(16, 16, 15) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(1, 0, 15)(15, 1, 16) {
      |*(frameTexture, UV.auto(16))
    }
    |#|:(1, 15, 15)(15, 16, 16) {
      |*(frameTexture, UV.auto(16))
    }
  }
}

