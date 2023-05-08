package gregicality.multiblocks;

import gregicality.multiblocks.api.fluids.GCYMMetaFluids;
import gregicality.multiblocks.api.unification.GCYMMaterialFlagAddition;
import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregicality.multiblocks.api.unification.properties.GCYMLatePropertyAddition;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.fluids.MetaFluids;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.materials.MaterialFlagAddition;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.CoreModManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Bootstrap {

    private static boolean bootstrapped = false;

    private Bootstrap() {/**/}

    public static void perform() {
        if (bootstrapped) {
            return;
        }
        try {
            Field deobfuscatedEnvironment = CoreModManager.class.getDeclaredField("deobfuscatedEnvironment");
            deobfuscatedEnvironment.setAccessible(true);
            deobfuscatedEnvironment.setBoolean(null, true);
            Method setLocale = I18n.class.getDeclaredMethod("setLocale", Locale.class);
            setLocale.setAccessible(true);
            setLocale.invoke(null, new Locale());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        net.minecraft.init.Bootstrap.register();
        ModMetadata meta = new ModMetadata();
        meta.modId = GTValues.MODID;
        Loader.instance().setupTestHarness(new DummyModContainer(meta));

        GregTechAPI.MATERIAL_REGISTRY.unfreeze();

        Materials.register();
        MaterialFlagAddition.register();

        meta = new ModMetadata();
        meta.modId = GTValues.MODID;
        Loader.instance().setupTestHarness(new DummyModContainer(meta));

        GCYMMaterials.init();
        GCYMMaterialFlagAddition.init();

        GCYMLatePropertyAddition.init();
        GCYMMaterialFlagAddition.initLate();

        GregTechAPI.MATERIAL_REGISTRY.freeze();

        OrePrefix.runMaterialHandlers();
        MetaFluids.init();
        GCYMMetaFluids.init();
        MetaItems.init();
        ModHandler.init();
        bootstrapped = true;
    }
}