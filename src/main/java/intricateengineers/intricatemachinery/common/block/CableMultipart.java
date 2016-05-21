package intricateengineers.intricatemachinery.common.block;

import mcmultipart.multipart.Multipart;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * @author topisani
 */
public class CableMultipart extends Multipart {

    public static ResourceLocation partType = new ResourceLocation("intricate_machinery", "machinery_frame");
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
