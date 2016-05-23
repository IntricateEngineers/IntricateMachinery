package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.core.ModInfo;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.Multipart;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import java.util.List;

/**
 * @author topisani
 */
public class IMModule extends Multipart {
    private final ResourceLocation name;

    public Vec3d localPos;

    public static final PropertyInteger PROPERTYX = PropertyInteger.create("local_x", 0 , 15);
    public static final PropertyInteger PROPERTYY = PropertyInteger.create("local_y", 0 , 15);
    public static final PropertyInteger PROPERTYZ = PropertyInteger.create("local_z", 0 , 15);

    public IMModel getModel() {
        return model;
    }

    private final IMModel model;

    public IMModule(String name, IMModel model) {
        this.name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), name);
        this.model = model;
    }

    public void setLocalPos(Vec3d localPos)
    {
        this.localPos = localPos;

    }

    @Override
    public ResourceLocation getType() {
        return name;
    }

    /**
     * Adds the selection boxes used to ray trace this part.
     */
    @Override
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
        for (IMModel.Box box : model.getBoxes()) {
            list.add(box.toAABB());
        }
    }

    @Override
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        //for(IMModel.Box box : model.getBoxes()) {
        //    list.add(box.toAABB());
        //}
    }

    @Override
    public IBlockState getActualState(IBlockState state) {

        return state;
    }

    @Override
    public BlockStateContainer createBlockState() {

        return new BlockStateContainer(MCMultiPartMod.multipart, PROPERTYX, PROPERTYY, PROPERTYZ);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state) {
        return state.withProperty(PROPERTYX,5).withProperty(PROPERTYY,2).withProperty(PROPERTYZ,3); // Hardcoded values for testing
    }
}
