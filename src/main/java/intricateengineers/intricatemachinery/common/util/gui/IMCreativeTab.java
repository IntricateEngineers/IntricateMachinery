package intricateengineers.intricatemachinery.common.util.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

/**
 * @author topisani
 */
public class IMCreativeTab extends CreativeTabs {

    public static final IMCreativeTab INSTANCE = new IMCreativeTab();

    public IMCreativeTab() {
        super("Intricate Machinery");
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(Blocks.PISTON);
    }
}
