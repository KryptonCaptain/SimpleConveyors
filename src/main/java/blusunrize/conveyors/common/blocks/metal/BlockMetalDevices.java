package blusunrize.conveyors.common.blocks.metal;

import java.util.ArrayList;
import java.util.List;

import blusunrize.conveyors.ImmersiveEngineering;

import blusunrize.conveyors.client.render.BlockRenderMetalDevices;
import blusunrize.conveyors.common.IEContent;
import blusunrize.conveyors.common.blocks.BlockIEBase;
import blusunrize.conveyors.common.util.ItemNBTHelper;
import blusunrize.conveyors.common.util.Lib;
import blusunrize.conveyors.common.util.Utils;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMetalDevices extends BlockIEBase 
{

	public IIcon[] icons_sorter = new IIcon[6];

	
	public static final int META_conveyorBelt=0;
	public static final int META_conveyorDropper=1;
	public static final int META_sorter=2;

	
	
	public BlockMetalDevices()
	{
		super("metalDevice", Material.iron, 4, ItemBlockMetalDevices.class,
				"conveyorBelt", "conveyorDropper", "sorter"
				);
		setHardness(3.0F);
		setResistance(15.0F);

		this.setMetaLightOpacity(META_sorter, 255);
	}


	@Override
	public boolean allowHammerHarvest(int meta)
	{
		return true;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		return super.getPickBlock(target, world, x, y, z, player);
	}
	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{
		TileEntity te = world.getTileEntity(x, y, z);

	
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		
		//11 conveyorBelt
		icons[0][0] = iconRegister.registerIcon("conveyors:metal_conveyor_top");
		icons[0][1] = iconRegister.registerIcon("conveyors:metal_conveyor_top");
		icons[0][2] = iconRegister.registerIcon("conveyors:metal_dynamo_bottom");
		icons[0][3] = iconRegister.registerIcon("conveyors:metal_dynamo_bottom");

		//13 sorter
		for(int i=0; i<6; i++)
			icons_sorter[i] = iconRegister.registerIcon("conveyors:metal_sorter_"+i);
		//15 conveyorDropper
		icons[META_conveyorDropper][0] = iconRegister.registerIcon("conveyors:metal_conveyor_dropper");
		icons[META_conveyorDropper][1] = iconRegister.registerIcon("conveyors:metal_conveyor_dropper");
		icons[META_conveyorDropper][2] = iconRegister.registerIcon("conveyors:metal_dynamo_bottom");
		icons[META_conveyorDropper][3] = iconRegister.registerIcon("conveyors:metal_dynamo_bottom");


	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity te = world.getTileEntity(x, y, z);


		if(te instanceof TileEntityConveyorBelt && (((TileEntityConveyorBelt)te).facing==side || ((TileEntityConveyorBelt)te).facing==ForgeDirection.OPPOSITES[side]))
		{
			if(((TileEntityConveyorBelt) te).dropping)
				return icons[META_conveyorDropper][1];
			else
				return icons[META_conveyorBelt][1];
		}

		if(world.getBlockMetadata(x, y, z) == META_sorter)
			return icons_sorter[side];

		return super.getIcon(world, x, y, z, side);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(meta == META_sorter)
			return icons_sorter[side];
		return super.getIcon(side, meta);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	@Override
	public int getRenderType()
	{
		return BlockRenderMetalDevices.renderID;
	}


	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		if(te instanceof TileEntityConveyorBelt && Utils.isHammer(player.getCurrentEquippedItem()))
		{
			if(!world.isRemote)
			{
				TileEntityConveyorBelt tile = (TileEntityConveyorBelt) te;
				if(player.isSneaking())
				{
					if(tile.transportUp)
					{
						tile.transportUp = false;
						tile.transportDown = true;
					}
					else if(tile.transportDown)
					{
						tile.transportDown = false;
					}
					else
						tile.transportUp = true;
				}
				else
					tile.facing = ForgeDirection.ROTATION_MATRIX[1][tile.facing];
				world.markBlockForUpdate(x, y, z);
			}
			return true;
		}

		if(te instanceof TileEntityConveyorSorter)
		{
			if(!player.isSneaking())
			{
				player.openGui(ImmersiveEngineering.instance, Lib.GUIID_Sorter, world, x, y, z);
				return true;
			}
		}

		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);

		if(te instanceof TileEntityConveyorBelt)
		{
			TileEntityConveyorBelt tile = (TileEntityConveyorBelt) te;
			this.setBlockBounds(0F, 0F, 0F, 1F, tile.transportDown||tile.transportUp?1.125f:0.125F, 1F);
		}
		else
			this.setBlockBounds(0,0,0,1,1,1);
	}
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		if(world.getBlockMetadata(x, y, z) == META_conveyorBelt || world.getBlockMetadata(x, y, z) == META_conveyorDropper )
			return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+.05, z + 1);
		this.setBlockBoundsBasedOnState(world,x,y,z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world,x,y,z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}


	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		switch(meta)
		{
		case META_conveyorBelt:
			return new TileEntityConveyorBelt();
		case META_conveyorDropper:
			return new TileEntityConveyorBelt(true);
		case META_sorter:
			return new TileEntityConveyorSorter();

		}
		return null;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
	{
		super.breakBlock(world, x, y, z, par5, par6);
	}


	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity par5Entity)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(par5Entity!=null && te instanceof TileEntityConveyorBelt && !par5Entity.isDead && !(par5Entity instanceof EntityPlayer && ((EntityPlayer)par5Entity).isSneaking()))
		{
			if(world.isBlockIndirectlyGettingPowered(x, y, z))
				return;
			TileEntityConveyorBelt tile = (TileEntityConveyorBelt) te;
			int f = tile.facing;
			ForgeDirection fd = ForgeDirection.getOrientation(f).getOpposite();
			double vBase = 1.15;
			double vX = 0.1 * vBase*fd.offsetX;
			double vY = par5Entity.motionY;
			double vZ = 0.1 * vBase*fd.offsetZ;

			if (tile.transportUp)
				vY = 0.17D * vBase;
			else if (tile.transportDown)
				vY = -0.07000000000000001D * vBase;

			if (tile.transportUp||tile.transportDown)
				par5Entity.onGround = false;

			//			if(par5Entity instanceof EntityItem)
			if (fd == ForgeDirection.WEST || fd == ForgeDirection.EAST)
			{
				if (par5Entity.posZ > z + 0.65D)
					vZ = -0.1D * vBase;
				else if (par5Entity.posZ < z + 0.35D)
					vZ = 0.1D * vBase;
				//				else
				//				{
				//					vZ = 0;
				//					par5Entity.posZ=z+.5;
				//				}
			}
			else if (fd == ForgeDirection.NORTH || fd == ForgeDirection.SOUTH)
			{
				if (par5Entity.posX > x + 0.65D)
					vX = -0.1D * vBase;
				else if (par5Entity.posX < x + 0.35D)
					vX = 0.1D * vBase;
				//				else
				//				{
				//					vX = 0;
				//					par5Entity.posX=x+.5;
				//				}
			}

			par5Entity.motionX = vX;
			par5Entity.motionY = vY;
			par5Entity.motionZ = vZ;
			if(par5Entity instanceof EntityItem)
			{
				boolean contact;
				boolean dropping = ((TileEntityConveyorBelt) te).dropping;
				if(dropping)
				{
					te = world.getTileEntity(x, y-1, z);
					contact = (f==2)&&(par5Entity.posZ-z>=.2) || (f==3)&&(par5Entity.posZ-z<=.8) || (f==4)&&(par5Entity.posX-x>=.2) || (f==5)&&(par5Entity.posX-x<=.8);
					fd = ForgeDirection.DOWN;
				}
				else
				{
					te = world.getTileEntity(x+fd.offsetX,y+(tile.transportUp?1: tile.transportDown?-1: 0),z+fd.offsetZ);
					contact = f==3? (par5Entity.posZ-z<=.2): f==2? (par5Entity.posZ-z>=.8): f==5? (par5Entity.posX-x<=.2): (par5Entity.posX-x>=.8);
				}
				if (!contact)
					((EntityItem)par5Entity).age=0;
				if (!world.isRemote)
					if(contact && te instanceof IInventory)
					{
						IInventory inv = (IInventory)te;
						if(!(inv instanceof TileEntityConveyorBelt))
						{
							ItemStack stack = ((EntityItem)par5Entity).getEntityItem();
							if(stack!=null)
							{
								ItemStack ret = Utils.insertStackIntoInventory(inv, stack.copy(), fd.getOpposite().ordinal());
								if(ret==null)
									par5Entity.setDead();
								else if(ret.stackSize<stack.stackSize)
									((EntityItem)par5Entity).setEntityItemStack(ret);
							}
						}
					}

				if(dropping && contact && !(te instanceof IInventory) && world.isAirBlock(x, y-1, z) && !world.isRemote)
				{
					par5Entity.motionX = 0;
					par5Entity.motionZ = 0;
					par5Entity.setPosition(x+.5, y-.5, z+.5);
				}
			}
		}
	}



}