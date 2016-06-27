package intricateengineers.intricatemachinery.core

import javax.annotation.Nullable

import intricateengineers.intricatemachinery.api.client.FrameRenderer
import intricateengineers.intricatemachinery.api.module.{FrameModel, MachineryFrame}
import intricateengineers.intricatemachinery.client.event.DebugRenderHandler
import intricateengineers.intricatemachinery.common.module.{DummyModel, FurnaceModel}
import mcmultipart.client.multipart.MultipartRegistryClient
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.event.{ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent,
FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ClientProxy {
  var EMPTY_MODEL: IBakedModel = new IBakedModel() {
    def getQuads(@Nullable state: IBlockState, @Nullable side: EnumFacing, rand: Long): java.util.List[BakedQuad] = {
      new java.util.ArrayList[BakedQuad]
    }

    def isAmbientOcclusion: Boolean = false

    def isGui3d: Boolean = false

    def isBuiltInRenderer: Boolean = false

    def getParticleTexture: TextureAtlasSprite = null

    def getItemCameraTransforms: ItemCameraTransforms = null

    def getOverrides: ItemOverrideList = null
  }
}

class ClientProxy extends CommonProxy {
  @SubscribeEvent
  def onPostBake(event: ModelBakeEvent) {
    event.getModelRegistry.putObject(new ModelResourceLocation(MachineryFrame.NAME, "inventory"), ClientProxy
      .EMPTY_MODEL)
    event.getModelRegistry.putObject(new ModelResourceLocation(MachineryFrame.NAME, "multipart"), ClientProxy
      .EMPTY_MODEL)
  }

  @SubscribeEvent
  def onTextureStitch(event: TextureStitchEvent.Pre) {
    FurnaceModel.boxes.foreach(_.initTextures())
    DummyModel.boxes.foreach(_.initTextures())
    FrameModel.bakedModel.initTextures()
  }

  @SubscribeEvent
  def onTextureStitch(event: TextureStitchEvent.Post) {
    FurnaceModel.boxes.foreach(_.updateQuads())
    DummyModel.boxes.foreach(_.updateQuads())
    FrameModel.boxes.foreach(_.updateQuads())
  }

  override def preInit(event: FMLPreInitializationEvent) {
  }

  override def init(event: FMLInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(DebugRenderHandler.instance)
    MultipartRegistryClient.bindMultipartSpecialRenderer(classOf[MachineryFrame], new FrameRenderer)
  }

  override def postInit(event: FMLPostInitializationEvent) {
  }
}
