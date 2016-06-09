package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.api.client.QuadHandler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModuleModel extends ModelBase {
    @SideOnly(Side.CLIENT)
    private QuadHandler quadHandler;

    @SideOnly(Side.CLIENT)
    public QuadHandler getQuadHandler(){
        if (this.quadHandler == null) {
            this.quadHandler = new QuadHandler(this);
        }    
        return this.quadHandler;
    }
}
