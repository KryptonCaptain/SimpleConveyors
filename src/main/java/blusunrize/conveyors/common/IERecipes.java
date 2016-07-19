package blusunrize.conveyors.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.ArrayListMultimap;

import blusunrize.conveyors.ImmersiveEngineering;
import blusunrize.conveyors.api.ApiUtils;
import blusunrize.conveyors.api.IEApi;

import blusunrize.conveyors.common.blocks.metal.BlockMetalDevices;

import blusunrize.conveyors.common.util.Utils;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class IERecipes
{

	public static void initCraftingRecipes()
	{
		addOredictRecipe(new ItemStack(IEContent.blockMetalDevice,8, BlockMetalDevices.META_conveyorBelt), 
				"LLL","IRI", 'I',"ingotIron",'R',"dustRedstone",'L',Items.leather);
		addOredictRecipe(new ItemStack(IEContent.blockMetalDevice,1, BlockMetalDevices.META_sorter), 
				"IRI","WBW","IRI", 'I',"ingotIron",'R',"dustRedstone",'W',"plankWood",'B',Blocks.chest);
		addOredictRecipe(new ItemStack(IEContent.blockMetalDevice,1, BlockMetalDevices.META_conveyorDropper), 
				"C","H", 'C',new ItemStack(IEContent.blockMetalDevice,1,BlockMetalDevices.META_conveyorBelt),'H',Blocks.hopper);
		
		addOredictRecipe(new ItemStack(IEContent.itemTool,1,0), 
				" IF"," SI","S  ", 'I',"ingotIron", 'S',"stickWood", 'F',new ItemStack(Items.string));

	}
	
	public static ShapedOreRecipe addOredictRecipe(ItemStack output, Object... recipe)
	{
		ShapedOreRecipe sor = new ShapedOreRecipe(output, recipe);
		GameRegistry.addRecipe(sor);
		return sor;
	}
	
	public static void addShapelessOredictRecipe(ItemStack output, Object... recipe)
	{
		GameRegistry.addRecipe(new ShapelessOreRecipe(output, recipe));
	}


}