package blusunrize.immersiveengineering.common;

import java.util.UUID;

import com.mojang.authlib.GameProfile;


import blusunrize.immersiveengineering.common.blocks.metal.TileEntityConveyorSorter;

import blusunrize.immersiveengineering.common.gui.ContainerSorter;

import blusunrize.immersiveengineering.common.util.Lib;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler
{
	public void init(){}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		if(ID==Lib.GUIID_Sorter && te instanceof TileEntityConveyorSorter)
			return new ContainerSorter(player.inventory, (TileEntityConveyorSorter) te);

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	public void postInit()
	{
	}
	public void serverStarting()
	{
	}

	public void handleTileSound(String soundName, TileEntity tile, boolean tileActive, float volume, float pitch)
	{
	}
	public void stopTileSound(String soundName, TileEntity tile)
	{
	}

	public void spawnSparkFX(World world, double x, double y, double z, double mx, double my, double mz)
	{
	}
	public void spawnRedstoneFX(World world, double x, double y, double z, double mx, double my, double mz, float size, float r, float g, float b)
	{
	}
	public void draw3DBlockCauldron()
	{
	}
	public String[] splitStringOnWidth(String s, int w)
	{
		return new String[]{s};
	}
	public World getClientWorld()
	{
		return null;
	}
	public String getNameFromUUID(String uuid)
	{
		return MinecraftServer.getServer().func_147130_as().fillProfileProperties(new GameProfile(UUID.fromString(uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), null), false).getName();
	}
}