package blusunrize.immersiveengineering.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.AdvancedAABB;
import blusunrize.immersiveengineering.client.fx.ParticleRenderer;
import blusunrize.immersiveengineering.client.models.ModelIEObj;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockOverlayText;
import blusunrize.immersiveengineering.common.util.IELogger;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Lib;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.network.MessageRequestBlockUpdate;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.oredict.OreDictionary;

public class ClientEventHandler
{
	public static IIcon iconItemBlank;
	public static int itemSheetWidth;
	public static int itemSheetHeight;

	
	@SubscribeEvent()
	public void textureStich(TextureStitchEvent.Post event)
	{
		if(event.map.getTextureType()==0)
			for(ModelIEObj modelIE : ModelIEObj.existingStaticRenders)
			{
				WavefrontObject model = modelIE.rebindModel();
				rebindUVsToIcon(model, modelIE);
			}
		if(event.map.getTextureType()==1)
		{
			itemSheetWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			itemSheetHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		}
		//
		//		if(event.map.getTextureType()==Config.getInt("revolverSheetID"))
		//		{
		//			try {
		//				IELogger.debug("TEST-udafe");
		//				TextureAtlasSprite tal = (TextureAtlasSprite)event.map.registerIcon("immersiveengineering:revolver");
		//				URL url = new URL("http://i.imgur.com/bU3bEDe.png");
		//				BufferedImage img = ImageIO.read(url);
		//				IELogger.debug("url = "+url);
		//				IELogger.debug("img = "+img);
		//				IELogger.debug("Loading sprite");
		//				tal.loadSprite(new BufferedImage[]{img}, null, false);
		//				IELogger.debug("sprite loaded");
		//				((ItemRevolver)IEContent.itemRevolver).revolverDefaultTexture=tal;
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
	}

	void rebindUVsToIcon(WavefrontObject model, ModelIEObj modelIE)
	{
		for(GroupObject groupObject : model.groupObjects)
		{
			IIcon icon = modelIE.getBlockIcon(groupObject.name);
			if(icon==null)
				continue;
			float minU = icon.getInterpolatedU(0);
			float sizeU = icon.getInterpolatedU(16) - minU;
			float minV = icon.getInterpolatedV(0);
			float sizeV = icon.getInterpolatedV(16) - minV;
			float baseOffsetU = (16f/icon.getIconWidth())*.0005F;
			float baseOffsetV = (16f/icon.getIconHeight())*.0005F;
			for(Face face : groupObject.faces)
			{
				float averageU = 0F;
				float averageV = 0F;
				if(face.textureCoordinates!=null && face.textureCoordinates.length>0)
				{
					for(int i=0; i<face.textureCoordinates.length; ++i)
					{
						averageU += face.textureCoordinates[i].u;
						averageV += face.textureCoordinates[i].v;
					}
					averageU = averageU / face.textureCoordinates.length;
					averageV = averageV / face.textureCoordinates.length;
				}

				for (int i=0; i<face.vertices.length; ++i)
				{
					float offsetU, offsetV;
					TextureCoordinate textureCoordinate = face.textureCoordinates[i];
					offsetU = baseOffsetU;
					offsetV = baseOffsetV;
					if (face.textureCoordinates[i].u > averageU)
						offsetU = -offsetU;
					if (face.textureCoordinates[i].v > averageV)
						offsetV = -offsetV;

					face.textureCoordinates[i] = new TextureCoordinate(
							minU + sizeU * (textureCoordinate.u+offsetU),
							minV + sizeV * (textureCoordinate.v+offsetV)
							);
				}
			}
		}
	}

	

	@SubscribeEvent()
	public void lastWorldRender(RenderWorldLastEvent event)
	{
		ParticleRenderer.dispatch();
	}

	
	@SubscribeEvent()
	public void renderAdditionalBlockBounds(DrawBlockHighlightEvent event)
	{
		if(event.subID==0 && event.target.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK)
		{	
			float f1 = 0.002F;
			double d0 = event.player.lastTickPosX + (event.player.posX - event.player.lastTickPosX) * (double)event.partialTicks;
			double d1 = event.player.lastTickPosY + (event.player.posY - event.player.lastTickPosY) * (double)event.partialTicks;
			double d2 = event.player.lastTickPosZ + (event.player.posZ - event.player.lastTickPosZ) * (double)event.partialTicks;
			if(event.player.worldObj.getBlock(event.target.blockX,event.target.blockY,event.target.blockZ) instanceof IEBlockInterfaces.ICustomBoundingboxes)
			{
				ChunkCoordinates cc = new ChunkCoordinates(event.target.blockX,event.target.blockY,event.target.blockZ);
				IEBlockInterfaces.ICustomBoundingboxes block = (IEBlockInterfaces.ICustomBoundingboxes) event.player.worldObj.getBlock(event.target.blockX,event.target.blockY,event.target.blockZ);
				ArrayList<AxisAlignedBB> set = block.addCustomSelectionBoxesToList(event.player.worldObj, cc.posX,cc.posY,cc.posZ);
				if(!set.isEmpty())
				{
					GL11.glEnable(GL11.GL_BLEND);
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
					GL11.glLineWidth(2.0F);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(false);
					ArrayList<AxisAlignedBB> specialBoxes = new ArrayList<AxisAlignedBB>();
					AxisAlignedBB overrideBox = null; 
					for(AxisAlignedBB aabb : set)
						if(aabb!=null)
						{
							boolean b = block.addSpecifiedSubBox(event.player.worldObj, cc.posX,cc.posY,cc.posZ, event.player, aabb, event.target.hitVec, specialBoxes);
							if(b)
								overrideBox = specialBoxes.get(specialBoxes.size()-1);
						}

					if(overrideBox!=null)
						renderBoundingBox(overrideBox, cc.posX-d0,cc.posY-d1,cc.posZ-d2, f1);
					else
						for(AxisAlignedBB aabb : specialBoxes.isEmpty()?set:specialBoxes)
							if(aabb!=null)
								renderBoundingBox(aabb, cc.posX-d0,cc.posY-d1,cc.posZ-d2, f1);

					GL11.glDepthMask(true);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_BLEND);
					event.setCanceled(true);
				}
			}


		}
	}

	static void renderBoundingBox(AxisAlignedBB aabb, double offsetX, double offsetY, double offsetZ, float expand)
	{
		if(aabb instanceof AdvancedAABB && ((AdvancedAABB)aabb).drawOverride!=null && ((AdvancedAABB)aabb).drawOverride.length>0)
		{
			double midX = aabb.minX+(aabb.maxX-aabb.minX)/2;
			double midY = aabb.minY+(aabb.maxY-aabb.minY)/2;
			double midZ = aabb.minZ+(aabb.maxZ-aabb.minZ)/2;
			ClientUtils.tes().addTranslation((float)offsetX, (float)offsetY, (float)offsetZ);
			for(Vec3[] face : ((AdvancedAABB)aabb).drawOverride)
			{
				ClientUtils.tes().startDrawing(GL11.GL_LINE_LOOP);
				for(Vec3 v : face)
					ClientUtils.tes().addVertex(v.xCoord+(v.xCoord<midX?-expand:expand),v.yCoord+(v.yCoord<midY?-expand:expand),v.zCoord+(v.zCoord<midZ?-expand:expand));
				ClientUtils.tes().draw();
			}
			ClientUtils.tes().addTranslation((float)-offsetX, (float)-offsetY, (float)-offsetZ);
		}
		else
			RenderGlobal.drawOutlinedBoundingBox(aabb.getOffsetBoundingBox(offsetX, offsetY, offsetZ).expand((double)expand, (double)expand, (double)expand), -1);
	}

	@SubscribeEvent()
	public void onClientDeath(LivingDeathEvent event)
	{
	}
	@SubscribeEvent()
	public void onRenderLivingPre(RenderLivingEvent.Pre event)
	{
		if(event.entity.getEntityData().hasKey("headshot"))
		{
			ModelBase model = event.renderer.mainModel;
			if(model instanceof ModelBiped)
				((ModelBiped)model).bipedHead.showModel=false;
			else if(model instanceof ModelVillager)
				((ModelVillager)model).villagerHead.showModel=false;
		}
		//		if(OreDictionary.itemMatches(new ItemStack(IEContent.itemRailgun),event.entity.getEquipmentInSlot(0),true))
		//		{
		//			ModelBase model = event.renderer.mainModel;
		//			if(model instanceof ModelBiped)
		//				((ModelBiped)model).bipedLeftArm.rotateAngleX=.9f;
		//		}
	}
	@SubscribeEvent()
	public void onRenderLivingPost(RenderLivingEvent.Post event)
	{
		if(event.entity.getEntityData().hasKey("headshot"))
		{
			ModelBase model = event.renderer.mainModel;
			if(model instanceof ModelBiped)
				((ModelBiped)model).bipedHead.showModel=true;
			else if(model instanceof ModelVillager)
				((ModelVillager)model).villagerHead.showModel=true;
		}
	}
}
