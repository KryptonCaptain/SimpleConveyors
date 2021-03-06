package blusunrize.conveyors.common.items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;
import blusunrize.conveyors.ImmersiveEngineering;

import blusunrize.conveyors.api.tool.ITool;


import blusunrize.conveyors.common.util.ItemNBTHelper;
import blusunrize.conveyors.common.util.Lib;
import blusunrize.conveyors.common.util.Utils;


import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.common.Optional;

@Optional.Interface(iface = "cofh.api.item.IToolHammer", modid = "CoFHAPI|item")
public class ItemIETool extends ItemIEBase implements cofh.api.item.IToolHammer, ITool
{
	static int hammerMaxDamage;
	public ItemIETool()
	{
		super("tool", 1, "hammer");

	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv)
	{
		if(adv && stack.getItemDamage()==0)
		{
			int nbtDamage = ItemNBTHelper.getInt(stack, "hammerDmg");
			list.add("Durability: " + (hammerMaxDamage-nbtDamage)+" / "+hammerMaxDamage);
		}
		if(ItemNBTHelper.hasKey(stack, "linkingPos"))
		{
			int[] link = ItemNBTHelper.getIntArray(stack, "linkingPos");
			if(link!=null&&link.length>3)
				list.add(StatCollector.translateToLocalFormatted(Lib.DESC_INFO+"attachedToDim", link[1],link[2],link[3],link[0]));
		}
	}

	@Override
	public boolean hasContainerItem(ItemStack stack)
	{
		return stack.getItemDamage()==0;
	}
	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		if(stack.getItemDamage()==0)
		{
			int nbtDamage = ItemNBTHelper.getInt(stack, "hammerDmg")+1;
			if(nbtDamage<hammerMaxDamage)
			{
				ItemStack container = stack.copy();
				ItemNBTHelper.setInt(container, "hammerDmg", nbtDamage);
				return container;
			}
		}
		return null;
	}
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
	{
		return stack.getItemDamage()!=0;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			//			int chunkX = (x>>4);
			//			int chunkZ = (z>>4);
			//			MineralWorldInfo info = ExcavatorHandler.getMineralWorldInfo(world, chunkX, chunkZ);
			//			player.addChatMessage(new ChatComponentText("info for chunk; mineral: "+(info.mineral!=null?EnumChatFormatting.GREEN+info.mineral.name+EnumChatFormatting.RESET:"none")+"  override: "+(info.mineralOverride!=null?EnumChatFormatting.GREEN+info.mineralOverride.name+EnumChatFormatting.RESET:"none")+"  depletion: "+info.depletion));
			if(stack.getItemDamage()==0)
			{


				if (world.getBlock(x, y, z)==Blocks.piston)
				{
					int meta = world.getBlockMetadata(x, y, z);
					if (!BlockPistonBase.isExtended(meta))
					{
						int dir = BlockPistonBase.getPistonOrientation(meta);
						dir = (dir+1)%6;
						world.setBlockMetadataWithNotify(x, y, z, meta-(meta&7)+dir, 3);
					}

				}
				return false;
			}

			
			//						x += 6;
			//						y += 1;
			//			
			//						world.createExplosion(player, x+.5, y+.5, z+.5, 1.5f, true);
			//			
			//						float vex = 16;
			//			
			//						if(world instanceof WorldServer)
			//							for(int i=0; i<vex; i++)
			//							{
			//								float angle = i*(360/vex);
			//								float h = 0;
			//								for(int j=0; j<16; j++)
			//								{
			//									float r = 1f-Math.min(j,5)*.0625f;
			//									double xx = r*Math.cos(angle);
			//									double zz = r*Math.sin(angle);
			//									((WorldServer)world).func_147487_a("explode", x+xx, y+h,z+zz, 0, 0,0,0, 1);
			//									((WorldServer)world).func_147487_a("largesmoke", x+xx,y+h,z+zz, 0, 0,.0,0, 1);
			//									((WorldServer)world).func_147487_a("largesmoke", x+xx,y+h,z+zz, 0, 0,.0,0, 1);
			//									//					world.spawnParticle("explode", x+xx, y+h,z+zz, 0,0,0);
			//									//					world.spawnParticle("largesmoke", x+xx,y+h,z+zz, 0,.0,0);
			//									//					world.spawnParticle("largesmoke", x+xx,y+h,z+zz, 0,.0,0);
			//									if(i%2==0)
			//										//						world.spawnParticle("angryVillager", x+xx, y+h,z+zz, 0,0,0);
			//										((WorldServer)world).func_147487_a("angryVillager", x+xx, y+h,z+zz, 0, 0,0,0, 1);
			//									h += .1875f;
			//								}
			//								for(int j=0; j<16; j++)
			//								{
			//									float r = (float)(Math.cos(112.5f-j*(45/16f)));
			//									double xx = r*Math.cos(angle);
			//									double zz = r*Math.sin(angle);
			//									//					world.spawnParticle("explode", x+xx, y+h, z+zz, 0,.0,0);
			//									//					world.spawnParticle("largesmoke", x+xx, y+h, z+zz, xx*.025,.0,zz*.025);
			//									//					world.spawnParticle("largesmoke", x+xx, y+h, z+zz, xx*.05,.0,zz*.05);
			//									//					world.spawnParticle("largesmoke", x+xx, y+h, z+zz, xx*.1,.0,zz*.1);
			//									((WorldServer)world).func_147487_a("explode", x+xx, y+h, z+zz, 0, 0,.0,0, 1);
			//									((WorldServer)world).func_147487_a("largesmoke", x+xx, y+h, z+zz, 0, xx*.025,.0,zz*.025, 1);
			//									((WorldServer)world).func_147487_a("largesmoke", x+xx, y+h, z+zz, 0, xx*.05,.0,zz*.05, 1);
			//									((WorldServer)world).func_147487_a("largesmoke", x+xx, y+h, z+zz, 0, xx*.1,.0,zz*.1, 1);
			//									h += .0625f;
			//								}
			//							}
		}
		else
		{

		}
		return false;
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
	{
		if(player.getCurrentEquippedItem()!=null && this.equals(player.getCurrentEquippedItem().getItem()))
			return player.getCurrentEquippedItem().getItemDamage()==0;
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{

		return stack;
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass)
	{
		if(getToolClasses(stack).contains(toolClass))
			return 2;
		else
			return -1;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.getItemDamage()==0&&ItemNBTHelper.getInt(stack, "hammerDmg")>0;
	}
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return ItemNBTHelper.getInt(stack, "hammerDmg") / (double)hammerMaxDamage;
	}
	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return hammerMaxDamage;
	}
	@Override
	public boolean isDamaged(ItemStack stack)
	{
		return false;
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack)
	{
		int meta = stack.getItemDamage();
		return meta==0?ImmutableSet.of(Lib.TOOL_HAMMER): meta==1?ImmutableSet.of(Lib.TOOL_WIRECUTTER): new HashSet<String>();
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta)
	{
		if(ForgeHooks.isToolEffective(stack, block, meta))
			return 6;
		return super.getDigSpeed(stack, block, meta);
	}

	@Override
	@Optional.Method(modid = "CoFHAPI|item")
	public boolean isUsable(ItemStack stack, EntityLivingBase living, int x, int y, int z)
	{
		return stack!=null&&stack.getItemDamage()==0;
	}

	@Override
	@Optional.Method(modid = "CoFHAPI|item")
	public void toolUsed(ItemStack stack, EntityLivingBase living, int x, int y, int z)
	{
	}

	@Override
	public boolean isTool(ItemStack item) {
		return item.getItemDamage()!=3;
	}
}