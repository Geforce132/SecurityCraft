package net.geforcemods.securitycraft;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.api.TileEntityLinkable;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.BlockSonicSecuritySystem;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.items.ItemUniversalBlockReinforcer;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.PortalSize;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SCWorldListener;
import net.geforcemods.securitycraft.misc.SonicSecuritySystemTracker;
import net.geforcemods.securitycraft.tileentity.IEMPAffected;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem.NoteWrapper;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.block.BlockPortal;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@EventBusSubscriber(modid=SecurityCraft.MODID)
public class SCEventHandler {

	public static HashMap<String, String> tipsWithLink = new HashMap<>();
	public static final Integer NOTE_DELAY = 10;
	public static final Map<EntityPlayer,MutablePair<Integer,Deque<NoteWrapper>>> PLAYING_TUNES = new HashMap<>();

	static {
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
		tipsWithLink.put("outdated", "https://www.curseforge.com/minecraft/mc-mods/security-craft/files/all");
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			PLAYING_TUNES.forEach((player, pair) -> {
				int ticksRemaining = pair.getLeft();

				if (ticksRemaining == 0) {
					if (PlayerUtils.getSelectedItemStack(player, SCContent.portableTunePlayer).isEmpty()) {
						pair.setLeft(-1);
						return;
					}

					NoteWrapper note = pair.getRight().poll();

					if (note != null) {
						SoundEvent sound = ((BlockNote)Blocks.NOTEBLOCK).getInstrument(Instrument.valueOf(note.instrumentName.toUpperCase()).ordinal());
						float pitch = (float)Math.pow(2.0D, (note.noteID - 12) / 12.0D);

						player.world.playSound(null, player.getPosition(), sound, SoundCategory.RECORDS, 3.0F, pitch);
						handlePlayedNote(player.world, player.getPosition(), note.noteID, note.instrumentName);
						pair.setLeft(NOTE_DELAY);
					}
					else
						pair.setLeft(-1);
				}
				else
					pair.setLeft(ticksRemaining - 1);
			});

			//remove finished tunes
			if (PLAYING_TUNES.size() > 0) {
				Iterator<Entry<EntityPlayer,MutablePair<Integer,Deque<NoteWrapper>>>> entries = PLAYING_TUNES.entrySet().iterator();

				while (entries.hasNext()) {
					if (entries.next().getValue().left == -1)
						entries.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(ConfigHandler.sayThanksMessage)
		{
			String tipKey = getRandomTip();
			ITextComponent message = new TextComponentString("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] ")
					.appendSibling(Utils.localize("messages.securitycraft:thanks", SecurityCraft.getVersion()))
					.appendSibling(new TextComponentString(" "))
					.appendSibling(Utils.localize("messages.securitycraft:tip"))
					.appendSibling(new TextComponentString(" "))
					.appendSibling(Utils.localize(tipKey));

			if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
				message.appendSibling(new TextComponentString(" ")).appendSibling(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));

			event.player.sendMessage(message);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		EntityPlayerMP player = (EntityPlayerMP)event.player;

		if(player.getSpectatingEntity() instanceof EntitySecurityCamera)
		{
			EntitySecurityCamera cam = (EntitySecurityCamera)player.getSpectatingEntity();
			TileEntity tile = player.world.getTileEntity(new BlockPos(cam.posX, cam.posY, cam.posZ));

			if(tile instanceof TileEntitySecurityCamera)
				((TileEntitySecurityCamera)tile).stopViewing();

			cam.setDead();
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;

		if(event.getSource() == CustomDamageSources.ELECTRICITY)
			world.playSound(null, entity.getPosition(), SCSounds.ELECTRIFIED.event, SoundCategory.BLOCKS, 0.25F, 1.0F);

		if(!world.isRemote && entity instanceof EntityPlayerMP && PlayerUtils.isPlayerMountedOnCamera(entity)) {
			EntityPlayerMP player = (EntityPlayerMP)entity;

			((EntitySecurityCamera)player.getSpectatingEntity()).stopViewing(player);
		}
	}

	@SubscribeEvent
	public static void onBucketUsed(FillBucketEvent event){
		if(event.getTarget() == null)
			return;

		World world = event.getWorld();
		BlockPos pos = event.getTarget().getBlockPos();
		ItemStack result;
		Block block = world.getBlockState(pos).getBlock();

		if(block == SCContent.fakeWater)
			result = new ItemStack(SCContent.fWaterBucket, 1);
		else if(block == SCContent.fakeLava)
			result = new ItemStack(SCContent.fLavaBucket, 1);
		else
			return;

		world.setBlockToAir(pos);
		event.setFilledBucket(result);
		event.setResult(Result.ALLOW);
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	//side effect for keypad door: it is now only openable with an empty hand
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if(!stack.isEmpty() && item != SCContent.universalBlockRemover && item != SCContent.universalBlockModifier && item != SCContent.universalOwnerChanger)
		{
			if(!(item instanceof ItemBlock))
			{
				Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

				if(block == SCContent.keypadDoor || block == SCContent.reinforcedDoor || block == SCContent.reinforcedIronTrapdoor || block == SCContent.scannerDoor)
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()))
		{
			event.setCanceled(true);
			return;
		}

		World world = event.getWorld();
		TileEntity te = world.getTileEntity(event.getPos());
		Block block = world.getBlockState(event.getPos()).getBlock();

		if(te instanceof ILockable && ((ILockable) te).isLocked() && ((ILockable) te).disableInteractionWhenLocked(world, event.getPos(), event.getEntityPlayer()))
		{
			if(event.getHand() == EnumHand.MAIN_HAND)
				PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), Utils.localize(block), Utils.localize("messages.securitycraft:sonic_security_system.locked", Utils.localize(block)), TextFormatting.DARK_RED, false);

			event.setCanceled(true);
			return;
		}

		if(event.getItemStack().getItem() == Items.REDSTONE && te instanceof IEMPAffected && ((IEMPAffected)te).isShutDown())
		{
			((IEMPAffected)te).reactivate();

			if(!event.getEntityPlayer().isCreative())
				event.getItemStack().shrink(1);

			event.getEntityPlayer().swingArm(event.getHand());
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			return;
		}

		if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.keyPanel, event.getHand()))
		{
			for(IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles())
			{
				if(pc.getOriginalBlock() == block)
				{
					event.setUseBlock(Result.DENY);
					event.setUseItem(Result.ALLOW);
				}
			}

			return;
		}

		if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.codebreaker, event.getHand()) && handleCodebreaking(event)) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			return;
		}

		if(PlayerUtils.isHoldingItem(event.getEntityPlayer(), SCContent.universalBlockModifier, event.getHand())){
			if(te instanceof IModuleInventory) {
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);

				if(te instanceof IOwnable && !((IOwnable) te).getOwner().isOwner(event.getEntityPlayer())){
					if(!(te.getBlockType() instanceof BlockDisguisable) || (((ItemBlock)((BlockDisguisable)te.getBlockType()).getDisguisedStack(world, event.getPos()).getItem()).getBlock() instanceof BlockDisguisable))
						PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), Utils.localize("item.securitycraft:universalBlockModifier.name"), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) te).getOwner().getName())), TextFormatting.RED);

					return;
				}

				event.getEntityPlayer().openGui(SecurityCraft.instance, GuiHandler.CUSTOMIZE_BLOCK, world, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
				return;
			}
		}

		if(te instanceof INameSetter && (te instanceof TileEntitySecurityCamera || te instanceof TileEntityPortableRadar) && PlayerUtils.isHoldingItem(event.getEntityPlayer(), Items.NAME_TAG, event.getHand()) && event.getEntityPlayer().getHeldItem(event.getHand()).hasDisplayName()){
			ItemStack nametag = event.getEntityPlayer().getHeldItem(event.getHand());
			INameSetter nameable = (INameSetter)te;

			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);

			if(nameable.getName().equals(nametag.getDisplayName())) {
				PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), Utils.localize(event.getWorld().getBlockState(event.getPos()).getBlock().getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:naming.alreadyMatches", nameable.getDisplayName()), TextFormatting.RED);
				return;
			}

			if(!event.getEntityPlayer().isCreative())
				nametag.shrink(1);

			if (!world.isRemote)
				nameable.setCustomName(nametag.getDisplayName());

			PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), Utils.localize(event.getWorld().getBlockState(event.getPos()).getBlock().getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:naming.named", nameable.getDisplayName()), TextFormatting.GREEN);
			return;
		}

		//all the sentry functionality for when the sentry is diguised
		List<EntitySentry> sentries = world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

		if(!sentries.isEmpty())
		{
			event.setCanceled(sentries.get(0).processInteract(event.getEntityPlayer(), event.getHand())); //cancel if an action was taken
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer())) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.FAIL);
			return;
		}

		ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
		Item held = stack.getItem();

		if(held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3)
			ItemUniversalBlockReinforcer.convertBlock(stack, event.getPos(), event.getEntityPlayer());
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event)
	{
		if(!event.getWorld().isRemote) {
			BlockPos pos = event.getPos();
			World world = event.getWorld();

			if(world.getTileEntity(pos) instanceof IModuleInventory){
				IModuleInventory te = (IModuleInventory) world.getTileEntity(pos);

				for(int i = 100; i - 100 < te.getMaxNumberOfModules(); i++) {
					if(!te.getStackInSlot(i).isEmpty()){
						ItemStack stack = te.getStackInSlot(i);
						EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
						WorldUtils.addScheduledTask(world, () -> world.spawnEntity(item));

						te.onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModuleType());

						if(te instanceof TileEntityLinkable)
							((TileEntityLinkable)te).createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModuleType() }, (TileEntityLinkable)te);

						if(te instanceof TileEntitySecurityCamera)
						{
							TileEntitySecurityCamera cam = (TileEntitySecurityCamera)te;

							cam.getWorld().notifyNeighborsOfStateChange(cam.getPos().offset(cam.getWorld().getBlockState(cam.getPos()).getValue(BlockSecurityCamera.FACING), -1), cam.getWorld().getBlockState(cam.getPos()).getBlock(), true);
						}
					}
				}
			}
		}

		List<EntitySentry> sentries = event.getWorld().getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(event.getPos()));

		//don't let people break the disguise block
		if(!sentries.isEmpty() && !sentries.get(0).getDisguiseModule().isEmpty())
		{
			ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
			Block block = ((ItemModule)disguiseModule.getItem()).getBlockAddon(disguiseModule.getTagCompound());

			if(block == event.getWorld().getBlockState(event.getPos()).getBlock())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event)
	{
		if(event.getModID().equals(SecurityCraft.MODID))
			ConfigManager.sync(SecurityCraft.MODID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event)
	{
		TileEntity te = event.getWorld().getTileEntity(event.getPos());

		if(te instanceof IOwnable) {
			String name = event.getPlayer().getName();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable)te).setOwner(uuid, name);
		}
	}

	@SubscribeEvent
	public static void onBlockPlaced(PlaceEvent event) {
		//reinforced obsidian portal handling
		if(event.getState().getBlock() == Blocks.FIRE && event.getWorld().getBlockState(event.getPos().down()).getBlock() == SCContent.reinforcedObsidian)
		{
			PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

			if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
				portalSize.placePortalBlocks();
			else
			{
				portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
					portalSize.placePortalBlocks();
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		//fix for spawning under the portal
		if(event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) //nether
		{
			BlockPos pos = event.getEntity().getPosition();

			//check for obsidian or reinforced obsidian from the player's position up to the world height
			do
			{
				if(event.getWorld().getBlockState(pos).getBlock() == Blocks.OBSIDIAN)
				{
					//check if the block is part of a valid portal, and if so move the entity down
					BlockPortal.Size portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL) //sometimes the top of the portal is more valid than the bottom o.O
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else //check other axis
					{
						portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
				else if(event.getWorld().getBlockState(pos).getBlock() == SCContent.reinforcedObsidian) //analogous to if check above
				{
					PortalSize portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid())
					{
						double y = pos.getY() + 0.5D;

						if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else
					{
						portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid())
						{
							double y = pos.getY() + 0.5D;

							if(event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
			}
			while((pos = pos.up()).getY() < Math.min(event.getWorld().getHeight(), 256)); //open cubic chunks "fix"
		}
	}

	@SubscribeEvent
	public static void onNeighborNotify(NeighborNotifyEvent event)
	{
		//prevent portal blocks from disappearing because they think they're not inside of a proper portal frame
		if(event.getState().getBlock() == Blocks.PORTAL)
		{
			EnumFacing.Axis axis = event.getState().getValue(BlockPortal.AXIS);

			if (axis == EnumFacing.Axis.X)
			{
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
			else if (axis == EnumFacing.Axis.Z)
			{
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
			return;

		if(event.getTarget() instanceof EntitySentry)
			((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event)
	{
		event.getWorld().addEventListener(new SCWorldListener());
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()) && event.getItemStack().getItem() != SCContent.cameraMonitor)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onLivingDestroyEvent(LivingDestroyBlockEvent event)
	{
		event.setCanceled(event.getEntity() instanceof EntityWither && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public static void onNoteBlockPlayed(NoteBlockEvent.Play event)
	{
		handlePlayedNote(event.getWorld(), event.getPos(), event.getVanillaNoteId(), event.getInstrument().name());
	}

	private static void handlePlayedNote(World world, BlockPos pos, int vanillaNoteId, String instrumentName) {
		List<TileEntitySonicSecuritySystem> sonicSecuritySystems = SonicSecuritySystemTracker.getSonicSecuritySystemsInRange(world, pos);

		for(TileEntitySonicSecuritySystem te : sonicSecuritySystems) {

			// If the SSS is disabled, don't listen to any notes
			if(!te.isActive())
				continue;

			// If the SSS is recording, record the note being played
			if(te.isRecording())
			{
				te.recordNote(vanillaNoteId, instrumentName);
			}
			// If the SSS is active, check to see if the note being played matches the saved combination.
			// If so, toggle its redstone power output on
			else if(te.listenToNote(vanillaNoteId, instrumentName))
			{
				te.correctTuneWasPlayed = true;
				te.powerCooldown = te.signalLength.get();

				if (te.hasModule(EnumModuleType.REDSTONE)) {
					world.setBlockState(te.getPos(), te.getWorld().getBlockState(te.getPos()).withProperty(BlockSonicSecuritySystem.POWERED, true));
					world.notifyNeighborsOfStateChange(te.getPos(), SCContent.sonicSecuritySystem, false);
				}
			}
		}
	}

	private static boolean handleCodebreaking(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getEntityPlayer().world;
		TileEntity tileEntity = event.getEntityPlayer().world.getTileEntity(event.getPos());

		if(tileEntity instanceof IPasswordProtected && ((IPasswordProtected)tileEntity).isCodebreakable())
		{
			if(ConfigHandler.allowCodebreakerItem)
			{
				if(event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == SCContent.codebreaker)
					event.getEntityPlayer().getHeldItem(event.getHand()).damageItem(1, event.getEntityPlayer());

				if(event.getEntityPlayer().isCreative() || new Random().nextInt(3) == 1)
					return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getEntityPlayer());
				else {
					PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), Utils.localize("item.securitycraft:codebreaker.name"), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
					return true;
				}
			}
			else {
				Block block = world.getBlockState(event.getPos()).getBlock();

				PlayerUtils.sendMessageToPlayer(event.getEntityPlayer(), Utils.localize(block.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
			}
		}

		return false;
	}

	private static String getRandomTip(){
		String[] tips = {
				"messages.securitycraft:tip.scHelp",
				"messages.securitycraft:tip.patreon",
				"messages.securitycraft:tip.discord",
				"messages.securitycraft:tip.scserver",
				"messages.securitycraft:tip.outdated"
		};

		return tips[new Random().nextInt(isOutdated() ? tips.length : tips.length - 1)];
	}

	private static boolean isOutdated()
	{
		return ForgeVersion.getResult(Loader.instance().activeModContainer()).status == Status.OUTDATED;
	}
}
