package blusunrize.conveyors.common;



import java.util.List;

import blusunrize.conveyors.ImmersiveEngineering;

import blusunrize.conveyors.common.blocks.BlockIEBase;
import blusunrize.conveyors.common.blocks.BlockIEBase.BlockIESimple;
import blusunrize.conveyors.common.blocks.ItemBlockIEBase;
import blusunrize.conveyors.common.blocks.metal.BlockMetalDevices;
import blusunrize.conveyors.common.blocks.metal.TileEntityConveyorBelt;
import blusunrize.conveyors.common.blocks.metal.TileEntityConveyorSorter;
import blusunrize.conveyors.common.items.ItemIEBase;
import blusunrize.conveyors.common.items.ItemIETool;
import blusunrize.conveyors.common.util.IELogger;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class IEContent
{

	public static BlockIEBase blockMetalDevice;


	public static ItemIEBase itemTool;


	public static void preInit()
	{
		blockMetalDevice = new BlockMetalDevices();



		itemTool = new ItemIETool();
		

		
	}

	public static void init()
	{
		/**TILEENTITIES*/
		registerTile(TileEntityConveyorBelt.class);

		registerTile(TileEntityConveyorSorter.class);
		
		/**CRAFTING*/
		IERecipes.initCraftingRecipes();	


	}

	public static void postInit()
	{

	}

	public static void registerToOreDict(String type, ItemIEBase item, int... metas)
	{
		if(metas==null||metas.length<1)
			for(int meta=0; meta<item.subNames.length; meta++)
				OreDictionary.registerOre(type+item.subNames[meta], new ItemStack(item,1,meta));
		else
			for(int meta: metas)
				OreDictionary.registerOre(type+item.subNames[meta], new ItemStack(item,1,meta));
	}
	public static void registerToOreDict(String type, BlockIEBase item, int... metas)
	{
		if(metas==null||metas.length<1)
			for(int meta=0; meta<item.subNames.length; meta++)
				OreDictionary.registerOre(type+item.subNames[meta], new ItemStack(item,1,meta));
		else
			for(int meta: metas)
				OreDictionary.registerOre(type+item.subNames[meta], new ItemStack(item,1,meta));
	}
	public static void registerOre(String type, ItemStack ore, ItemStack ingot, ItemStack dust, ItemStack block, ItemStack nugget)
	{
		if(ore!=null)
			OreDictionary.registerOre("ore"+type, ore);
		if(ingot!=null)
			OreDictionary.registerOre("ingot"+type, ingot);
		if(dust!=null)
			OreDictionary.registerOre("dust"+type, dust);
		if(block!=null)
			OreDictionary.registerOre("block"+type, block);
		if(nugget!=null)
			OreDictionary.registerOre("nugget"+type, nugget);
	}

	public static void registerTile(Class<? extends TileEntity> tile)
	{
		String s = tile.getSimpleName();
		s = s.substring(s.indexOf("TileEntity")+"TileEntity".length());
		GameRegistry.registerTileEntity(tile, ImmersiveEngineering.MODID+":"+ s);
	}


}
