package com.vonbraunz.apogtnh.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // TODO(scaffold): client-side texture / renderer registration
        // - Item textures via ItemStack.getItem().setTextureName in item ctor is fine
        // - TESRs register here with ClientRegistry.bindTileEntitySpecialRenderer
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // TODO(scaffold): elite marker particle handler subscription (client-only)
    }
}
