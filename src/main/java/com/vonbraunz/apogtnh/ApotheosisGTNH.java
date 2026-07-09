package com.vonbraunz.apogtnh;

import net.minecraftforge.common.MinecraftForge;

import com.vonbraunz.apogtnh.affix.AffixRegistry;
import com.vonbraunz.apogtnh.compat.TConstructAffixHandler;
import com.vonbraunz.apogtnh.deadly.DeadlyEventHandler;
import com.vonbraunz.apogtnh.proxy.CommonProxy;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ApotheosisGTNH.MODID, name = ApotheosisGTNH.NAME, version = ApotheosisGTNH.VERSION)
public class ApotheosisGTNH {

    public static final String MODID = "apogtnh";
    public static final String NAME = "ApotheosisGTNH";
    public static final String VERSION = "0.1.0";

    @SidedProxy(
        clientSide = "com.vonbraunz.apogtnh.proxy.ClientProxy",
        serverSide = "com.vonbraunz.apogtnh.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ApoConfig.load(event.getSuggestedConfigurationFile());
        AffixRegistry.bootstrap();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DeadlyEventHandler());

        if (Loader.isModLoaded("TConstruct")) {
            MinecraftForge.EVENT_BUS.register(new TConstructAffixHandler());
        }

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
