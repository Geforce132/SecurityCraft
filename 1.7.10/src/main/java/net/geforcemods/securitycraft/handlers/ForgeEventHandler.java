package net.geforcemods.securitycraft.handlers;

import java.util.HashMap;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
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
import net.minecraftforge.event.world.WorldEvent.Unload;

public class ForgeEventHandler {

	private static HashMap<String, String> tipsWithLink = new HashMap<String, String>();
	
	public ForgeEventHandler()
	{
		tipsWithLink.put("trello", "https://trello.com/b/dbCNZwx0/securitycraft");
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
	}
	
	/**
	 * Called whenever a {@link EntityPlayer} joins the game.
	 */
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		mod_SecurityCraft.instance.createIrcBot(event.player.getCommandSenderName());
		
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
	public void onServerChatEvent(ServerChatEvent event)
	{
		SCIRCBot bot = mod_SecurityCraft.instance.getIrcBot(event.player.getCommandSenderName());
		
		if(bot != null && bot.getMessageMode())
		{
			event.setCanceled(true);
			bot.sendMessage("> " + event.message);
			bot.sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + event.player.getCommandSenderName() + " --> IRC> " + event.message, event.player);
		}
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
			World world = event.entityPlayer.worldObj;
			TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
			Block block = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z);
			
			if(event.action != Action.RIGHT_CLICK_BLOCK) return;
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.codebreaker) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}
			
			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && tileEntity instanceof CustomizableSCTE && PlayerUtils.isHoldingItem(event.entityPlayer, mod_SecurityCraft.universalBlockModifier)){
				event.setCanceled(true);

				if(!(((IOwnable) tileEntity)).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockModifier.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				event.entityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.CUSTOMIZE_BLOCK, world, event.x, event.y, event.z);	
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
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.universalBlockRemover.name"), StatCollector.translateToLocal("messages.notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				if(block == mod_SecurityCraft.laserBlock){
					world.func_147480_a(event.x, event.y, event.z, true);
					BlockLaserBlock.destroyAdjacentLasers(event.world, event.x, event.y, event.z);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}else{
					world.func_147480_a(event.x, event.y, event.z, true);
					world.removeTileEntity(event.x, event.y, event.z);
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
			if(event.world.getTileEntity(event.x, event.y, event.z) != null && event.world.getTileEntity(event.x, event.y, event.z) instanceof CustomizableSCTE){
				CustomizableSCTE te = (CustomizableSCTE) event.world.getTileEntity(event.x, event.y, event.z);
				
				for(int i = 0; i < te.getNumberOfCustomizableOptions(); i++){
					if(te.itemStacks[i] != null){
						ItemStack stack = te.itemStacks[i];
						EntityItem item = new EntityItem(event.world, event.x, event.y, event.z, stack);
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
		else if(event.type == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP player = mc.thePlayer;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;
			
			if(held < 0 || held >= player.inventory.mainInventory.length)
				return;
			
			ItemStack monitor = player.inventory.mainInventory[held];

			if(monitor != null && monitor.getItem() == mod_SecurityCraft.cameraMonitor)
			{
				String textureToUse = "cameraNotBound";
	        	double eyeHeight = player.getEyeHeight();
	        	Vec3 lookVec = Vec3.createVectorHelper((player.posX + (player.getLookVec().xCoord * 5)), ((eyeHeight + player.posY) + (player.getLookVec().yCoord * 5)), (player.posZ + (player.getLookVec().zCoord * 5)));
	        	MovingObjectPosition mop = world.rayTraceBlocks(Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);
	        	
	        	if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK && world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof TileEntitySecurityCamera)
	        	{
	        		NBTTagCompound cameras = monitor.getTagCompound();
	        		
	        		if(cameras != null)
	        		{
	        			for(int i = 1; i < 31; i++)
		        		{
		        		    if(!cameras.hasKey("Camera" + i))
		        		        continue;
		        		    
		        			String[] coords = cameras.getString("Camera" + i).split(" ");
	
		        			if(Integer.parseInt(coords[0]) == mop.blockX && Integer.parseInt(coords[1]) == mop.blockY && Integer.parseInt(coords[2]) == mop.blockZ)
		        			{
		        				textureToUse = "cameraBound";
		        				break;
		        			}
		        		}
	        		}
	        		
					GL11.glEnable(GL11.GL_BLEND);
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(mod_SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.resolution.getScaledWidth() / 2 - 90 + held * 20 + 2, event.resolution.getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GL11.glDisable(GL11.GL_BLEND);
	        	}
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

	private void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		double z = 200;
		double f = 1F / (double) textureWidth;
		double f1 = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, z, u * f, (v + height) * f1);
		tessellator.addVertexWithUV(x + width, y + height, z, (u + width) * f, (v + height) * f1);
		tessellator.addVertexWithUV(x + width, y, z, (u + width) * f, v * f1);
		tessellator.addVertexWithUV(x, y, z, u * f, v * f1);
		tessellator.draw();
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
	
	private void handleOwnableTEs(PlaceEvent event) {
		if(event.world.getTileEntity(event.x, event.y, event.z) instanceof IOwnable) {
			String name = event.player.getCommandSenderName();
			String uuid = event.player.getGameProfile().getId().toString();

			((IOwnable) event.world.getTileEntity(event.x, event.y, event.z)).getOwner().set(uuid, name);
		}		
	}
	
	private boolean handleCodebreaking(PlayerInteractEvent event) {
		World world = event.entityPlayer.worldObj;
		TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
		
		if(mod_SecurityCraft.configHandler.allowCodebreakerItem) //safety so when codebreakers are disabled they can't take damage
			event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
		
		if(tileEntity != null && tileEntity instanceof IPasswordProtected && new Random().nextInt(3) == 1) {
			return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockMetadata(event.x, event.y, event.z), event.entityPlayer, !mod_SecurityCraft.configHandler.allowCodebreakerItem);
		}
		
		return false;
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable);
	}
	
}
