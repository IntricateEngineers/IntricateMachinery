package intricateengineers.intricatemachinery.api.module;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class Modules {

    private static Map<ResourceLocation, Class<? extends Module>> registry = new HashMap<>();


    public static void registerModule(ResourceLocation name, Class<? extends Module> moduleClass) {
        registry.put(name, moduleClass);
    }

    public static Module newModule(ResourceLocation name) {
        try {
            return registry.get(name).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

}
