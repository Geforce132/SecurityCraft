package org.freeforums.geforce.securitycraft.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.blocks.BlockLaserBlock;
import org.freeforums.geforce.securitycraft.blocks.BlockOwnable;
import org.freeforums.geforce.securitycraft.entity.EntitySecurityCamera;
import org.freeforums.geforce.securitycraft.interfaces.IOwnable;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCheckRetinalScanner;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;

@SuppressWarnings({"unused", "rawtypes"})
public class ForgeEventHandler {
	
	private int counter = 0;
	private int cooldownCounter = 0;
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.setIrcBot(event.player.getName());
		ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("Thanks for using SecurityCraft " + mod_SecurityCraft.getVersion() + "! Tip: " + getRandomTip(), new Object[0]);
    	
		if(mod_SecurityCraft.configHandler.sayThanksMessage){
			event.player.addChatComponentMessage(chatcomponenttranslation);	
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
	@SideOnly(Side.CLIENT)
	public void renderGameOverlay(RenderGameOverlayEvent event){
		if(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.cameraMonitor){
			Minecraft.getMinecraft().fontRendererObj.drawString("Camera X: " + Math.floor(Minecraft.getMinecraft().thePlayer.posX), 10, 10, 0xffffff);
			Minecraft.getMinecraft().fontRendererObj.drawString("Camera Y: " + Math.floor(Minecraft.getMinecraft().thePlayer.posY), 10, 20, 0xffffff);
			Minecraft.getMinecraft().fontRendererObj.drawString("Camera Z: " + Math.floor(Minecraft.getMinecraft().thePlayer.posZ), 10, 30, 0xffffff);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerTick(PlayerTickEvent event){
		counter++;
		if(cooldownCounter > 0){
			cooldownCounter--;
		}
		
		if(counter >= 20){
			mod_SecurityCraft.network.sendToServer(new PacketCheckRetinalScanner(event.player.getName()));
			counter = 0;
		}
		
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
			if(event.action == Action.RIGHT_CLICK_BLOCK && isCustomizableBlock(event.entityPlayer.worldObj.getBlockState(event.pos).getBlock()) && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockModifier){
				event.setCanceled(true);
				
				if(event.entityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockModifier && ((CustomizableSCTE) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID() != null && !((CustomizableSCTE) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID().matches(event.entityPlayer.getGameProfile().getId().toString())){
					HelpfulMethods.sendMessageToPlayer(event.entityPlayer, "I'm sorry, you can not customize this block. This block is owned by " + ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID() + ".", EnumChatFormatting.RED);
					return;
				}
				
				event.entityPlayer.openGui(mod_SecurityCraft.instance, 100, event.entityPlayer.worldObj, event.pos.getX(), event.pos.getY(), event.pos.getZ());	
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlockState(event.pos).getBlock() == mod_SecurityCraft.portableRadar && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.name_tag && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);
				
				event.entityPlayer.getCurrentEquippedItem().stackSize--;
				
				((TileEntityPortableRadar) event.entityPlayer.worldObj.getTileEntity(event.pos)).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && isOwnableBlock(event.entityPlayer.worldObj.getBlockState(event.pos).getBlock()) && event.entityPlayer.worldObj.getTileEntity(event.pos) != null && event.entityPlayer.worldObj.getTileEntity(event.pos) instanceof TileEntityOwnable && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover){
				event.setCanceled(true);

				if(((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID() != null && !((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID().matches(event.entityPlayer.getGameProfile().getId().toString())){
					HelpfulMethods.sendMessageToPlayer(event.entityPlayer, "I'm sorry, you can not remove this block. This block is owned by " + ((TileEntityOwnable) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerName() + ".", EnumChatFormatting.RED);
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
			}else if(event.action == Action.RIGHT_CLICK_BLOCK && isOwnableBlock(event.entityPlayer.worldObj.getBlockState(event.pos).getBlock()) && event.entityPlayer.worldObj.getTileEntity(event.pos) != null && event.entityPlayer.worldObj.getTileEntity(event.pos) instanceof TileEntityKeypadChest && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.universalBlockRemover){
				event.setCanceled(true);
				
				if(((TileEntityKeypadChest) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID() != null && !((TileEntityKeypadChest) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerUUID().matches(event.entityPlayer.getGameProfile().getId().toString())){
					HelpfulMethods.sendMessageToPlayer(event.entityPlayer, "I'm sorry, you can not remove this block. This block is owned by " + ((TileEntityKeypadChest) event.entityPlayer.worldObj.getTileEntity(event.pos)).getOwnerName() + ".", EnumChatFormatting.RED);
					return;
				}else{
					event.entityPlayer.worldObj.destroyBlock(event.pos, true);
					event.entityPlayer.worldObj.removeTileEntity(event.pos);
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
	public void onEntityMounted(EntityMountEvent event){
		if(!mod_SecurityCraft.debuggingMode){ return; }
		
		if(!event.worldObj.isRemote && event.isMounting() && event.entityBeingMounted != null && event.entityBeingMounted instanceof EntitySecurityCamera){
			System.out.println(event.entityMounting.getName() + " is mounting " + event.entityBeingMounted.getName());
			((EntityPlayer) event.entityMounting).setInvisible(true);
		}else if(!event.worldObj.isRemote && event.isDismounting() && event.entityBeingMounted != null && event.entityBeingMounted instanceof EntitySecurityCamera){
			System.out.println(event.entityMounting.getName() + " is dismounting. " + (event.entityBeingMounted != null ? event.entityBeingMounted.getName() : ""));
			((EntityPlayer) event.entityMounting).setInvisible(false);
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
	
	private int[] getBlockInFront(World par1World, EntityPlayer par2EntityPlayer, double reach){
    	int[] blockInfo = {0, 0, 0, 0, -1, 0};
    	
    	MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(par1World, par2EntityPlayer, true, reach);
    	
    	if(movingobjectposition != null){
    		if(movingobjectposition.typeOfHit == MovingObjectType.BLOCK){
    			blockInfo[1] = movingobjectposition.getBlockPos().getX();
    			blockInfo[2] = movingobjectposition.getBlockPos().getY();
    			blockInfo[3] = movingobjectposition.getBlockPos().getZ();
    			blockInfo[4] = movingobjectposition.sideHit.getIndex();
    			blockInfo[5] = par1World.getBlockState(movingobjectposition.getBlockPos()).getBlock().getMetaFromState(par1World.getBlockState(movingobjectposition.getBlockPos()));
    			blockInfo[0] = Block.getIdFromBlock(par1World.getBlockState(new BlockPos(blockInfo[1], blockInfo[2], blockInfo[3])).getBlock());

    		}
    	}
    	
    	return blockInfo;
    }
    
    private MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean flag, double reach){
    	float f = 1.0F;
    	float playerPitch = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
    	float playerYaw = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
    	double playerPosX = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * f;
    	double playerPosY = (par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * f + 1.6200000000000001D);
    	double playerPosZ = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * f;
    	Vec3 vecPlayer = new Vec3(playerPosX, playerPosY, playerPosZ);
    	float cosYaw = MathHelper.cos(-playerYaw * 0.01745329F - 3.141593F);
    	float sinYaw = MathHelper.sin(-playerYaw * 0.01745329F - 3.141593F);
    	float cosPitch = -MathHelper.cos(-playerPitch * 0.01745329F);
    	float sinPitch = -MathHelper.sin(-playerPitch * 0.01745329F);
    	float pointX = sinYaw * cosPitch;
    	float pointY = sinPitch;
    	float pointZ = cosYaw * cosPitch;
    	Vec3 vecPoint = vecPlayer.addVector(pointX * reach, pointY * reach, pointZ * reach);
    	MovingObjectPosition movingobjectposition = par1World.rayTraceBlocks(vecPlayer, vecPoint, flag);
    	return movingobjectposition;

    }
  
	private boolean isOwnableBlock(Block par1Block){
    	if(par1Block == mod_SecurityCraft.doorIndestructableIron || par1Block == mod_SecurityCraft.keypad || par1Block == mod_SecurityCraft.keycardReader || par1Block == mod_SecurityCraft.retinalScanner || par1Block == mod_SecurityCraft.reinforcedGlass || par1Block == mod_SecurityCraft.alarm || par1Block == mod_SecurityCraft.reinforcedStone || par1Block == mod_SecurityCraft.unbreakableIronBars || par1Block == mod_SecurityCraft.reinforcedFencegate || par1Block == mod_SecurityCraft.LaserBlock || par1Block == mod_SecurityCraft.keypadChest || par1Block == mod_SecurityCraft.reinforcedPlanks_Oak || par1Block == mod_SecurityCraft.reinforcedPlanks_Spruce || par1Block == mod_SecurityCraft.reinforcedPlanks_Birch || par1Block == mod_SecurityCraft.reinforcedPlanks_Jungle || par1Block == mod_SecurityCraft.reinforcedPlanks_Acadia || par1Block == mod_SecurityCraft.reinforcedPlanks_DarkOak || par1Block == mod_SecurityCraft.keycardReader || par1Block == mod_SecurityCraft.ironTrapdoor || par1Block == mod_SecurityCraft.keypadFurnace || par1Block == mod_SecurityCraft.panicButton || par1Block == mod_SecurityCraft.FurnaceMine || par1Block == mod_SecurityCraft.usernameLogger || par1Block == mod_SecurityCraft.portableRadar || par1Block instanceof BlockOwnable || par1Block instanceof IOwnable){
    		return true;
    	}else{
    		return false;
    	}
    }
	
	private boolean isCustomizableBlock(Block par1Block){
    	if(par1Block == mod_SecurityCraft.portableRadar || par1Block == mod_SecurityCraft.keypad || par1Block == mod_SecurityCraft.retinalScanner || par1Block == mod_SecurityCraft.keycardReader || par1Block == mod_SecurityCraft.LaserBlock || par1Block == mod_SecurityCraft.inventoryScanner){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    @SideOnly(Side.SERVER)
    public World getServerWorld(){
    	return MinecraftServer.getServer().getEntityWorld();
    }
    
    public Side getSide(){
    	return FMLCommonHandler.instance().getEffectiveSide();
    }
    
    public EntityPlayer getPlayerFromName(String par1){
    	List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    	Iterator iterator = players.iterator();
    	
    	while(iterator.hasNext()){
    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
    		if(tempPlayer.getName().matches(par1)){
    			return tempPlayer;
    		}
    	}
    	
    	return null;
    }
    
    public void setCooldown(int par1){
    	this.cooldownCounter = par1;
    }
    
    public int getCooldown(){
    	return this.cooldownCounter;
    }

}
