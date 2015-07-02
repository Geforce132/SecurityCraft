package org.freeforums.geforce.securitycraft.handlers;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;

import org.freeforums.geforce.securitycraft.blocks.BlockLaserBlock;
import org.freeforums.geforce.securitycraft.blocks.BlockOwnable;
import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.entity.EntitySecurityCamera;
import org.freeforums.geforce.securitycraft.gui.GuiUtils;
import org.freeforums.geforce.securitycraft.interfaces.IOwnable;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.ClientProxy;
import org.freeforums.geforce.securitycraft.network.packets.PacketCheckRetinalScanner;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ForgeEventHandler {
	
	private int counter = 0;
	private int cooldownCounter = 0;
	
	/**
	 * Called whenever a {@link EntityPlayer} joins the game.
	 */
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.createIrcBot(event.player.getCommandSenderName());
		ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("Thanks for using SecurityCraft " + mod_SecurityCraft.getVersion() + "! Tip: " + getRandomTip(), new Object[0]);
    	
		if(mod_SecurityCraft.configHandler.sayThanksMessage){
			event.player.addChatComponentMessage(chatcomponenttranslation);	
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
	
	private String getRandomTip(){
    	Random random = new Random();
    	int randomInt = random.nextInt(3);
    	
    	if(randomInt == 0){
    		return "Join breakinbad.net, the official SecurityCraft server!";
    	}else if(randomInt == 1){
    		return "/sc recipe will display the recipe for SecurityCraft blocks/items.";
    	}else if(randomInt == 2){
    		return "/sc help will display help info for SecurityCraft blocks/items.";
    	}else{
    		return "";
    	}
    }
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event){
		if(event.player.worldObj.isRemote){
			counter++;
			if(cooldownCounter > 0){
				cooldownCounter--;
			}
			if(counter >= 20){
				mod_SecurityCraft.network.sendToServer(new PacketCheckRetinalScanner(event.player.getCommandSenderName()));
				counter = 0;
			}
		}
		
		//TODO Remove after we implement the new version of the cameras that use LookingGlass.
		if((event.player.ridingEntity == null || !(event.player.ridingEntity instanceof EntitySecurityCamera)) && mod_SecurityCraft.instance.hasUsePosition(event.player.getCommandSenderName())){
			PlayerUtils.setPlayerPosition(event.player, (Double) mod_SecurityCraft.instance.getUsePosition(event.player.getCommandSenderName())[0], (Double) mod_SecurityCraft.instance.getUsePosition(event.player.getCommandSenderName())[1], (Double) mod_SecurityCraft.instance.getUsePosition(event.player.getCommandSenderName())[2], (Float) mod_SecurityCraft.instance.getUsePosition(event.player.getCommandSenderName())[3], (Float) mod_SecurityCraft.instance.getUsePosition(event.player.getCommandSenderName())[4]);
			mod_SecurityCraft.instance.removeUsePosition(event.player.getCommandSenderName());
		}
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
			((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.clear();
			System.out.println(((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.size());
		}
	}
	
	@SubscribeEvent 
	public void onPlayerInteracted(PlayerInteractEvent event){
		if(!event.entityPlayer.worldObj.isRemote){	
			if(event.action == Action.RIGHT_CLICK_BLOCK &&  event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z) != null && isCustomizableBlock(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z), event.world.getTileEntity(event.x, event.y, event.z)) && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockModifier){
				event.setCanceled(true);
				
				if(((CustomizableSCTE) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerUUID() != null && !((CustomizableSCTE) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerUUID().matches(event.entityPlayer.getGameProfile().getId().toString())){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, "I'm sorry, you can not customize this block. This block is owned by " + ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerName() + ".", EnumChatFormatting.RED);
					return;
				}
				
				event.entityPlayer.openGui(mod_SecurityCraft.instance, 100, event.entityPlayer.worldObj, event.x, event.y, event.z);	
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == mod_SecurityCraft.portableRadar && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.name_tag && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);
				
				event.entityPlayer.getCurrentEquippedItem().stackSize--;
				
				((TileEntityPortableRadar) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z) != null && isOwnableBlock(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z), event.world.getTileEntity(event.x, event.y, event.z)) && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover){
				event.setCanceled(true);
				
				if(((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerUUID() != null && !((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerUUID().matches(event.entityPlayer.getGameProfile().getId().toString())){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, "I'm sorry, you can not remove this block. This block is owned by " + ((IOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerName() + ".", EnumChatFormatting.RED);
					return;
				}

				if(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == mod_SecurityCraft.LaserBlock){
					event.entityPlayer.worldObj.func_147480_a(event.x, event.y, event.z, true);
					BlockLaserBlock.destroyAdjecentLasers(event.world, event.x, event.y, event.z);
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
	
	@SubscribeEvent //TODO Remove after we implement the new version of the cameras that use LookingGlass.
	public void onEntityConstructing(EntityConstructing event){
		if(event.entity instanceof EntityPlayer){
			event.entity.registerExtendedProperties("SecurityCraftHandler", new PlayerExtendedPropertyHandler());
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT) //TODO Remove after we implement the new version of the cameras that use LookingGlass.
	public void onPlayerRendered(RenderPlayerEvent.Pre event){
		if(event.entityPlayer.ridingEntity != null && event.entityPlayer.ridingEntity instanceof EntitySecurityCamera){
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT) //TODO Remove after we implement the new version of the cameras that use LookingGlass.
	public void renderGameOverlay(RenderGameOverlayEvent.Post event){
		if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.ridingEntity != null && Minecraft.getMinecraft().thePlayer.ridingEntity instanceof EntitySecurityCamera){
			//Minecraft.getMinecraft().getTextureManager().bindTexture(CameraTextures.cameraDashboard);            
            //event.gui.drawTexturedModalRect(5, 0, 0, 0, 0, 0);
			if(event.type == ElementType.EXPERIENCE && Minecraft.getMinecraft().theWorld.getBlock((int) Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int) (Minecraft.getMinecraft().thePlayer.ridingEntity.posY - 1D), (int) Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ)) instanceof BlockSecurityCamera){
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.resolution, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, (int) Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int) (Minecraft.getMinecraft().thePlayer.ridingEntity.posY - 1D), (int) Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ), event.mouseX, event.mouseY);
				//GuiUtils.drawTooltip(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(((EntitySecurityCamera) Minecraft.getMinecraft().thePlayer.ridingEntity).getCameraInfo(), 200), 0, 20, Minecraft.getMinecraft().fontRenderer);
			}
		}
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
    
    public void setCooldown(int par1){
    	this.cooldownCounter = par1;
    }
    
    public int getCooldown(){
    	return this.cooldownCounter;
    }

}
