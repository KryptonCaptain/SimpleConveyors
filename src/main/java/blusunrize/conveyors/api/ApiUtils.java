package blusunrize.conveyors.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameData;

public class ApiUtils
{
	public static boolean compareToOreName(ItemStack stack, String oreName)
	{
		if(!isExistingOreName(oreName))
			return false;
		ItemStack comp = copyStackWithAmount(stack, 1);
		ArrayList<ItemStack> s = OreDictionary.getOres(oreName);
		for (ItemStack st:s)
			if (ItemStack.areItemStacksEqual(comp, st))
				return true;
		return false;
	}
	public static boolean stackMatchesObject(ItemStack stack, Object o)
	{
		if(o instanceof ItemStack)
			return OreDictionary.itemMatches((ItemStack)o, stack, false);
		else if(o instanceof ArrayList)
		{
			for(Object io : (ArrayList)o)
				if(io instanceof ItemStack && OreDictionary.itemMatches((ItemStack)io, stack, false))
					return true;
		}
		else if(o instanceof String)
			return compareToOreName(stack, (String)o);
		return false;
	}
	public static ItemStack copyStackWithAmount(ItemStack stack, int amount)
	{
		if(stack==null)
			return null;
		ItemStack s2 = stack.copy();
		s2.stackSize=amount;
		return s2;
	}
	
	public static boolean isExistingOreName(String name)
	{
		if(!OreDictionary.doesOreNameExist(name))
			return false;
		else
			return !OreDictionary.getOres(name).isEmpty();
	}

	public static String nameFromStack(ItemStack stack)
	{
		if(stack==null)
			return "";
		try
		{
			return GameData.getItemRegistry().getNameForObject(stack.getItem());
		}
		catch (NullPointerException e) {}
		return "";
	}


	public static boolean isMetalComponent(ItemStack stack, String componentType)
	{
		return getMetalComponentType(stack, componentType)!=null;
	}
	public static String getMetalComponentType(ItemStack stack, String... componentTypes)
	{
		ItemStack comp = copyStackWithAmount(stack, 1);
		for(String oreName : OreDictionary.getOreNames())//This is super ugly, but I don't want to force the latest forge ._.
			for(int iType=0; iType<componentTypes.length; iType++)
				if(oreName.startsWith(componentTypes[iType]))
				{
					ArrayList<ItemStack> s = OreDictionary.getOres(oreName);
					for(ItemStack st : s)
						if(ItemStack.areItemStacksEqual(comp, st))
							return componentTypes[iType];
				}
		return null;
	}
	public static String[] getMetalComponentTypeAndMetal(ItemStack stack, String... componentTypes)
	{
		ItemStack comp = copyStackWithAmount(stack, 1);
		for(String oreName : OreDictionary.getOreNames())//This is super ugly, but I don't want to force the latest forge ._.
			for(int iType=0; iType<componentTypes.length; iType++)
				if(oreName.startsWith(componentTypes[iType]))
				{
					ArrayList<ItemStack> s = OreDictionary.getOres(oreName);
					for(ItemStack st : s)
						if(ItemStack.areItemStacksEqual(comp, st))
							return new String[]{componentTypes[iType], oreName.substring(componentTypes[iType].length())};
				}
		return null;
	}
	public static boolean isIngot(ItemStack stack)
	{
		return isMetalComponent(stack, "ingot");
	}
	public static boolean isPlate(ItemStack stack)
	{
		return isMetalComponent(stack, "plate");
	}
	public static int getComponentIngotWorth(ItemStack stack)
	{
		String[] keys = IEApi.prefixToIngotMap.keySet().toArray(new String[IEApi.prefixToIngotMap.size()]);
		String key = getMetalComponentType(stack, keys);
		if(key!=null)
		{
			Integer[] relation = IEApi.prefixToIngotMap.get(key);
			if(relation!=null && relation.length>1)
			{
				double val = relation[0]/(double)relation[1];
				return (int)val;
			}
		}
		return 0;
	}
	public static ItemStack breakStackIntoIngots(ItemStack stack)
	{
		String[] keys = IEApi.prefixToIngotMap.keySet().toArray(new String[IEApi.prefixToIngotMap.size()]);
		String[] type = getMetalComponentTypeAndMetal(stack, keys);
		if(type!=null)
		{
			Integer[] relation = IEApi.prefixToIngotMap.get(type[0]);
			if(relation!=null && relation.length>1)
			{
				double val = relation[0]/(double)relation[1];
				return copyStackWithAmount(IEApi.getPreferredOreStack("ingot"+type[1]), (int)val);
			}
		}
		return null;
	}
	public static Object[] breakStackIntoPreciseIngots(ItemStack stack)
	{
		String[] keys = IEApi.prefixToIngotMap.keySet().toArray(new String[IEApi.prefixToIngotMap.size()]);
		String[] type = getMetalComponentTypeAndMetal(stack, keys);
		if(type!=null)
		{
			Integer[] relation = IEApi.prefixToIngotMap.get(type[0]);
			if(relation!=null && relation.length>1)
			{
				double val = relation[0]/(double)relation[1];
				return new Object[]{IEApi.getPreferredOreStack("ingot"+type[1]),val};
			}
		}
		return null;
	}



	public static Vec3 addVectors(Vec3 vec0, Vec3 vec1)
	{
		return vec0.addVector(vec1.xCoord,vec1.yCoord,vec1.zCoord);
	}

	

	public static Object convertToValidRecipeInput(Object input)
	{
		if(input instanceof ItemStack)
			return input;
		else if(input instanceof Item)
			return new ItemStack((Item)input);
		else if(input instanceof Block)
			return new ItemStack((Block)input);
		else if(input instanceof ArrayList)
			return input;
		else if(input instanceof String)
		{
			if(!ApiUtils.isExistingOreName((String)input))
				return null;
			ArrayList<ItemStack> l = OreDictionary.getOres((String)input);
			if(!l.isEmpty())
				return l;
			else
				return null;
		}
		else
			throw new RuntimeException("Recipe Inputs must always be ItemStack, Item, Block or String (OreDictionary name), "+input+" is invalid");
	}

	public static ItemStack getItemStackFromObject(Object o)
	{
		if(o instanceof ItemStack)
			return (ItemStack)o;
		else if(o instanceof Item)
			return new ItemStack((Item)o);
		else if(o instanceof Block)
			return new ItemStack((Block)o);
		else if(o instanceof ArrayList)
			return ((ArrayList<ItemStack>)o).get(0);
		else if(o instanceof String)
		{
			if(!isExistingOreName((String)o))
				return null;
			ArrayList<ItemStack> l = OreDictionary.getOres((String)o);
			if(!l.isEmpty())
				return l.get(0);
			else
				return null;
		}
		return null;
	}

	public static Map<String, Integer> sortMap(Map<String, Integer> map, boolean inverse)
	{
		TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(new ValueComparator(map, inverse));
		sortedMap.putAll(map);
		return sortedMap;
	}
	static class ValueComparator implements Comparator<String>
	{
		Map<String, Integer> base;
		boolean inverse;
		public ValueComparator(Map<String, Integer> base, boolean inverse)
		{
			this.base = base;
			this.inverse = inverse;
		}
		@Override
		//Cant return equal to keys separate
		public int compare(String s0, String s1)
		{
			if(inverse)
			{
				if (base.get(s0) <= base.get(s1))
					return -1;
				else
					return 1;
			}
			else
			{
				if (base.get(s0) >= base.get(s1))
					return -1;
				else
					return 1;
			}
		}
	}
}