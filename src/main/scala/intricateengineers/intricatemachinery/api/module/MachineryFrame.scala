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

import intricateengineers.intricatemachinery.api.client.BakedModelFrame
import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.common.module.DummyModule
import intricateengineers.intricatemachinery.common.module.FurnaceModule
import intricateengineers.intricatemachinery.core.ModInfo
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import javax.annotation.Nullable

import intricateengineers.intricatemachinery.api.model
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.{INormallyOccludingPart, Multipart}
import mcmultipart.raytrace.RayTraceUtils
import net.minecraft.util.EnumFacing._

import scala.collection.JavaConversions._

object MachineryFrame {
  val PROPERTY: MachineryFrame.Property = new MachineryFrame.Property
  val MODEL: BlockModel = new MachineryFrame.Model
  val NAME: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "machinery_frame")

  private class Property extends IUnlistedProperty[MachineryFrame] {
    def getName: String = "machinery_frame"

    def isValid(value: MachineryFrame): Boolean = true

    def getType: Class[MachineryFrame] = classOf[MachineryFrame]

    def valueToString(value: MachineryFrame): String = value.toString
  }

  private class Model extends BlockModel {
    def init {
      val frameTexture: ResourceLocation = new ResourceLocation("minecraft", "blocks/furnace_top")
      += (vec(0, 0, 0), vec(1, 16, 1)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(0, 0, 15), vec(1, 16, 16)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(15, 0, 0), vec(16, 16, 1)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(15, 0, 15), vec(16, 16, 16)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(1, 0, 0), vec(15, 1, 1)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(1, 15, 0), vec(15, 16, 1)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(0, 0, 1), vec(1, 1, 15)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(0, 15, 1), vec(1, 16, 15)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(15, 0, 1), vec(16, 1, 15)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(15, 15, 1), vec(16, 16, 15)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(1, 0, 15), vec(15, 1, 16)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
      += (vec(1, 15, 15), vec(15, 16, 16)).setFace(NORTH, frameTexture, UV.auto(16)).setFace(EAST, frameTexture, UV.auto(16)).setFace(SOUTH, frameTexture, UV.auto(16)).setFace(WEST, frameTexture, UV.auto(16)).setFace(UP, frameTexture, UV.auto(16)).setFace(DOWN, frameTexture, UV.auto(16))
    }

    def initBakedModel: BakedModelFrame = new BakedModelFrame
  }

}

class MachineryFrame extends Multipart with INormallyOccludingPart {
  modules.add(new FurnaceModule((this)) {})
  modules.add(new DummyModule((this)) {})
  var getModules: java.util.List[Module] = modules

  final private val modulePositions: Array[Array[Array[Module]]] = new Array[Array[Array[model.Module]]](16, 16, 16)
  var debugInfo: java.util.Set[java.util.Map[String, _]] = null
  private val selectionBoxes: java.util.List[AxisAlignedBB] = new java.util.ArrayList[AxisAlignedBB]
  private val modules: java.util.List[Module] = new java.util.ArrayList[Module]

  def addModule(module: Module): Boolean = {
    modules.add(module)
    return true
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

  @Nullable def moduleHit(start: Vec3d, end: Vec3d): Module = {
    val framePos: Vec3d = new Vec3d(this.getPos.getX, this.getPos.getY, this.getPos.getZ)
    for (module <- this.modules) {
      for (bounds <- module.getAABBs) {
        val rt: RayTraceResult = bounds.offset(module.posX / 16f, module.posY / 16f, module.posZ / 16f).calculateIntercept(start.subtract(framePos), end.add(framePos))
        if (rt != null) {
          return module
        }
      }
    }
    return null
  }

  override def addSelectionBoxes(list: java.util.List[AxisAlignedBB]) {
    MachineryFrame.MODEL.getBoxes.foreach(box => list.add(box.toAABB(0, 0, 0)))
    modules.foreach(module => module.addSelectionBoxes(list))
  }

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(tag)
    val modules: NBTTagCompound = new NBTTagCompound
    var i: Int = 0
    while (i < this.modules.size) {
      {
        modules.setTag(String.valueOf(i), this.modules.get(i).serializeNBT)
      }
      ({
        i += 1; i - 1   // wut
      })
    }
    tag.setTag("modules", modules)
    return tag
  }

  override def readFromNBT(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    val modules: NBTTagList = tag.getTagList("modules", 0)
    var i: Int = 0
    while (i < modules.tagCount) {
      {
      }
      ({
        i += 1; i - 1   // wut
      })
    }
  }

  override def writeUpdatePacket(buf: PacketBuffer) {
  }

  override def readUpdatePacket(buf: PacketBuffer) {
  }

  override def createBlockState: BlockStateContainer = {
    new ExtendedBlockState(MCMultiPartMod.multipart, new Array[IProperty[_]](0), Array[IUnlistedProperty[_]](MachineryFrame.PROPERTY))
  }

  override def getActualState(state: IBlockState): IBlockState = state


  override def getExtendedState(state: IBlockState): IBlockState = {
    state.asInstanceOf[IExtendedBlockState].withProperty(MachineryFrame.PROPERTY, this)
  }

  def addOcclusionBoxes(list: java.util.List[AxisAlignedBB]) {
    list.add(MachineryFrame.MODEL.mainBox.aabb(0, 0, 0))
  }

  def getDebugInfo: java.util.Set[java.util.Map[String, _]] = debugInfo
}