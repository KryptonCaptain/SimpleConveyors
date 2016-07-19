package blusunrize.conveyors.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import blusunrize.conveyors.ImmersiveEngineering;
import blusunrize.conveyors.api.ApiUtils;
import blusunrize.conveyors.api.IEApi;
import blusunrize.conveyors.client.gui.GuiSorter;
import blusunrize.conveyors.client.render.BlockRenderMetalDevices;
import blusunrize.conveyors.client.render.EntityRenderNone;
import blusunrize.conveyors.common.CommonProxy;
import blusunrize.conveyors.common.IEContent;
import blusunrize.conveyors.common.IERecipes;
import blusunrize.conveyors.common.blocks.metal.BlockMetalDevices;
import blusunrize.conveyors.common.blocks.metal.TileEntityConveyorSorter;
import blusunrize.conveyors.common.util.Lib;
import blusunrize.conveyors.common.util.Utils;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ClientProxy extends CommonProxy
{
	public static TextureMap revolverTextureMap;
	public static final ResourceLocation revolverTextureResource = new ResourceLocation("textures/atlas/immersiveengineering/revolvers.png");
	public static FontRenderer nixieFontOptional;


	@Override
	public void init()
	{

		//METAL
		RenderingRegistry.registerBlockHandler(new BlockRenderMetalDevices());

		ClientEventHandler handler = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}

	@Override
	public void postInit()
	{
		
	}
	@Override
	public void serverStarting()
	{
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		if(ID==Lib.GUIID_Sorter && te instanceof TileEntityConveyorSorter)
			return new GuiSorter(player.inventory, (TileEntityConveyorSorter) te);

		return null;
	}

	
	@Override
	public void spawnRedstoneFX(World world, double x, double y, double z, double mx, double my, double mz, float size, float r, float g, float b)
	{
		EntityReddustFX particle = new EntityReddustFX(world, x,y,z, size, 0,0,0);
		particle.motionX*=mx;
		particle.motionY*=my;
		particle.motionZ*=mz;
		particle.setRBGColorF(r,g,b);
		ClientUtils.mc().effectRenderer.addEffect(particle);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);
	}

	@Override
	public void draw3DBlockCauldron()
	{
		RenderBlocks blockRender = RenderBlocks.getInstance();
		blockRender.setRenderBounds(0, 0, 0, 1, 1, 1);
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addTranslation(-.5f, -.5f, -.5f);
		blockRender.renderFaceYPos(Blocks.cauldron, 0,0,0, Blocks.cauldron.getBlockTextureFromSide(1));
		IIcon icon = Blocks.cauldron.getBlockTextureFromSide(2);
		blockRender.renderFaceXNeg(Blocks.cauldron, 0,0,0, icon);
		blockRender.renderFaceXPos(Blocks.cauldron, 0,0,0, icon);
		blockRender.renderFaceZNeg(Blocks.cauldron, 0,0,0, icon);
		blockRender.renderFaceZPos(Blocks.cauldron, 0,0,0, icon);
		float f4 = 0.125F;
		blockRender.renderFaceXPos(Blocks.cauldron, ((float)0 - 1.0F + f4), (double)0, (double)0, icon);
		blockRender.renderFaceXNeg(Blocks.cauldron, (double)((float)0 + 1.0F - f4), (double)0, (double)0, icon);
		blockRender.renderFaceZPos(Blocks.cauldron, (double)0, (double)0, (double)((float)0 - 1.0F + f4), icon);
		blockRender.renderFaceZNeg(Blocks.cauldron, (double)0, (double)0, (double)((float)0 + 1.0F - f4), icon);
		IIcon iicon1 = BlockCauldron.getCauldronIcon("inner");
		blockRender.renderFaceYPos(Blocks.cauldron, (double)0, (double)((float)0 - 1.0F + 0.25F), (double)0, iicon1);
		blockRender.renderFaceYNeg(Blocks.cauldron, (double)0, (double)((float)0 + 1.0F - 0.75F), (double)0, iicon1);
		Tessellator.instance.addTranslation(.5f, .5f, .5f);
		Tessellator.instance.draw();
	}

	static String[][] formatToTable_ItemIntHashmap(Map<String, Integer> map, String valueType)
	{
		Map.Entry<String,Integer>[] sortedMapArray = map.entrySet().toArray(new Map.Entry[0]);
		ArrayList<String[]> list = new ArrayList();
		try{
			for(int i=0; i<sortedMapArray.length; i++)
			{
				String item = null;
				if(ApiUtils.isExistingOreName(sortedMapArray[i].getKey()))
				{
					ItemStack is = OreDictionary.getOres(sortedMapArray[i].getKey()).get(0);
					if(is!=null)
						item = is.getDisplayName();
				}
				else if(sortedMapArray[i].getKey().contains("::"))
				{
					String[] split = sortedMapArray[i].getKey().split("::");
					Item it = GameData.getItemRegistry().getObject(split[0]);
					int meta = 0;
					try{meta = Integer.parseInt(split[1]);}catch(Exception e){}
					if(it!=null)
						item = new ItemStack(it, 1, meta).getDisplayName();
				}
				else
					item = sortedMapArray[i].getKey();

				if(item!=null)
				{
					int bt = sortedMapArray[i].getValue();
					String am = bt+" "+valueType;
					list.add(new String[]{item,am});
				}
			}
		}catch(Exception e)	{}
		String[][] table = list.toArray(new String[0][]);
		return table;
	}


	@Override
	public String[] splitStringOnWidth(String s, int w)
	{
		return ((List<String>)ClientUtils.font().listFormattedStringToWidth(s, w)).toArray(new String[0]);
	}

	@Override
	public World getClientWorld()
	{
		return ClientUtils.mc().theWorld;
	}

	@Override
	public String getNameFromUUID(String uuid)
	{
		return Minecraft.getMinecraft().func_152347_ac().fillProfileProperties(new GameProfile(UUID.fromString(uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), null), false).getName();
	}
}