package net.geforcemods.securitycraft.handlers;

import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeEventHandler {
		
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.createIrcBot(event.player.getName());
		ChatComponentText chatcomponenttext = new ChatComponentText(StatCollector.translateToLocal("messages.thanks") + " " + mod_SecurityCraft.getVersion() + "! " + StatCollector.translateToLocal("messages.tip") + ": " + getRandomTip());
    	
		if(mod_SecurityCraft.configHandler.sayThanksMessage){
			event.player.addChatComponentMessage(chatcomponenttext);	
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
		ItemStack result = fillBucket(event.world, event.target.getBlockPos());
		if(result == null){ return; }
		event.result = result;
		event.setResult(Result.ALLOW);	
	}
	
	@SubscribeEvent 
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event){
		if(mod_SecurityCraft.configHandler.disconnectOnWorldClose && mod_SecurityCraft.instance.getIrcBot(event.player.getName()) != null){
			mod_SecurityCraft.instance.getIrcBot(event.player.getName()).disconnect();
			mod_SecurityCraft.instance.removeIrcBot(event.player.getName());
		}		
	}
	
	@SubscribeEvent 
	public void onPlayerInteracted(PlayerInteractEvent event){
		if(event.entityPlayer.worldObj.isRemote){
			return;
		}else{
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getTileEntity(event.pos) != null && isCustomizableBlock(event.entityPlayer.worldObj.getBlockState(event.pos).getBlock(), event.entityPlayer.worldObj.getTileEntity(event.pos)) && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockModifier)){
				event.setCanceled(true);
				
				if(!BlockUtils.isOwnerOfBlock((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos), event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockModifier.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerName()), EnumChatFormatting.RED);
					return;
				}
				
				event.entityPlayer.openGui(mod_SecurityCraft.instance, 100, event.entityPlayer.worldObj, event.pos.getX(), event.pos.getY(), event.pos.getZ());	
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlockState(event.pos).getBlock() == mod_SecurityCraft.portableRadar && PlayerUtils.isHoldingItem(event.entityPlayer, Items.name_tag) && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);
				
				event.entityPlayer.getCurrentEquippedItem().stackSize--;
				
				((TileEntityPortableRadar) event.entityPlayer.worldObj.getTileEntity(event.pos)).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getTileEntity(event.pos) != null && isOwnableBlock(event.entityPlayer.worldObj.getBlockState(event.pos).getBlock(), event.entityPlayer.worldObj.getTileEntity(event.pos)) && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockRemover)){
				event.setCanceled(true);

				if(!BlockUtils.isOwnerOfBlock((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos), event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockRemover.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerName()), EnumChatFormatting.RED);
					return;
				}

				if(event.entityPlayer.worldObj.getBlockState(event.pos).getBlock() == mod_SecurityCraft.LaserBlock){
					event.entityPlayer.worldObj.destroyBlock(event.pos, true);
					BlockLaserBlock.destroyAdjecentLasers(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ());
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}else{
					event.entityPlayer.worldObj.destroyBlock(event.pos, true);
					event.entityPlayer.worldObj.removeTileEntity(event.pos);
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
			if(event.world.getTileEntity(event.pos) != null && event.world.getTileEntity(event.pos) instanceof CustomizableSCTE){
				for(int i = 0; i < ((CustomizableSCTE) event.world.getTileEntity(event.pos)).getNumberOfCustomizableOptions(); i++){
					if(((CustomizableSCTE) event.world.getTileEntity(event.pos)).itemStacks[i] != null){
						ItemStack stack = ((CustomizableSCTE) event.world.getTileEntity(event.pos)).itemStacks[i];
						EntityItem item = new EntityItem(event.world, (double) event.pos.getX(), (double) event.pos.getY(), (double) event.pos.getZ(), stack);
						event.world.spawnEntityInWorld(item);
						
						((CustomizableSCTE) event.world.getTileEntity(event.pos)).onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.entityPlayer)){
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(Minecraft.getMinecraft().thePlayer != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer)){
			if(event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE && ((BlockUtils.getBlock(Minecraft.getMinecraft().theWorld, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)(Minecraft.getMinecraft().thePlayer.ridingEntity.posY - 1.0D), (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ))) instanceof BlockSecurityCamera))){
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.resolution, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)(Minecraft.getMinecraft().thePlayer.ridingEntity.posY - 1.0D), (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ)));
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

	private ItemStack fillBucket(World world, BlockPos pos){
		Block block = world.getBlockState(pos).getBlock();
		
		if(block == mod_SecurityCraft.bogusWater){
			world.setBlockToAir(pos);
			return new ItemStack(mod_SecurityCraft.fWaterBucket, 1);
		}else if(block == mod_SecurityCraft.bogusLava){
			world.setBlockToAir(pos);
			return new ItemStack(mod_SecurityCraft.fLavaBucket, 1);
		}else{
			return null;
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
