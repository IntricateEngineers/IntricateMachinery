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
import intricateengineers.intricatemachinery.api.model.BlockModel
import intricateengineers.intricatemachinery.api.util.Logger
import intricateengineers.intricatemachinery.common.module.{DummyModule, FurnaceModule}
import intricateengineers.intricatemachinery.core.ModInfo
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.{INormallyOccludingPart, Multipart}
import mcmultipart.raytrace.RayTraceUtils
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing._
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.{AxisAlignedBB, RayTraceResult, Vec3d}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState, IUnlistedProperty}

import scala.collection.JavaConversions._
import scala.collection._
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object MachineryFrame {
  val NAME: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "machinery_frame")
}

class MachineryFrame extends Multipart with INormallyOccludingPart {

  val modulePositions: Array[Array[ArrayBuffer[Module]]] = Array(Array(ArrayBuffer()))
  val selectionBoxes: ListBuffer[AxisAlignedBB] = ListBuffer()
  var debugInfo: Map[String, String] = Map()
  var modules: List[Module] = List()

  addModule(new FurnaceModule(this)).pos = ModulePos(3d, 4d, 1d)
  addModule(new DummyModule(this)).pos = ModulePos(4d, 6d, 2d)

  def addModule(module: Module): Module = {
    val lb: mutable.Buffer[Module] = modules.toBuffer
    lb += module
    modules = lb.toList
    module
  }

  def updateModulePositions(module: Module): Unit = {
    modulePositions.foreach(_.foreach(_ -= module))
    for (bb ← module.boundingBoxes) {
      for (
        x ← bb.minX until bb.maxX;
        y ← bb.minY until bb.maxY;
        z ← bb.minZ until bb.maxZ
      ) {
        modulePositions(x)(y)(z) = module
      }
    }
  }

  override def getType: ResourceLocation = MachineryFrame.NAME

  override def collisionRayTrace(start: Vec3d, end: Vec3d): RayTraceUtils.AdvancedRayTraceResultPart = {
    if (selectionBoxes.isEmpty) {
      addSelectionBoxes(selectionBoxes)
    }
    val result: RayTraceUtils.AdvancedRayTraceResult = RayTraceUtils.collisionRayTrace(getWorld, getPos, start, end, selectionBoxes)

    if (result == null) null
    else new RayTraceUtils.AdvancedRayTraceResultPart(result, this)
  }

  def addSelectionBoxes(list: ListBuffer[AxisAlignedBB]) {
    FrameModel.boxes.foreach(box => list.add(box.aabb(0, 0, 0)))
    modules.foreach(i => list.append(i.model.mainBox.aabb(0, 0, 0)))
  }

  @Nullable
  def moduleHit(start: Vec3d, end: Vec3d): Module = {
    val framePos: Vec3d = new Vec3d(this.getPos.getX, this.getPos.getY, this.getPos.getZ)
    for (module <- this.modules) {
      for (bounds: AxisAlignedBB <- module.boundingBoxes) {
        val rt: RayTraceResult = bounds.offset(module.pos.dX, module.pos.dY, module.pos.dZ).calculateIntercept(start.subtract(framePos), end.add(framePos))
        if (rt != null) {
          return module
        }
      }
    }
    null
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
    new ExtendedBlockState(MCMultiPartMod.multipart, new Array[IProperty[_]](0), Array[IUnlistedProperty[_]](FrameProperty))
  }

  override def getActualState(state: IBlockState): IBlockState = state


  override def getExtendedState(state: IBlockState): IBlockState = {
    state.asInstanceOf[IExtendedBlockState].withProperty(FrameProperty, this)
  }

  // TODO: Never gets called; investigate
  def addOcclusionBoxes(list: java.util.List[AxisAlignedBB]) {
    list.add(FrameModel.mainBox.aabb(0, 0, 0))
  }
}

object FrameProperty extends IUnlistedProperty[MachineryFrame] {
  def getName: String = "machinery_frame"

  def isValid(value: MachineryFrame): Boolean = true

  def getType: Class[MachineryFrame] = classOf[MachineryFrame]

  def valueToString(value: MachineryFrame): String = value.toString
}

object FrameModel extends BlockModel {
  def init() {
    val frameTexture: ResourceLocation = new ResourceLocation("minecraft", "blocks/furnace_top")
    += ((0, 0, 0), (1, 16, 1))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((0, 0, 15), (1, 16, 16))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((15, 0, 0), (16, 16, 1))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((15, 0, 15), (16, 16, 16))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((1, 0, 0), (15, 1, 1))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((1, 15, 0), (15, 16, 1))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((0, 0, 1), (1, 1, 15))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((0, 15, 1), (1, 16, 15))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((15, 0, 1), (16, 1, 15))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((15, 15, 1), (16, 16, 15))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((1, 0, 15), (15, 1, 16))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
    += ((1, 15, 15), (15, 16, 16))
      .face(NORTH, frameTexture, UV.auto(16))
      .face(EAST, frameTexture, UV.auto(16))
      .face(SOUTH, frameTexture, UV.auto(16))
      .face(WEST, frameTexture, UV.auto(16))
      .face(UP, frameTexture, UV.auto(16))
      .face(DOWN, frameTexture, UV.auto(16))
  }

  def initBakedModel = new BakedModelFrame
}


