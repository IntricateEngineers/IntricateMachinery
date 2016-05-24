/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intricateengineers.intricatemachinery.common.util.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

/**
 * @author topisani
 */
public class IMCreativeTab extends CreativeTabs {

    public static final IMCreativeTab INSTANCE = new IMCreativeTab(CreativeTabs.getNextID(), "tabIntricateMachinery");

    public IMCreativeTab(int index, String label) {
        super(index, label);
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(Blocks.PISTON);
    }
}
