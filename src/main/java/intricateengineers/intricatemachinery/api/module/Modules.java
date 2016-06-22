package intricateengineers.intricatemachinery.api.module;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;

import net.minecraft.util.ResourceLocation;

public class Modules {

    private static Map<ResourceLocation, Function<MachineryFrame, Module>> registry = new HashMap<>();


    public static void registerModule(ResourceLocation name, Function<MachineryFrame, Module> newModule) {
        registry.put(name, newModule);
    }

    public static Module newModule(ResourceLocation name, MachineryFrame frame) {
        try {
            return registry.get(name).apply(frame);
        } catch (Exception e) {
            return null;
        }
    }

}
