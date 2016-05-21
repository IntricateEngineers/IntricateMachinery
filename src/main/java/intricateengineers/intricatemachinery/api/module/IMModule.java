package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.core.ModInfo;
import mcmultipart.multipart.Multipart;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * @author topisani
 */
public class IMModule extends Multipart {
    public static ResourceLocation partType = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "machinery_frame");

    public IMModule(String name, IMModel model) {

    }
    @Override
    public ResourceLocation getType() {

        return partType;
    }

    /**
     * Adds the selection boxes used to ray trace this part.
     */
    @Override
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
        list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
    }

    @Override
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
    }
}
