package intricateengineers.intricatemachinery.api.client

import intricateengineers.intricatemachinery.api.module.{FrameModel, MachineryFrame}
import mcmultipart.client.multipart.MultipartSpecialRenderer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11

class FrameRenderer extends MultipartSpecialRenderer[MachineryFrame] {
  final private val mc: Minecraft = Minecraft.getMinecraft
  final private val tessellator: Tessellator = Tessellator.getInstance
  private var renderer: BlockModelRenderer = null
  private var baseState: IBlockState = null

  def renderMultipartAt(part: MachineryFrame, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
    mc.getTextureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
    GlStateManager.pushMatrix()
    GlStateManager.disableLighting()
    val buffer: VertexBuffer = Tessellator.getInstance.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
    renderMultipartFast(part, x, y, z, partialTicks, destroyStage, buffer)
    tessellator.draw()
    GlStateManager.enableLighting()
    GlStateManager.popMatrix()
  }

  override def renderMultipartFast(frame: MachineryFrame, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, buffer: VertexBuffer) {
    if (this.baseState == null) {
      this.baseState = frame.createBlockState.getBaseState
      renderer = mc.getBlockRendererDispatcher.getBlockModelRenderer
    }
    if (frame == null) {
      return
    }
    val pos: BlockPos = frame.getPos
    buffer.setTranslation(x - pos.getX, y - pos.getY, z - pos.getZ)
    renderer.renderModel(frame.getWorld, FrameModel.bakedModel, frame.getExtendedState(this.baseState), pos, buffer, false, 52L)
    buffer.setTranslation(0, 0, 0)
  }
}
