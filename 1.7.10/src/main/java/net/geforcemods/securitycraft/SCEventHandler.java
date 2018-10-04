package net.geforcemods.securitycraft;

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
import net.geforcemods.securitycraft.blocks.BlockCageTrap;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockPos;
import net.geforcemods.securitycraft.util.BlockUtils;
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
import net.minecraft.item.Item;
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
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<String, String>();

	public SCEventHandler()
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
		if(!SecurityCraft.config.sayThanksMessage || !event.player.getEntityWorld().isRemote)
			return;

		String tipKey = getRandomTip();
		IChatComponent message;

		if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
			message = new ChatComponentText("[" + EnumChatFormatting.GOLD + "SecurityCraft" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + StatCollector.translateToLocal("messages.securitycraft:tip") + " " + StatCollector.translateToLocal(tipKey) + " ").appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));
		else
			message = new ChatComponentText("[" + EnumChatFormatting.GOLD + "SecurityCraft" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.securitycraft:thanks").replace("#", SecurityCraft.getVersion()) + " " + StatCollector.translateToLocal("messages.securitycraft:tip") + " " + StatCollector.translateToLocal(tipKey));

		event.player.addChatComponentMessage(message);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.player) && event.player.ridingEntity instanceof EntitySecurityCamera)
			event.player.ridingEntity.setDead();
	}

	@SubscribeEvent
	public void onDamageTaken(LivingHurtEvent event)
	{
		if(event.entityLiving != null && PlayerUtils.isPlayerMountedOnCamera(event.entityLiving)){
			event.setCanceled(true);
			return;
		}

		if(event.source == CustomDamageSources.electricity)
			SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(event.entity.posX, event.entity.posY, event.entity.posZ, SCSounds.ELECTRIFIED.path, 0.25F));
	}

	@SubscribeEvent
	public void onBucketUsed(FillBucketEvent event){
		ItemStack result = fillBucket(event.world, event.target);
		if(result == null)
			return;
		event.result = result;
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void onWorldUnloaded(Unload event){
		if(event.world.isRemote)
			((ClientProxy) SecurityCraft.serverProxy).worldViews.clear();
	}

	@SubscribeEvent
	public void onPlayerInteracted(PlayerInteractEvent event){
		if(!event.entityPlayer.worldObj.isRemote){
			World world = event.entityPlayer.worldObj;
			TileEntity tileEntity = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
			Block block = event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z);

			if(event.action != Action.RIGHT_CLICK_BLOCK) return;

			if(event.action == Action.RIGHT_CLICK_BLOCK && PlayerUtils.isHoldingItem(event.entityPlayer, SCContent.codebreaker) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && tileEntity instanceof CustomizableSCTE && PlayerUtils.isHoldingItem(event.entityPlayer, SCContent.universalBlockModifier)){
				event.setCanceled(true);

				if(!(((IOwnable) tileEntity)).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.securitycraft:universalBlockModifier.name"), StatCollector.translateToLocal("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				event.entityPlayer.openGui(SecurityCraft.instance, GuiHandler.CUSTOMIZE_BLOCK, world, event.x, event.y, event.z);
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity instanceof INameable && ((INameable) tileEntity).canBeNamed() && PlayerUtils.isHoldingItem(event.entityPlayer, Items.name_tag) && event.entityPlayer.getCurrentEquippedItem().hasDisplayName()){
				event.setCanceled(true);

				for(String character : new String[]{"(", ")"})
					if(event.entityPlayer.getCurrentEquippedItem().getDisplayName().contains(character)) {
						PlayerUtils.sendMessageToPlayer(event.entityPlayer, "Naming", StatCollector.translateToLocal("messages.securitycraft:naming.error").replace("#n", event.entityPlayer.getCurrentEquippedItem().getDisplayName()).replace("#c", character), EnumChatFormatting.RED);
						return;
					}

				if(((INameable) tileEntity).getCustomName().matches(event.entityPlayer.getCurrentEquippedItem().getDisplayName())) {
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, "Naming", StatCollector.translateToLocal("messages.securitycraft:naming.alreadyMatches").replace("#n", ((INameable) tileEntity).getCustomName()), EnumChatFormatting.RED);
					return;
				}

				if(!event.entityPlayer.capabilities.isCreativeMode)
					event.entityPlayer.getCurrentEquippedItem().stackSize--;

				((INameable) tileEntity).setCustomName(event.entityPlayer.getCurrentEquippedItem().getDisplayName());
				return;
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK && tileEntity != null && isOwnableBlock(block, tileEntity) && PlayerUtils.isHoldingItem(event.entityPlayer, SCContent.universalBlockRemover)){
				event.setCanceled(true);

				if(!((IOwnable) tileEntity).getOwner().isOwner(event.entityPlayer)){
					PlayerUtils.sendMessageToPlayer(event.entityPlayer, StatCollector.translateToLocal("item.securitycraft:universalBlockRemover.name"), StatCollector.translateToLocal("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), EnumChatFormatting.RED);
					return;
				}

				if(block == SCContent.laserBlock){
					world.breakBlock(event.x, event.y, event.z, true);
					BlockLaserBlock.destroyAdjacentLasers(event.world, event.x, event.y, event.z);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}else if(block == SCContent.deactivatedCageTrap && ((BlockCageTrap)world.getBlock(event.x, event.y, event.z)).deactivated) {
					BlockPos originalPos = new BlockPos(event.x, event.y, event.z);
					int[] pos = originalPos.east().up().asArray();

					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(2).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(3).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up().south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(2).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(3).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(2).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(3).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up().south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(2).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(3).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up().south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(2).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(3).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(2).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(3).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up().north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(2).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(3).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up().north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(2).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(3).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up().north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(2).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(3).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(4).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(4).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(4).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(4).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(4).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.east().up(4).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.west().up(4).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(4).south().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(4).north().asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.up(4).asArray();
					if(BlockUtils.getBlock(world, pos[0], pos[1], pos[2]) == SCContent.reinforcedIronBars && ((IOwnable)world.getTileEntity(pos[0], pos[1], pos[2])).getOwner().isOwner(event.entityPlayer))
						BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);

					pos = originalPos.asArray();
					BlockUtils.destroyBlock(world, pos[0], pos[1], pos[2], false);
				}else{
					world.breakBlock(event.x, event.y, event.z, true);
					world.removeTileEntity(event.x, event.y, event.z);
					event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
				}
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.modID.equals("securitycraft")){
			SecurityCraft.configFile.save();

			SecurityCraft.config.setupConfiguration();
		}
	}

	@SubscribeEvent
	public void onBlockPlaced(PlaceEvent event) {
		handleOwnableTEs(event);
	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event){
		if(!event.world.isRemote)
			if(event.world.getTileEntity(event.x, event.y, event.z) != null && event.world.getTileEntity(event.x, event.y, event.z) instanceof CustomizableSCTE){
				CustomizableSCTE te = (CustomizableSCTE) event.world.getTileEntity(event.x, event.y, event.z);

				for(int i = 0; i < te.getNumberOfCustomizableOptions(); i++)
					if(te.itemStacks[i] != null){
						ItemStack stack = te.itemStacks[i];
						EntityItem item = new EntityItem(event.world, event.x, event.y, event.z, stack);
						event.world.spawnEntityInWorld(item);

						te.onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
						te.createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, te);
					}
			}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.target != null && event.target instanceof EntityPlayer && event.target != event.entityLiving.func_94060_bK())
			if(PlayerUtils.isPlayerMountedOnCamera(event.target))
				((EntityLiving)event.entityLiving).setAttackTarget(null);
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.entityPlayer != null && event.entityPlayer.getHeldItem() != null)
		{
			Item held = event.entityPlayer.getHeldItem().getItem();

			if(held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3)
			{
				for(Block rb : IReinforcedBlock.BLOCKS)
				{
					IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

					if(reinforcedBlock.getVanillaBlocks().contains(event.block))
					{
						event.newSpeed = 10000.0F;
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRendered(RenderPlayerEvent.Pre event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.entityPlayer))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer) && Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX) == event.target.blockX && Minecraft.getMinecraft().thePlayer.ridingEntity.posY == event.target.blockY && Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ) == event.target.blockZ)
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderGameOverlay(RenderGameOverlayEvent.Post event){
		if(Minecraft.getMinecraft().thePlayer != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer)){
			if(event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE && Minecraft.getMinecraft().theWorld.getBlock((int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)Minecraft.getMinecraft().thePlayer.ridingEntity.posY, (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ)) instanceof BlockSecurityCamera)
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.resolution, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX), (int)Minecraft.getMinecraft().thePlayer.ridingEntity.posY, (int)Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ), event.mouseX, event.mouseY);
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

			if(monitor != null && monitor.getItem() == SCContent.cameraMonitor)
			{
				String textureToUse = "cameraNotBound";
				double eyeHeight = player.getEyeHeight();
				Vec3 lookVec = Vec3.createVectorHelper((player.posX + (player.getLookVec().xCoord * 5)), ((eyeHeight + player.posY) + (player.getLookVec().yCoord * 5)), (player.posZ + (player.getLookVec().zCoord * 5)));
				MovingObjectPosition mop = world.rayTraceBlocks(Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK && world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof TileEntitySecurityCamera)
				{
					NBTTagCompound cameras = monitor.getTagCompound();

					if(cameras != null)
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

					GL11.glEnable(GL11.GL_BLEND);
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.resolution.getScaledWidth() / 2 - 90 + held * 20 + 2, event.resolution.getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GL11.glDisable(GL11.GL_BLEND);
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.entity))
			event.newfov = ((EntitySecurityCamera) event.entity.ridingEntity).getZoomAmount();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer))
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(MouseEvent event) {
		if(Minecraft.getMinecraft().theWorld != null)
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer))
				event.setCanceled(true);
	}

	private void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		double z = 200;
		double widthFactor = 1F / (double) textureWidth;
		double heightFactor = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, z, u * widthFactor, (v + height) * heightFactor);
		tessellator.addVertexWithUV(x + width, y + height, z, (u + width) * widthFactor, (v + height) * heightFactor);
		tessellator.addVertexWithUV(x + width, y, z, (u + width) * widthFactor, v * heightFactor);
		tessellator.addVertexWithUV(x, y, z, u * widthFactor, v * heightFactor);
		tessellator.draw();
	}

	private String getRandomTip(){
		String[] tips = {
				"messages.tip.scHelp",
				"messages.tip.trello",
				"messages.tip.patreon",
				"messages.tip.discord",
				"messages.tip.scserver"
		};

		return tips[new Random().nextInt(tips.length)];
	}

	private ItemStack fillBucket(World world, MovingObjectPosition position){
		Block block = world.getBlock(position.blockX, position.blockY, position.blockZ);

		if(block == SCContent.bogusWater){
			world.setBlockToAir(position.blockX, position.blockY, position.blockZ);
			return new ItemStack(SCContent.fWaterBucket, 1);
		}else if(block == SCContent.bogusLava){
			world.setBlockToAir(position.blockX, position.blockY, position.blockZ);
			return new ItemStack(SCContent.fLavaBucket, 1);
		}
		else
			return null;
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

		if(SecurityCraft.config.allowCodebreakerItem) //safety so when codebreakers are disabled they can't take damage
			event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);

		if(tileEntity != null && tileEntity instanceof IPasswordProtected && new Random().nextInt(3) == 1)
			return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockMetadata(event.x, event.y, event.z), event.entityPlayer, !SecurityCraft.config.allowCodebreakerItem);

		return false;
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity){
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable);
	}

}
