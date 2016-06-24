/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package intricateengineers.intricatemachinery.api.module

import javax.annotation.Nullable

import intricateengineers.intricatemachinery.api.client.BakedModelFrame
import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.model.{BlockModel, Box, BoxFace}
import intricateengineers.intricatemachinery.api.util.{Cache, IHasDebugInfo, Logger}
import intricateengineers.intricatemachinery.common.module.{DummyModule, FurnaceModule}
import intricateengineers.intricatemachinery.core.ModInfo
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.{INormallyOccludingPart, Multipart}
import mcmultipart.raytrace.RayTraceUtils
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing._
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.{AxisAlignedBB, RayTraceResult, Vec3d}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object MachineryFrame {
  val NAME: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "machinery_frame")
}

class MachineryFrame extends Multipart
  with INormallyOccludingPart
  with IHasDebugInfo {

  val quadCache: Cache[java.util.List[BakedQuad]] = Cache(updateQuads)
  val bbCache: Cache[java.util.List[AxisAlignedBB]] = Cache(updateAABBs)
  var modules: List[Module] = List()

  addModule(new FurnaceModule(this)).pos = ModulePos(8, 8, 8)
  addModule(new DummyModule(this)).pos = ModulePos(0, 0, 0)

  def addModule(module: Module): Module = {
    val lb: ListBuffer[Module] = modules.to[ListBuffer]
    lb += module
    modules = lb.toList
    module
  }

  def moduleUpdated(module: Module): Unit = {
    //updateModulePositions(module)
    updateAABBs()
  }

  def updateAABBs(): java.util.List[AxisAlignedBB] = {
    val bbs = ListBuffer[AxisAlignedBB]()
    bbs ++= modules.flatMap(_.bbCache.get)
    bbs ++= FrameModel.aabbs
    bbs
  }

  override def updateDebugInfo(): Map[String, String] = {
    // TODO: some kind of debugInfo
    Map()
  }

  @Nullable
  def moduleHit(start: Vec3d, end: Vec3d): Module = {
    val framePos: Vec3d = new Vec3d(this.getPos.getX, this.getPos.getY, this.getPos.getZ)
    for (module <- this.modules) {
      for (bounds: AxisAlignedBB <- module.bbCache.get()) {
        val rt: RayTraceResult = bounds.calculateIntercept(start.subtract(framePos), end.add(framePos))
        if (rt != null) {
          return module
        }
      }
    }
    null
  }

  override def getType: ResourceLocation = MachineryFrame.NAME

  override def collisionRayTrace(start: Vec3d, end: Vec3d): RayTraceUtils.AdvancedRayTraceResultPart = {
    val result: RayTraceUtils.AdvancedRayTraceResult = RayTraceUtils.collisionRayTrace(getWorld, getPos, start, end,
      bbCache.get())
    if (result == null) null
    else new RayTraceUtils.AdvancedRayTraceResultPart(result, this)
  }

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(tag)
    val modules: NBTTagCompound = new NBTTagCompound
    for (i <- this.modules.indices) {
      try {
        modules.setTag(String.valueOf(i), this.modules(i).serializeNBT)
      } catch {
        case e: Exception => Logger.warn("Couldn't write to NBT tag")
      }
    }
    tag.setTag("modules", modules)
    tag
  }

  override def readFromNBT(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    val modules: NBTTagList = tag.getTagList("modules", 0)
    for (i <- 0 until modules.tagCount) {
      try {
        modules.getCompoundTagAt(i)
        //TODO: Do stuff
      } catch {
        case e: Exception => Logger.warn("Couldn't read from NBT tag")
      }
    }
  }

  override def writeUpdatePacket(buf: PacketBuffer) {
  }

  override def readUpdatePacket(buf: PacketBuffer) {
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
    list.appendAll(bbCache.get())
  }

  private def updateQuads(): java.util.List[BakedQuad] = {
    val buffer = ListBuffer[BakedQuad]()
    buffer ++= FrameModel.boxes.flatMap(_.quads)
    buffer ++= modules.flatMap(_.boxCache.get().flatMap(_.quads))
    buffer
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

class ModuleList {
  private val positions = mutable.Map[(Int, Int, Int), Module]()

  def +=(m: Module): Unit = {
    def addAndOffset(x: Int, y: Int, z: Int): Unit = {
      positions((x + m.pos.iX, y + m.pos.iY, z + m.pos.iZ)) = m
    }
  }

  private def forEachCoord(m: Module)(f: (Box, BoxFace, Int, Int, Int) ⇒ Unit): Unit = {
    for (b ← m.model.boxes)
      for (face ← b.faces) {
        val (from, to) = b.vecs(face)
        for (x ← from.x.toInt until to.x.toInt;
             y ← from.y.toInt until to.y.toInt;
             z ← from.z.toInt until to.z.toInt)
          f(b, face, x, y, z)
      }
  }
}
