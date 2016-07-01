package intricateengineers.intricatemachinery.api.model

import intricateengineers.intricatemachinery.api.client.QuadHandler
import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.module.Module
import intricateengineers.intricatemachinery.api.util.VectorUtils._
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model._
import net.minecraft.util.EnumFacing._
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumFacing, ResourceLocation}
import org.lwjgl.util.vector.Vector3f

import scala.math._

/**
  * Created by topisani on 23/06/16.
  */
case class Box(from: (Double, Double, Double), to: (Double, Double, Double), faces: List[BoxFace] = List()) {
  val size = Vector3f.sub(to, from, null)
  val aabb = new AxisAlignedBB(
    from._1 / Module.GRID_SIZE,
    from._2 / Module.GRID_SIZE,
    from._3 / Module.GRID_SIZE,
    to._1 / Module.GRID_SIZE,
    to._2 / Module.GRID_SIZE,
    to._3 / Module.GRID_SIZE)
  private var _quads: List[BakedQuad] = List()

  def vecs(face: BoxFace): (Vector3f, Vector3f) = {
    face.side match {
      case UP =>
        val k = max(from._2, to._2)
        ((from._1, k, from._3), (to._1, k, to._3))
      case DOWN =>
        val k = min(from._2, to._2)
        ((from._1, k, from._3), (to._1, k, to._3))
      case NORTH =>
        val k = min(from._3, to._3)
        ((from._1, from._2, k), (to._1, to._2, k))
      case SOUTH =>
        val k = max(from._3, to._3)
        ((from._1, from._2, k), (to._1, to._2, k))
      case WEST =>
        val k = min(from._1, to._1)
        ((k, from._2, from._3), (k, to._2, to._3))
      case EAST =>
        val k = max(from._1, to._1)
        ((k, from._2, from._3), (k, to._2, to._3))
      case _ => (null, null)
    }
  }

  def quads = _quads

  def initTextures() = {
    for (face <- faces) {
      Minecraft.getMinecraft.getTextureMapBlocks
        .registerSprite(face.texture)
    }
  }

  def offset(pos: (Int, Int, Int)): Box = {
    val b = Box((from._1 + pos._1, from._2 + pos._2, from._3 + pos._3),
      (to._1 + pos._1, to._2 + pos._2, to._3 + pos._3), faces)
    b.updateQuads()
    b
  }

  def updateQuads(): Unit = {
    try {
      _quads = faces.map(_.quad(this))
    } catch {
      case _: NullPointerException =>
    }
  }
}

case class BoxFace(side: EnumFacing, texture: ResourceLocation, uv: UV) {
  def quad(box: Box): BakedQuad = {
    val vecs = box.vecs(this)
    val atlasSprite = Minecraft.getMinecraft.getTextureMapBlocks
      .registerSprite(texture)
    val partFace = new BlockPartFace(null, 0, "", uv.toBFUV(side, (box.from, box.to)))
    val mr = ModelRotation.X0_Y0
    val bpRot: BlockPartRotation = null
    QuadHandler.FaceBakery.makeBakedQuad(
      vecs._1,
      vecs._2,
      partFace,
      atlasSprite,
      side,
      mr,
      bpRot,
      true,
      true)
  }
}
