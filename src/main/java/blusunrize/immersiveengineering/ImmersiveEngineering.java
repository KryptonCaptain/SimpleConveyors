package blusunrize.immersiveengineering;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

import blusunrize.immersiveengineering.api.IEApi;

import blusunrize.immersiveengineering.common.CommonProxy;


import blusunrize.immersiveengineering.common.IEContent;

import blusunrize.immersiveengineering.common.util.IELogger;
import blusunrize.immersiveengineering.common.util.Lib;

import blusunrize.immersiveengineering.common.util.network.MessageRequestBlockUpdate;

import blusunrize.immersiveengineering.common.util.network.MessageTileSync;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid=ImmersiveEngineering.MODID,name=ImmersiveEngineering.MODNAME,version = ImmersiveEngineering.VERSION, dependencies="")
public class ImmersiveEngineering
{
	public static final String MODID = "s_conveyors";
	public static final String MODNAME = "Simple Conveyors";
	public static final String VERSION = "1.0";
	

	@Mod.Instance(MODID)
	public static ImmersiveEngineering instance = new ImmersiveEngineering();
	@SidedProxy(clientSide="blusunrize.immersiveengineering.client.ClientProxy", serverSide="blusunrize.immersiveengineering.common.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		IELogger.debug = VERSION.startsWith("${");
		IEContent.preInit();


	}
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		IEContent.init();


		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.init();

		int messageId = 0;
		packetHandler.registerMessage(MessageTileSync.Handler.class, MessageTileSync.class, messageId++, Side.SERVER);
		packetHandler.registerMessage(MessageRequestBlockUpdate.Handler.class, MessageRequestBlockUpdate.class, messageId++, Side.SERVER);

	}
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		IEContent.postInit();

		proxy.postInit();

	}
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{

	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		proxy.serverStarting();

	}
	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{

	}

	public static CreativeTabs creativeTab = new CreativeTabs(MODID)
	{
		@Override
		public Item getTabIconItem()
		{
			return null;
		}
		@Override
		public ItemStack getIconItemStack()
		{
			return new ItemStack(IEContent.blockMetalDevice,1,0);
		}
	};


}
