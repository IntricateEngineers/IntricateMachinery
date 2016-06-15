package intricateengineers.intricatemachinery.common.util.gui

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.Item

object IMCreativeTab {
    val INSTANCE: IMCreativeTab = new IMCreativeTab(CreativeTabs.getNextID, "tabIntricateMachinery")
}

class IMCreativeTab(val index: Int, val label: String) extends CreativeTabs(index, label) {
    def getTabIconItem: Item = {
        return Item.getItemFromBlock(Blocks.PISTON)
    }
}