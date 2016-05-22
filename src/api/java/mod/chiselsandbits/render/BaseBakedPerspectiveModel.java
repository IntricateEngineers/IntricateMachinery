package mod.chiselsandbits.render;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;

public abstract class BaseBakedPerspectiveModel implements IPerspectiveAwareModel
{

	private static final Matrix4f ground;
	private static final Matrix4f gui;
	private static final Matrix4f fixed;
	private static final Matrix4f firstPerson_righthand;
	private static final Matrix4f firstPerson_lefthand;
	private static final Matrix4f thirdPerson_righthand;
	private static final Matrix4f thirdPerson_lefthand;

	static
	{
		// for some reason these are not identical to vanilla's Block.json, I
		// don't know why.. but its close.

		{
			final javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f( 0, 0, 0 );
			final javax.vecmath.Vector3f scale = new javax.vecmath.Vector3f( 0.625f, 0.625f, 0.625f );
			final Quat4f rotation = TRSRTransformation.quatFromXYZDegrees( new javax.vecmath.Vector3f( 30, 225, 0 ) );

			final TRSRTransformation transform = new TRSRTransformation( translation, rotation, scale, null );
			gui = transform.getMatrix();
		}

		{
			final javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f( 0, 0, 0 );
			final javax.vecmath.Vector3f scale = new javax.vecmath.Vector3f( 0.25f, 0.25f, 0.25f );
			final Quat4f rotation = TRSRTransformation.quatFromXYZDegrees( new javax.vecmath.Vector3f( 0, 0, 0 ) );

			final TRSRTransformation transform = new TRSRTransformation( translation, rotation, scale, null );
			ground = transform.getMatrix();
		}

		{
			final javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f( 0, 0, 0 );
			final javax.vecmath.Vector3f scale = new javax.vecmath.Vector3f( 0.5f, 0.5f, 0.5f );
			final Quat4f rotation = TRSRTransformation.quatFromXYZDegrees( new javax.vecmath.Vector3f( 0, 0, 0 ) );

			final TRSRTransformation transform = new TRSRTransformation( translation, rotation, scale, null );
			fixed = transform.getMatrix();
		}

		{
			final javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f( 0, 0, 0 );
			final javax.vecmath.Vector3f scale = new javax.vecmath.Vector3f( 0.375f, 0.375f, 0.375f );
			final Quat4f rotation = TRSRTransformation.quatFromXYZDegrees( new javax.vecmath.Vector3f( 75, 45, 0 ) );

			final TRSRTransformation transform = new TRSRTransformation( translation, rotation, scale, null );
			thirdPerson_lefthand = thirdPerson_righthand = transform.getMatrix();
		}

		{
			final javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f( 0, 0, 0 );
			final javax.vecmath.Vector3f scale = new javax.vecmath.Vector3f( 0.40f, 0.40f, 0.40f );
			final Quat4f rotation = TRSRTransformation.quatFromXYZDegrees( new javax.vecmath.Vector3f( 0, 45, 0 ) );

			final TRSRTransformation transform = new TRSRTransformation( translation, rotation, scale, null );
			firstPerson_righthand = transform.getMatrix();
		}

		{
			final javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f( 0, 0, 0 );
			final javax.vecmath.Vector3f scale = new javax.vecmath.Vector3f( 0.40f, 0.40f, 0.40f );
			final Quat4f rotation = TRSRTransformation.quatFromXYZDegrees( new javax.vecmath.Vector3f( 0, 225, 0 ) );

			final TRSRTransformation transform = new TRSRTransformation( translation, rotation, scale, null );
			firstPerson_lefthand = transform.getMatrix();
		}
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
			final TransformType cameraTransformType )
	{
		switch ( cameraTransformType )
		{
			case FIRST_PERSON_LEFT_HAND:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, firstPerson_lefthand );
			case FIRST_PERSON_RIGHT_HAND:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, firstPerson_righthand );
			case THIRD_PERSON_LEFT_HAND:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, thirdPerson_lefthand );
			case THIRD_PERSON_RIGHT_HAND:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, thirdPerson_righthand );
			case FIXED:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, fixed );
			case GROUND:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, ground );
			case GUI:
				return new ImmutablePair<IBakedModel, Matrix4f>( this, gui );
			default:
		}

		return new ImmutablePair<IBakedModel, Matrix4f>( this, fixed );
	}

}
