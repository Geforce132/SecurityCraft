package net.geforcemods.securitycraft.handlers;

import java.util.Random;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiUtils;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class ForgeEventHandler {

	/**
	 * Called whenever a {@link EntityPlayer} joins the game.
	 */
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.createIrcBot(event.player.getCommandSenderName());
		ChatComponentText chatcomponenttext = new ChatComponentText(StatCollector.translateToLocal("messages.thanks") + " " + mod_SecurityCraft.getVersion() + "! " + StatCollector.translateToLocal("messages.tip") + " " + getRandomTip());

		if(mod_SecurityCraft.configHandler.sayThanksMessage){
			event.player.addChatComponentMessage(chatcomponenttext);	
		}
	}

	/**
	 * Called whenever a {@link EntityPlayer} leaves the game.
	 */
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event){
		if(mod_SecurityCraft.configHandler.disconnectOnWorldClose && mod_SecurityCraft.instance.getIrcBot(event.player.getCommandSenderName()) != null){
			mod_SecurityCraft.instance.getIrcBot(event.player.getCommandSenderName()).disconnect();
			mod_SecurityCraft.instance.removeIrcBot(event.player.getCommandSenderName());
		}	
	}

	@SubscribeEvent
	public void onDamageTaken(LivingHurtEvent event)
	{
		if(event.entityLiving != null && PlayerUtils.isPlayerMountedOnCamera(event.entityLiving)){
			event.setCanceled(true);
			return;
		}

		if(event.source == CustomDamageSources.electricity)
			mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(event.entity.posX, event.entity.posY, event.entity.posZ, SCSounds.ELECTRIFIED.path, 0.25F));
	}

	@SubscribeEvent
	public void onBucketUsed(FillBucketEvent event){
		ItemStack result = fillBucket(event.world, event.target);
		if(result == null){ return; }
		event.result = result;
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void onWorldUnloaded(Unload event){
		if(event.world.isRemote){
			((ClientProxy) mod_SecurityCraft.serverProxy).worldViews.clear();
		}
	}

	@SubscribeEvent 
	public void onPlayerInteracted(PlayerInteractEvent event){
		if(!event.entityPlayer.worldObj.isRemote){	
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z) != null && isCustomizableBlock(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z), event.world.getTileEntity(event.x, event.y, event.z)) && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockModifier)){
				event.setCanceled(true);

				if(!BlockUtils.isOwnerOfBlock(((CustomizableSCTE) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)), event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockModifier.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerName()), EnumChatFormatting.RED);
					return;
				}

				event.entityPlayer.openGui(mod_SecurityCraft.instance, 100, event.entityPlayer.worldObj, event.x, event.y, event.z);	
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == mod_SecurityCraft.portableRadar && PlayerUtils.isHoldingItem(event.entityPlayer, Items.name_tag) && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);

				event.entityPlayer.getCurrentEquippedItem().stackSize--;

				((TileEntityPortableRadar) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z) != null && isOwnableBlock(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z), event.world.getTileEntity(event.x, event.y, event.z)) && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockRemover)){
				event.setCanceled(true);

				if(!BlockUtils.isOwnerOfBlock((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z), event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockRemover.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerName()), EnumChatFormatting.RED);
					return;
				}

				if(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == mod_SecurityCraft.LaserBlock){
					event.entityPlayer.worldObj.func_147480_a(event.x, event.y, event.z, true);
					BlockLaserBlock.destroyAdjacentLasers(event.world, event.x, event.y, event.z);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}else{
					event.entityPlayer.worldObj.func_147480_a(event.x, event.y, event.z, true);
					event.entityPlayer.worldObj.removeTileEntity(event.x, event.y, event.z);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.modID.equals("securitycraft")){
			mod_SecurityCraft.configFile.save();

			mod_SecurityCraft.configHandler.setupConfiguration();
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event){
		if(!event.world.isRemote){
			if(event.world.getTileEntity(event.x, event.y, event.z) != null && event.world.getTileEntity(event.x, event.y, event.z) instanceof CustomizableSCTE){
				for(int i = 0; i < ((CustomizableSCTE) event.world.getTileEntity(event.x, event.y, event.z)).getNumberOfCustomizableOptions(); i++){
					if(((CustomizableSCTE) event.world.getTileEntity(event.x, event.y, event.z)).itemStacks[i] != null){
						ItemStack stack = ((CustomizableSCTE) event.world.getTileEntity(event.x, event.y, event.z)).itemStacks[i];
						EntityItem item = new EntityItem(event.world, (double) event.x, (double) event.y, (double) event.z, stack);
						event.world.spawnEntityInWorld(item);

						((CustomizableSCTE) event.world.getTileEntity(event.x, event.y, event.z)).onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRendered(RenderPlayerEvent.Pre event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.entityPlayer)){
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderGameOverlay(RenderGameOverlayEvent.Post event){
		if(Minecraft.getMinecraft().thePlayer != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer)){
			if(event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE && Minecraft.getMinecraft().theWorld.getBlock((int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)(Minecraft.getMinecraft().thePlayer.ridingEntity.posY - 1.0D), (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ)) instanceof BlockSecurityCamera){
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.resolution, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)(Minecraft.getMinecraft().thePlayer.ridingEntity.posY - 1.0D), (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ), event.mouseX, event.mouseY);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.entity)){
			event.newfov = ((EntitySecurityCamera) event.entity.ridingEntity).getZoomAmount();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer)){
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(MouseEvent event) {
		if(Minecraft.getMinecraft().theWorld != null)
		{
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer))
			{
				event.setCanceled(true);
			}
		}
	}

	private String getRandomTip(){
		String[] tips = {
				StatCollector.translateToLocal("messages.tip.scHelp"),
				StatCollector.translateToLocal("messages.tip.scConnect"),
				StatCollector.translateToLocal("messages.tip.trello"),
				StatCollector.translateToLocal("messages.tip.patreon")
		};

		return tips[new Random().nextInt(tips.length)];
	}

	private ItemStack fillBucket(World world, MovingObjectPosition position){
		Block block = world.getBlock(position.blockX, position.blockY, position.blockZ);

		if(block == mod_SecurityCraft.bogusWater){
			world.setBlockToAir(position.blockX, position.blockY, position.blockZ);
			return new ItemStack(mod_SecurityCraft.fWaterBucket, 1);
		}else if(block == mod_SecurityCraft.bogusLava){
			world.setBlockToAir(position.blockX, position.blockY, position.blockZ);
			return new ItemStack(mod_SecurityCraft.fLavaBucket, 1);
		}else{
			return null;
		}
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity){
		if(tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable){
			return true;
		}else{
			return false;
		}
	}

	private boolean isCustomizableBlock(Block block, TileEntity tileEntity){
		if(tileEntity instanceof CustomizableSCTE){
			return true;
		}else{
			return false;
		}
	}
	
}
