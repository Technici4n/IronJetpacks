package com.blakebr0.ironjetpacks;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.ModRecipeSerializers;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.ModItems;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.sound.ModSounds;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.api.common.events.v1.PlayerChangeWorldCallback;
import me.shedaniel.cloth.api.common.events.v1.PlayerLeaveCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public class IronJetpacks implements ModInitializer {
    public static final String MOD_ID = "iron-jetpacks";
    public static final String NAME = "Iron Jetpacks";
    
    public static Supplier<MinecraftServer> serverSupplier = () -> {
        Object instance = FabricLoader.getInstance().getGameInstance();
        if (instance instanceof MinecraftDedicatedServer)
            return (MinecraftServer) instance;
        return null;
    };
    
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(MOD_ID, MOD_ID))
            .icon(() -> {
                return new ItemStack(ModItems.STRAP.get());
            })
            .build();
    
    @Override
    public void onInitialize() {
        ModItems.register();
        ModSounds.register();
        ModRecipeSerializers.register();
        ModRecipeSerializers.onCommonSetup();
        
        NetworkHandler.onCommonSetup();
        
        AutoConfig.register(ModConfigs.Common.class, JanksonConfigSerializer::new);
        
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            try {
                Class.forName("com.blakebr0.ironjetpacks.client.IronJetpacksClient").getDeclaredMethod("onInitializeClient").invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (FabricLoader.getInstance().isModLoaded("fasttransferlib")) {
            try {
                Class.forName("com.blakebr0.ironjetpacks.compat.ftl.FtlCompat").getMethod("init").invoke(null);
            } catch (Exception e) {
                LogManager.getLogger(NAME).error("Exception thrown while loading FTL energy compat: " + e);
            }
        }
        
        ServerStopCallback.EVENT.register(server -> InputHandler.clear());
        PlayerChangeWorldCallback.EVENT.register((playerEntity, oldWorld, newWorld) -> InputHandler.onChangeDimension(playerEntity));
        PlayerLeaveCallback.EVENT.register(InputHandler::onLogout);
    }
}
