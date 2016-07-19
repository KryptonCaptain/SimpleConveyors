package blusunrize.conveyors.common.gui;

import java.util.List;


import blusunrize.conveyors.common.IEContent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

public abstract class IESlot extends Slot
{
	final Container container;
	public IESlot(Container container, IInventory inv, int id, int x, int y)
	{
		super(inv, id, x, y);
		this.container=container;
	}

	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		return true;
	}

	public static class Output extends IESlot
	{
		public Output(Container container, IInventory inv, int id, int x, int y)
		{
			super(container, inv, id, x, y);
		}
		@Override
		public boolean isItemValid(ItemStack itemStack)
		{
			return false;
		}

	}
	public static class FluidContainer extends IESlot
	{
		boolean empty;
		public FluidContainer(Container container, IInventory inv, int id, int x, int y, boolean empty)
		{
			super(container, inv, id, x, y);
			this.empty=empty;
		}
		@Override
		public boolean isItemValid(ItemStack itemStack)
		{
			if(empty)
				return FluidContainerRegistry.isEmptyContainer(itemStack);
			else
				return FluidContainerRegistry.isFilledContainer(itemStack);
		}
	}



	public static class Ghost extends IESlot
	{
		public Ghost(Container container, IInventory inv, int id, int x, int y)
		{
			super(container, inv, id, x, y);
		}

		@Override
		public void putStack(ItemStack itemStack)
		{
			super.putStack(itemStack);
		}
		@Override
		public boolean canTakeStack(EntityPlayer player)
		{
			return false;
		}
		@Override
		public int getSlotStackLimit()
		{
			return 1;
		}
	}
	public static class ItemDisplay extends IESlot
	{
		public ItemDisplay(Container container, IInventory inv, int id, int x, int y)
		{
			super(container, inv, id, x, y);
		}
		@Override
		public boolean isItemValid(ItemStack itemStack)
		{
			return false;
		}
		@Override
		public boolean canTakeStack(EntityPlayer player)
		{
			return false;
		}
	}

	public static class ContainerCallback extends IESlot
	{
		public ContainerCallback(Container container, IInventory inv, int id, int x, int y)
		{
			super(container, inv, id, x, y);
		}
		@Override
		public boolean isItemValid(ItemStack itemStack)
		{
			if(this.container instanceof ICallbackContainer)
				return ((ICallbackContainer)this.container).canInsert(itemStack, slotNumber, this);
			return true;
		}
		@Override
		public boolean canTakeStack(EntityPlayer player)
		{
			if(this.container instanceof ICallbackContainer)
				return ((ICallbackContainer)this.container).canTake(this.getStack(), slotNumber, this);
			return true;
		}
	}
	public static interface ICallbackContainer
	{
		public boolean canInsert(ItemStack stack, int slotNumer, Slot slotObject);
		public boolean canTake(ItemStack stack, int slotNumer, Slot slotObject);
	}
}