package intricateengineers.intricatemachinery.common.init;

import intricateengineers.intricatemachinery.common.block.CableMultipart;
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static intricateengineers.intricatemachinery.api.IMBlocks.CABLE_ITEMS;

/**
 * @author topisani
 */
public final class ModBlocks {

    public static void init() {
        CABLE_ITEMS = registerMultipart(new CableMultipart(), CableMultipart.partType);
    }

    private static Multipart registerMultipart(Multipart multipart, ResourceLocation name) {
        ItemMultiPart item = new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return multipart;
            }
        };
        item.setUnlocalizedName(name.getResourcePath());
        item.setCreativeTab(IMCreativeTab.INSTANCE);
        GameRegistry.<Item>register(item, name);
        MultipartRegistry.registerPart(multipart.getClass(), name.toString());
        return multipart;
    }
}
