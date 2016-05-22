package mod.chiselsandbits.bittank;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileEntitySpecialRenderBitTank extends FastTESR<TileEntityBitTank>
{

	private final FluidModelVertex[] model = new FluidModelVertex[6 * 4];

	public TileEntitySpecialRenderBitTank()
	{
		model[0] = new FluidModelVertex( EnumFacing.UP, 0, 1, 0, 0, 0, 0, 0 );
		model[1] = new FluidModelVertex( EnumFacing.UP, 1, 1, 0, 1, 0, 0, 0 );
		model[2] = new FluidModelVertex( EnumFacing.UP, 1, 1, 1, 1, 1, 0, 0 );
		model[3] = new FluidModelVertex( EnumFacing.UP, 0, 1, 1, 0, 1, 0, 0 );

		model[4] = new FluidModelVertex( EnumFacing.DOWN, 0, 0, 0, 0, 0, 0, 0 );
		model[5] = new FluidModelVertex( EnumFacing.DOWN, 1, 0, 0, 1, 0, 0, 0 );
		model[6] = new FluidModelVertex( EnumFacing.DOWN, 1, 0, 1, 1, 1, 0, 0 );
		model[7] = new FluidModelVertex( EnumFacing.DOWN, 0, 0, 1, 0, 1, 0, 0 );

		model[8] = new FluidModelVertex( EnumFacing.NORTH, 0, 0, 0, 0, 0, 0, 0 );
		model[9] = new FluidModelVertex( EnumFacing.NORTH, 1, 0, 0, 1, 0, 0, 0 );
		model[10] = new FluidModelVertex( EnumFacing.NORTH, 1, 1, 0, 1, 0, 0, 1 );
		model[11] = new FluidModelVertex( EnumFacing.NORTH, 0, 1, 0, 0, 0, 0, 1 );

		model[12] = new FluidModelVertex( EnumFacing.SOUTH, 0, 0, 1, 0, 0, 0, 0 );
		model[13] = new FluidModelVertex( EnumFacing.SOUTH, 1, 0, 1, 1, 0, 0, 0 );
		model[14] = new FluidModelVertex( EnumFacing.SOUTH, 1, 1, 1, 1, 0, 0, 1 );
		model[15] = new FluidModelVertex( EnumFacing.SOUTH, 0, 1, 1, 0, 0, 0, 1 );

		model[16] = new FluidModelVertex( EnumFacing.EAST, 1, 0, 0, 0, 0, 0, 0 );
		model[17] = new FluidModelVertex( EnumFacing.EAST, 1, 0, 1, 1, 0, 0, 0 );
		model[18] = new FluidModelVertex( EnumFacing.EAST, 1, 1, 1, 1, 0, 0, 1 );
		model[19] = new FluidModelVertex( EnumFacing.EAST, 1, 1, 0, 0, 0, 0, 1 );

		model[20] = new FluidModelVertex( EnumFacing.WEST, 0, 0, 0, 0, 0, 0, 0 );
		model[21] = new FluidModelVertex( EnumFacing.WEST, 0, 0, 1, 1, 0, 0, 0 );
		model[22] = new FluidModelVertex( EnumFacing.WEST, 0, 1, 1, 1, 0, 0, 1 );
		model[23] = new FluidModelVertex( EnumFacing.WEST, 0, 1, 0, 0, 0, 0, 1 );
	}

	@Override
	public void renderTileEntityFast(
			final TileEntityBitTank te,
			final double x,
			final double y,
			final double z,
			final float partialTicks,
			final int destroyStage,
			final VertexBuffer worldRenderer )
	{
		if ( destroyStage > 0 )
		{
			return;
		}

		final FluidStack fluidStack = te.getBitsAsFluidStack();
		if ( fluidStack != null )
		{
			final Fluid fluid = fluidStack.getFluid();
			final int pass = fluid.getBlock().getBlockLayer() == BlockRenderLayer.TRANSLUCENT ? 1 : 0;

			if ( MinecraftForgeClient.getRenderPass() != pass )
			{
				return;
			}

			final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite( fluid.getStill().toString() );
			final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite( fluid.getFlowing().toString() );

			final BlockPos pos = te.getPos();

			final int mixedBrightness = te.getWorld().getBlockState( pos ).getPackedLightmapCoords( te.getWorld(), te.getPos() );
			final int skyLight = mixedBrightness >> 16 & 65535;
			final int blockLight = mixedBrightness & 65535;

			final double fullness = (double) fluidStack.amount / (double) TileEntityBitTank.MAX_CONTENTS;

			final int rgbaColor = fluid.getColor();
			final int rColor = rgbaColor >> 16 & 0xff;
			final int gColor = rgbaColor >> 8 & 0xff;
			final int bColor = rgbaColor & 0xff;
			final int aColor = rgbaColor >> 24 & 0xff;

			worldRenderer.setTranslation( x - pos.getX(), y - pos.getY(), z - pos.getZ() );

			for ( final FluidModelVertex vert : model )
			{
				final EnumFacing face = vert.face;
				final TextureAtlasSprite sprite = face.getFrontOffsetY() != 0 ? still : flowing;

				for ( final VertexFormatElement e : worldRenderer.getVertexFormat().getElements() )
				{
					switch ( e.getUsage() )
					{
						case COLOR:
							worldRenderer.color( rColor, gColor, bColor, aColor );
							break;

						case NORMAL:
							worldRenderer.normal( face.getFrontOffsetX(), face.getFrontOffsetY(), face.getFrontOffsetZ() );
							break;

						case POSITION:
							final double vertX = pos.getX() + vert.x * 0.756 + 0.122;
							final double vertY = pos.getY() + vert.yMultiplier * fullness * 0.756 + 0.122;
							final double vertZ = pos.getZ() + vert.z * 0.756 + 0.122;

							worldRenderer.pos( vertX, vertY, vertZ );
							break;

						case UV:
							if ( e.getIndex() == 1 )
							{
								worldRenderer.lightmap( skyLight, blockLight );
							}
							else
							{
								worldRenderer.tex( sprite.getInterpolatedU( vert.u + vert.uMultiplier * fullness ), sprite.getInterpolatedV( 16.0 - ( vert.v + vert.vMultiplier * fullness ) ) );
							}
							break;

						default:
							break;
					}
				}
				worldRenderer.endVertex();
			}
		}
	}

}
