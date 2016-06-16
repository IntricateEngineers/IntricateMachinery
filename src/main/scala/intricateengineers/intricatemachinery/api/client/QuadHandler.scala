package intricateengineers.intricatemachinery.api.client

import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraftforge.fml.relauncher.Side
import intricateengineers.intricatemachinery.api.model.{ModelBase, Module}

import scala.collection.mutable.ArrayBuffer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.block.model.BlockPartRotation
import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats

@SideOnly(Side.CLIENT)
class QuadHandler(model : ModelBase) {

  protected val _quads = new ArrayBuffer[BakedQuad]

  def initQuads() {
    _quads.clear
    for (box <- model.boxes) {
      for (face <- EnumFacing.values) {
        val vecs = box.faceVecs(face)
        if (vecs != null) {
          val texture = Minecraft.getMinecraft.getTextureMapBlocks
            .getAtlasSprite(box.faces(face)._1.toString)
          val partFace = new BlockPartFace(null, 0, "", box.faces(face)._2)
          val mr = ModelRotation.X0_Y0
          val bpRot : BlockPartRotation = null
          var quad = QuadHandler.FaceBakery.makeBakedQuad(
            vecs._1,
            vecs._2,
            partFace,
            texture,
            face,
            mr,
            bpRot,
            true,
            true)
          _quads += quad
        }
      }
    }
  }

  def initTextures(): Unit = {
    for (box <- model.boxes) {
      for (face <- EnumFacing.values) {
        if (box.faces(face) != null) { 
          Minecraft.getMinecraft.getTextureMapBlocks
            .registerSprite(box.faces(face)._1)
        }
      }
    }
  }

  def quads(frame: MachineryFrame, module: Module, rand: Long): List[BakedQuad] = {
    var quads1 = new ArrayBuffer[BakedQuad]
    for (quad <- _quads) {
      val vertexData = quad.getVertexData.clone
      for (i <- 0 until 4*7 by 7) {
        val x = java.lang.Float.intBitsToFloat(vertexData(i))
        val y = java.lang.Float.intBitsToFloat(vertexData(i + 1))
        val z = java.lang.Float.intBitsToFloat(vertexData(i + 2))

        vertexData(i) = java.lang.Float.floatToRawIntBits(x + (module.posX / 16f))
        vertexData(i + 1) = java.lang.Float.floatToRawIntBits(y + (module.posY / 16f))
        vertexData(i + 2) = java.lang.Float.floatToRawIntBits(z + (module.posZ / 16f))
      }
      quads1 += new BakedQuad(vertexData, quad.getTintIndex, quad.getFace, quad.getSprite, quad.shouldApplyDiffuseLighting, DefaultVertexFormats.ITEM)
    }
    return quads1.toList
  }
}

object QuadHandler {
  val FaceBakery = new FaceBakery()
}
