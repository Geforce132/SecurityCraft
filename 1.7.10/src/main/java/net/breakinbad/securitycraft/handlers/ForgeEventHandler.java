package net.breakinbad.securitycraft.handlers;

import java.util.Random;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.api.IOwnable;
import net.breakinbad.securitycraft.blocks.BlockLaserBlock;
import net.breakinbad.securitycraft.blocks.BlockOwnable;
import net.breakinbad.securitycraft.items.ItemModule;
import net.breakinbad.securitycraft.main.Utils.BlockUtils;
import net.breakinbad.securitycraft.main.Utils.PlayerUtils;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.CustomDamageSources;
import net.breakinbad.securitycraft.misc.SCSounds;
import net.breakinbad.securitycraft.network.ClientProxy;
import net.breakinbad.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.breakinbad.securitycraft.network.packets.PacketCheckRetinalScanner;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.breakinbad.securitycraft.tileentity.TileEntityPortableRadar;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class ForgeEventHandler {
	
	private int counter = 0;
	private int cooldownCounter = 0;
	
	/**
	 * Called whenever a {@link EntityPlayer} joins the game.
	 */
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.createIrcBot(event.player.getCommandSenderName());
		ChatComponentText chatcomponenttext = new ChatComponentText("Thanks for using SecurityCraft " + mod_SecurityCraft.getVersion() + "! Tip: " + getRandomTip());
    	
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
	
	private String getRandomTip(){
    	Random random = new Random();
    	int randomInt = random.nextInt(4);
    	
    	if(randomInt == 0){
    		return "Join BreakIn' Bad, the official SecurityCraft server! IP: breakinbad.net";
    	}else if(randomInt == 1){
    		return "Typing /sc help will give you a SecurityCraft manual, which will display help info for SecurityCraft blocks/items.";
    	}else if(randomInt == 2){
    		return "Use /sc connect to get personal support from the mod devs!";
    	}else if(randomInt == 3){
    		return "Check out the Trello board for SecurityCraft, and report bugs or give us suggestions! https://trello.com/b/dbCNZwx0/securitycraft";
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
	}

	@SubscribeEvent
	public void onDamageTaken(LivingHurtEvent event)
	{
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
			((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.clear();
		}
	}
	
	@SubscribeEvent 
	public void onPlayerInteracted(PlayerInteractEvent event){
		if(!event.entityPlayer.worldObj.isRemote){	
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z) != null && isCustomizableBlock(event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z), event.world.getTileEntity(event.x, event.y, event.z)) && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockModifier)){
				event.setCanceled(true);
				
				if(!BlockUtils.isOwnerOfBlock(((CustomizableSCTE) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)), event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, "I'm sorry, you can not customize this block. This block is owned by " + ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z)).getOwnerName() + ".", EnumChatFormatting.RED);
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
