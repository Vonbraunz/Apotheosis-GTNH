package com.vonbraunz.apogtnh;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import com.vonbraunz.apogtnh.affix.AffixRegistry;
import com.vonbraunz.apogtnh.affix.ToolAffixEventHandler;
import com.vonbraunz.apogtnh.compat.TConstructAffixHandler;
import com.vonbraunz.apogtnh.deadly.DeadlyEventHandler;
import com.vonbraunz.apogtnh.proxy.CommonProxy;
import com.vonbraunz.apogtnh.reforge.AnvilHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ApotheosisGTNH.MODID, name = ApotheosisGTNH.NAME, version = ApotheosisGTNH.VERSION)
public class ApotheosisGTNH {

    public static final String MODID = "apogtnh";
    public static final String NAME = "ApotheosisGTNH";
    public static final String VERSION = "2.0.0";

    @Instance(MODID)
    public static ApotheosisGTNH instance;

    // creative tab that shows the rarity material as its icon
    public static final CreativeTabs creativeTab = new CreativeTabs(MODID) {

        @Override
        public Item getTabIconItem() {
            return ModContent.rarityMaterial;
        }
    };

    @SidedProxy(
        clientSide = "com.vonbraunz.apogtnh.proxy.ClientProxy",
        serverSide = "com.vonbraunz.apogtnh.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ApoConfig.load(event.getSuggestedConfigurationFile());
        AffixRegistry.bootstrap();
        ModContent.preInit();
        com.vonbraunz.apogtnh.reforge.item.ItemAffixBook.initOrder();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DeadlyEventHandler());
        MinecraftForge.EVENT_BUS.register(new ToolAffixEventHandler());
        MinecraftForge.EVENT_BUS.register(new AnvilHandler());

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
