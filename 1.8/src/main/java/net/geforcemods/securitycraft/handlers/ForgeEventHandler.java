package net.geforcemods.securitycraft.handlers;

import java.util.HashMap;
import java.util.Random;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.ircbot.SCIRCBot;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeEventHandler {
	
	private static HashMap<String, String> tipsWithLink = new HashMap<String, String>();
	
	public ForgeEventHandler()
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/RYsKYx5");
	}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.createIrcBot(event.player.getName());

        String tipKey = getRandomTip();
		
		IChatComponent chatcomponenttext;
		if(tipsWithLink.containsKey(tipKey.split("\\.")[2])) {
			chatcomponenttext = new ChatComponentText("[" + EnumChatFormatting.GOLD + "SecurityCraft" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.thanks").replace("#", mod_SecurityCraft.getVersion()) + " " + StatCollector.translateToLocal("messages.tip") + " " + StatCollector.translateToLocal(tipKey) + " ").appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));
		}
		else {
			chatcomponenttext = new ChatComponentText("[" + EnumChatFormatting.GOLD + "SecurityCraft" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.thanks").replace("#", mod_SecurityCraft.getVersion()) + " " + StatCollector.translateToLocal("messages.tip") + " " + StatCollector.translateToLocal(tipKey));
		}
    	
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
	public void onServerChatEvent(ServerChatEvent event)
	{
		SCIRCBot bot = mod_SecurityCraft.instance.getIrcBot(event.player.getName());
		
		if(bot != null && bot.getMessageMode())
		{
			event.setCanceled(true);
			bot.sendMessage("> " + event.message);
			bot.sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + event.player.getName() + " --> IRC> " + event.message, event.player);
		}
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
		if(!event.entityPlayer.worldObj.isRemote){
			World world = event.entityPlayer.worldObj;
			TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.pos);
			Block block = event.entityPlayer.worldObj.getBlockState(event.pos).getBlock();
			
			if(event.action != Action.RIGHT_CLICK_BLOCK) return;

			if(event.action == Action.RIGHT_CLICK_BLOCK && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.codebreaker) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && tileEntity instanceof CustomizableSCTE && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockModifier)){
				event.setCanceled(true);
				
				if(!((IOwnable) tileEntity).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockModifier.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((TileEntityOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}
				
				event.entityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.CUSTOMIZE_BLOCK, world, event.pos.getX(), event.pos.getY(), event.pos.getZ());	
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.entityPlayer, Items.name_tag) && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);
				
				for(String character : new String[]{"(", ")"}) {
					if(event.entityPlayer.getCurrentEquippedItem().getDisplayName().contains(character)) {
						PlayerUtils.sendMessageToPlayer(event.entityPlayer, "Naming", StatCollector.translateToLocal("messages.naming.error").replace("#n", event.entityPlayer.getCurrentEquippedItem().getDisplayName()).replace("#c", character), EnumChatFormatting.RED);
						return;
					}
				}		
				
				if(((INameable) tileEntity).getCustomName().matches(event.entityPlayer.getCurrentEquippedItem().getDisplayName())) {
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, "Naming", StatCollector.translateToLocal("messages.naming.alreadyMatches").replace("#n", ((INameable) tileEntity).getCustomName()), EnumChatFormatting.RED);
					return;
				}

				event.entityPlayer.getCurrentEquippedItem().stackSize--;

				((INameable) tileEntity).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && isOwnableBlock(block, tileEntity) && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockRemover)){
				event.setCanceled(true);

				if(!((IOwnable) tileEntity).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockRemover.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((TileEntityOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				if(block == mod_SecurityCraft.laserBlock){
					world.destroyBlock(event.pos, true);
					BlockLaserBlock.destroyAdjecentLasers(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ());
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}else{
					world.destroyBlock(event.pos, true);
					world.removeTileEntity(event.pos);
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
	public void onBlockPlaced(PlaceEvent event) {
		handleOwnableTEs(event);
	}
	
	@SubscribeEvent
	public void onBlockBroken(BreakEvent event){
		if(!event.world.isRemote){
			if(event.world.getTileEntity(event.pos) != null && event.world.getTileEntity(event.pos) instanceof CustomizableSCTE){
				CustomizableSCTE te = (CustomizableSCTE) event.world.getTileEntity(event.pos);

				for(int i = 0; i < te.getNumberOfCustomizableOptions(); i++){
					if(te.itemStacks[i] != null){
						ItemStack stack = te.itemStacks[i];
						EntityItem item = new EntityItem(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ(), stack);
						event.world.spawnEntityInWorld(item);
						
						te.onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
						te.createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, te);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.target != null && event.target instanceof EntityPlayer && event.target != event.entityLiving.func_94060_bK())
		{
			if(PlayerUtils.isPlayerMountedOnCamera(event.target))
				((EntityLiving)event.entityLiving).setAttackTarget(null);
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
	
	private void handleOwnableTEs(PlaceEvent event) {
		if(event.world.getTileEntity(event.pos) instanceof IOwnable) {
			String name = event.player.getName();
			String uuid = event.player.getGameProfile().getId().toString();

			((IOwnable) event.world.getTileEntity(event.pos)).getOwner().set(uuid, name);
		}		
	}
	
	private boolean handleCodebreaking(PlayerInteractEvent event) {
		World world = event.entityPlayer.worldObj;
		TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.pos);
		
		if(mod_SecurityCraft.configHandler.allowCodebreakerItem) //safety so when codebreakers are disabled they can't take damage
			event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
		
		if(tileEntity != null && tileEntity instanceof IPasswordProtected && new Random().nextInt(3) == 1) {
			return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.pos), event.entityPlayer, !mod_SecurityCraft.configHandler.allowCodebreakerItem);
		}
		
		return false;
	}
	
	private String getRandomTip(){
		String[] tips = {
				"messages.tip.scHelp",
				"messages.tip.scConnect",
				"messages.tip.trello",
				"messages.tip.patreon",
				"messages.tip.discord",
				"messages.tip.scserver"
		};

		return tips[new Random().nextInt(tips.length)];
	}
  
	private boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable);
    }
	
}
