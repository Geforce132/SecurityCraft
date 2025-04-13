package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.EnumDetectionMode;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.PortalSize;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SCWorldListener;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.UpdateTeamPrecedence;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
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
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID)
public class SCEventHandler {
	public static final Map<String, String> TIPS_WITH_LINK = new HashMap<>();
	public static final Map<EntityPlayer, MutablePair<Integer, Deque<NoteWrapper>>> PLAYING_TUNES = new HashMap<>();
	private static final Integer NOTE_DELAY = 9;

	static {
		TIPS_WITH_LINK.put("patreon", "https://www.patreon.com/Geforce");
		TIPS_WITH_LINK.put("discord", "https://discord.gg/U8DvBAW");
		TIPS_WITH_LINK.put("outdated", "https://www.curseforge.com/minecraft/mc-mods/security-craft/files/all");
	}

	private SCEventHandler() {}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.START)
			SecurityCameraBlockEntity.resetForceLoadingCounter();
		else {
			PLAYING_TUNES.forEach((player, pair) -> {
				int ticksRemaining = pair.getLeft();

				if (ticksRemaining == 0) {
					if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.portableTunePlayer).isEmpty()) {
						pair.setLeft(-1);
						return;
					}

					NoteWrapper note = pair.getRight().poll();

					if (note != null) {
						SoundEvent sound = ((BlockNote) Blocks.NOTEBLOCK).getInstrument(Instrument.valueOf(note.instrumentName.toUpperCase()).ordinal());
						float pitch = (float) Math.pow(2.0D, (note.noteID - 12) / 12.0D);

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
				Iterator<Entry<EntityPlayer, MutablePair<Integer, Deque<NoteWrapper>>>> entries = PLAYING_TUNES.entrySet().iterator();

				while (entries.hasNext()) {
					if (entries.next().getValue().left == -1)
						entries.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (ConfigHandler.sayThanksMessage) {
			String tipKey = getRandomTip();
			//@formatter:off
			ITextComponent message = new TextComponentString("[" + TextFormatting.GOLD + "SecurityCraft" + TextFormatting.WHITE + "] ")
					.appendSibling(Utils.localize("messages.securitycraft:thanks", SecurityCraft.getVersion()))
					.appendSibling(new TextComponentString(" "))
					.appendSibling(Utils.localize("messages.securitycraft:tip"))
					.appendSibling(new TextComponentString(" "))
					.appendSibling(Utils.localize(tipKey));
			//@formatter:on

			if (TIPS_WITH_LINK.containsKey(tipKey.split("\\.")[2]))
				message.appendSibling(new TextComponentString(" ")).appendSibling(ForgeHooks.newChatWithLinks(TIPS_WITH_LINK.get(tipKey.split("\\.")[2])));

			event.player.sendMessage(message);
		}

		if (ConfigHandler.enableTeamOwnership && event.player instanceof EntityPlayerMP)
			SecurityCraft.network.sendTo(new UpdateTeamPrecedence(ConfigHandler.teamOwnershipPrecedence), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLevelLoad(WorldEvent.Load event) {
		World world = event.getWorld();

		if (world instanceof WorldServer && world.provider.getDimension() == 0) {
			SaltData.refreshLevel(((WorldServer) world));
			BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.clear();
		}
		else if (world.isRemote) {
			CameraController.removeAllFeeds();
			CameraViewAreaExtension.clear();
		}
	}

	@SubscribeEvent
	public static void onLevelUnload(WorldEvent.Unload event) {
		World level = event.getWorld();

		if (level instanceof WorldServer && level.provider.getDimension() == 0)
			SaltData.invalidate();
	}

	@SubscribeEvent
	public static void onLivingAttacked(LivingAttackEvent event) {
		if (ConfigHandler.reinforcedSuffocationDamage != -1) {
			EntityLivingBase entity = event.getEntityLiving();

			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				World level = player.world;
				DamageSource damageSource = event.getSource();

				if (!player.isCreative() && damageSource == DamageSource.IN_WALL && !player.isEntityInvulnerable(damageSource) && BlockUtils.isInsideUnownedReinforcedBlocks(level, player, player.getEyeHeight())) {
					player.attackEntityFrom(CustomDamageSources.IN_REINFORCED_WALL, ConfigHandler.reinforcedSuffocationDamage);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;

		if (event.getSource() == CustomDamageSources.ELECTRICITY)
			world.playSound(null, entity.getPosition(), SCSounds.ELECTRIFIED.event, SoundCategory.BLOCKS, 0.25F, 1.0F);

		if (!world.isRemote && entity instanceof EntityPlayerMP && PlayerUtils.isPlayerMountedOnCamera(entity)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;

			((SecurityCamera) player.getSpectatingEntity()).stopViewing(player);
		}
	}

	@SubscribeEvent
	public static void onDismount(EntityMountEvent event) {
		if (!event.getWorldObj().isRemote && ConfigHandler.preventReinforcedFloorGlitching && event.isDismounting() && event.getEntityBeingMounted() instanceof EntityBoat && event.getEntityMounting() instanceof EntityPlayer) {
			EntityBoat boat = (EntityBoat) event.getEntityBeingMounted();
			EntityPlayer player = (EntityPlayer) event.getEntityMounting();

			if (player.isEntityAlive() && !player.capabilities.disableDamage) {
				Vec3d oldPlayerPos = new Vec3d(player.posX, player.posY, player.posZ);
				Vec3d incorrectDismountLocation = new Vec3d(boat.posX, boat.posY + player.height + 0.001D, boat.posZ);
				Vec3d dismountLocation;

				player.dismountEntity(boat);
				dismountLocation = new Vec3d(player.posX, player.posY, player.posZ);

				if (dismountLocation.equals(incorrectDismountLocation) && (BlockUtils.isInsideUnownedReinforcedBlocks(player.world, player, player.getEyeHeight()) || BlockUtils.isInsideUnownedReinforcedBlocks(player.world, player, player.height / 2) || BlockUtils.isInsideUnownedReinforcedBlocks(player.world, player, 0))) {
					player.rotationYaw = boat.rotationYaw + 180.0F % 360.0F; //The y-rotation is changed for the calculation of the new dismount location behind the boat in the next line
					player.setPosition(oldPlayerPos.x, oldPlayerPos.y, oldPlayerPos.z);
					player.dismountEntity(boat);
					dismountLocation = new Vec3d(player.posX, player.posY, player.posZ);

					if (dismountLocation.equals(incorrectDismountLocation))
						event.setCanceled(true);
				}

				player.setPosition(oldPlayerPos.x, oldPlayerPos.y, oldPlayerPos.z);
			}
		}
	}

	@SubscribeEvent
	public static void onBucketUsed(FillBucketEvent event) {
		if (event.getTarget() == null)
			return;

		World world = event.getWorld();
		BlockPos pos = event.getTarget().getBlockPos();
		ItemStack result;
		Block block = world.getBlockState(pos).getBlock();

		if (block == SCContent.fakeWater)
			result = new ItemStack(SCContent.fWaterBucket, 1);
		else if (block == SCContent.fakeLava)
			result = new ItemStack(SCContent.fLavaBucket, 1);
		else
			return;

		world.setBlockToAir(pos);
		event.setFilledBucket(result);
		event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public static void onHarvestDrops(HarvestDropsEvent event) {
		TileEntity te = event.getWorld().getTileEntity(event.getPos());

		if (te instanceof INameSetter && ((INameSetter) te).hasCustomName())
			event.getDrops().get(0).setStackDisplayName(((INameSetter) te).getName());
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	//side effect for keypad door: it is now only openable with an empty hand
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if (!stack.isEmpty() && !(item instanceof ItemBlock) && item != SCContent.adminTool && item != SCContent.codebreaker && item != SCContent.universalBlockRemover && item != SCContent.universalBlockModifier && item != SCContent.universalKeyChanger && item != SCContent.universalOwnerChanger && !(item instanceof ModuleItem)) {
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

			if (block == SCContent.keypadDoor)
				event.setUseItem(Result.DENY);
			else if (block == SCContent.reinforcedDoor || block == SCContent.reinforcedIronTrapdoor || block == SCContent.scannerDoor)
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			event.setCanceled(true);
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		TileEntity te = world.getTileEntity(event.getPos());
		IBlockState state = world.getBlockState(event.getPos());
		Block block = state.getBlock();
		EnumHand hand = event.getHand();

		if (te instanceof ILockable && ((ILockable) te).isLocked() && ((ILockable) te).disableInteractionWhenLocked(world, pos, player) && !player.isSneaking()) {
			if (hand == EnumHand.MAIN_HAND)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(block), Utils.localize("messages.securitycraft:sonic_security_system.locked", Utils.localize(block)), TextFormatting.DARK_RED, false);

			event.setCanceled(true);
			return;
		}

		if (te instanceof IOwnable) {
			IOwnable ownable = (IOwnable) te;
			Owner owner = ownable.getOwner();

			if (!owner.isValidated()) {
				if (ownable.isOwnedBy(player)) {
					owner.setValidated(true);
					ownable.onValidate();
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:ownable.validate"), TextFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:ownable.ownerNotValidated"), TextFormatting.RED);

				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);
				return;
			}
		}

		if (event.getItemStack().getItem() == Items.REDSTONE && te instanceof IEMPAffected && ((IEMPAffected) te).isShutDown()) {
			((IEMPAffected) te).reactivate();

			if (!player.isCreative())
				event.getItemStack().shrink(1);

			player.swingArm(hand);
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			return;
		}

		Item heldItem = player.getHeldItem(hand).getItem();

		if (heldItem == SCContent.keyPanel && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player))) {
			for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (pc.isUnprotectedBlock(state)) {
					event.setUseBlock(Result.DENY);
					event.setUseItem(Result.ALLOW);
				}
			}

			return;
		}

		if (heldItem == SCContent.codebreaker && te instanceof ICodebreakable) {
			((ICodebreakable) te).handleCodebreaking(player, event.getHand());
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
			return;
		}

		if (heldItem == SCContent.universalBlockModifier) {
			if (te instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) te).isOpen() && ((DisplayCaseBlockEntity) te).getDisplayedStack().isEmpty()))
				return;
			else if (te instanceof IModuleInventory) {
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.SUCCESS);

				if (te instanceof IOwnable && !((IOwnable) te).isOwnedBy(player)) {
					if (!(te.getBlockType() instanceof IDisguisable) || (((IDisguisable) te.getBlockType()).getDisguisedBlockState(te).getBlock() instanceof IDisguisable))
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalBlockModifier), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) te).getOwner())), TextFormatting.RED);

					return;
				}

				player.openGui(SecurityCraft.instance, Screens.CUSTOMIZE_BLOCK.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
				return;
			}
		}

		if (block instanceof DisplayCaseBlock && event.getEntity().isSneaking() && player.getHeldItemMainhand().isEmpty() && !player.getHeldItemOffhand().isEmpty()) {
			event.setUseBlock(Result.ALLOW);
			event.setUseItem(Result.DENY);
			return;
		}

		//all the sentry functionality for when the sentry is diguised
		List<Sentry> sentries = world.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(pos));

		if (!sentries.isEmpty()) {
			Sentry sentry = sentries.get(0);

			if (pos.equals(sentry.getPosition())) {
				event.setCanceled(sentry.processInteract(player, hand)); //cancel if an action was taken
				event.setCancellationResult(EnumActionResult.SUCCESS);
			}
		}
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if (ConfigHandler.inWorldUnReinforcing) {
			if (PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer())) {
				event.setCanceled(true);
				event.setCancellationResult(EnumActionResult.FAIL);
				return;
			}

			ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
			Item held = stack.getItem();

			if (held == SCContent.universalBlockReinforcerLvL1 || held == SCContent.universalBlockReinforcerLvL2 || held == SCContent.universalBlockReinforcerLvL3) {
				UniversalBlockReinforcerItem.maybeRemoveMending(stack);

				if (UniversalBlockReinforcerItem.convertBlock(stack, event.getPos(), event.getEntityPlayer()))
					event.setCanceled(true); //When the client knows that a block will be converted on the server, it should not destroy that block (e.g. via instamining)
			}
		}
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();

		//don't let players in creative mode break the disguise block. it's not possible to break it in other gamemodes
		if (event.getPlayer().isCreative()) {
			List<Sentry> sentries = world.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(event.getPos()));

			if (!sentries.isEmpty() && event.getPos().equals(sentries.get(0).getPosition())) {
				event.setCanceled(true);
				return;
			}
		}

		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof IModuleInventory) {
				IModuleInventory te = (IModuleInventory) tile;

				if (te.shouldDropModules()) {
					for (int i = 100; i - 100 < te.getMaxNumberOfModules(); i++) {
						if (!te.getStackInSlot(i).isEmpty()) {
							ItemStack stack = te.getStackInSlot(i);
							EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
							Utils.addScheduledTask(world, () -> world.spawnEntity(item));

							te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

							if (te instanceof LinkableBlockEntity) {
								LinkableBlockEntity linkable = (LinkableBlockEntity) te;

								linkable.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) stack.getItem()).getModuleType(), false), linkable);
							}

							if (te instanceof SecurityCameraBlockEntity) {
								SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) te;

								cam.getWorld().notifyNeighborsOfStateChange(cam.getPos().offset(cam.getWorld().getBlockState(cam.getPos()).getValue(SecurityCameraBlock.FACING), -1), cam.getWorld().getBlockState(cam.getPos()).getBlock(), true);
							}
						}
					}
				}
			}

			EntityPlayer player = event.getPlayer();
			IBlockState state = event.getState();

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTileEntitiesInRange(world, pos).forEach(detector -> detector.log(player, EnumDetectionMode.BREAK, pos, state));
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(SecurityCraft.MODID))
			ConfigManager.sync(SecurityCraft.MODID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event) {
		TileEntity te = event.getWorld().getTileEntity(event.getPos());

		if (te instanceof IOwnable) {
			String name = event.getPlayer().getName();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable) te).setOwner(uuid, name);
		}
	}

	@SubscribeEvent
	public static void onBlockPlaced(PlaceEvent event) {
		World world = event.getWorld();

		//reinforced obsidian portal handling
		if (event.getState().getBlock() == Blocks.FIRE && world.getBlockState(event.getPos().down()).getBlock() == SCContent.reinforcedObsidian) {
			PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

			if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
				portalSize.placePortalBlocks();
			else {
				portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() && portalSize.getPortalBlockCount() == 0)
					portalSize.placePortalBlocks();
			}
		}

		if (!world.isRemote && event.getEntity() instanceof EntityPlayer) {
			BlockPos pos = event.getPos();
			IBlockState state = event.getState();

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTileEntitiesInRange(world, pos).forEach(detector -> detector.log((EntityPlayer) event.getEntity(), EnumDetectionMode.PLACE, pos, state));
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		//fix for spawning under the portal
		if (event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) { //nether
			BlockPos pos = event.getEntity().getPosition();

			//check for obsidian or reinforced obsidian from the player's position up to the world height
			do {
				if (event.getWorld().getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
					//check if the block is part of a valid portal, and if so move the entity down
					BlockPortal.Size portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid()) {
						double y = pos.getY() + 0.5D;

						if (event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else { //check other axis
						portalSize = new BlockPortal.Size(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid()) {
							double y = pos.getY() + 0.5D;

							if (event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
				else if (event.getWorld().getBlockState(pos).getBlock() == SCContent.reinforcedObsidian) { //analogous to if check above
					PortalSize portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.X);

					if (portalSize.isValid()) {
						double y = pos.getY() + 0.5D;

						if (event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
							y -= 3.0D;

						event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
						break;
					}
					else {
						portalSize = new PortalSize(event.getWorld(), pos, EnumFacing.Axis.Z);

						if (portalSize.isValid()) {
							double y = pos.getY() + 0.5D;

							if (event.getWorld().getBlockState(pos.down()).getBlock() == Blocks.PORTAL)
								y -= 3.0D;

							event.getEntity().setPosition(pos.getX() + 0.5D, y, pos.getZ() + 0.5D);
							break;
						}
					}
				}
			}
			while ((pos = pos.up()).getY() < Math.min(event.getWorld().getHeight(), 256)); //open cubic chunks "fix"
		}
	}

	@SubscribeEvent
	public static void onNeighborNotify(NeighborNotifyEvent event) {
		//prevent portal blocks from disappearing because they think they're not inside of a proper portal frame
		if (event.getState().getBlock() == Blocks.PORTAL) {
			EnumFacing.Axis axis = event.getState().getValue(BlockPortal.AXIS);

			if (axis == EnumFacing.Axis.X) {
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.X);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
			else if (axis == EnumFacing.Axis.Z) {
				PortalSize portalSize = new PortalSize(event.getWorld(), event.getPos(), EnumFacing.Axis.Z);

				if (portalSize.isValid() || portalSize.getPortalBlockCount() > portalSize.getWidth() * portalSize.getHeight())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer)
			return;

		if (event.getTarget() instanceof Sentry)
			((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		event.getWorld().addEventListener(new SCWorldListener());
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()) && event.getItemStack().getItem() != SCContent.cameraMonitor)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onLivingDestroyEvent(LivingDestroyBlockEvent event) {
		event.setCanceled(event.getEntity() instanceof EntityWither && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public static void onEntityTeleport(EnderTeleportEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		Vec3d target = new Vec3d(event.getTargetX(), event.getTargetY(), event.getTargetZ());

		event.setCanceled(handleEntityTeleport(entity, entity.getPositionVector(), target, TeleportationType.getTypeFromEvent(entity, target)));
	}

	public static boolean handleEntityTeleport(EntityLivingBase entity, Vec3d source, Vec3d target, TeleportationType type) {
		World world = entity.getEntityWorld();
		List<RiftStabilizerBlockEntity> targetPosTileEntities = BlockEntityTracker.RIFT_STABILIZER.getTileEntitiesInRange(world, target);
		List<RiftStabilizerBlockEntity> sourcePosTileEntities = BlockEntityTracker.RIFT_STABILIZER.getTileEntitiesInRange(world, source);
		List<RiftStabilizerBlockEntity> tileEntities = new ArrayList<>();
		RiftStabilizerBlockEntity riftStabilizer = null;
		boolean targetPosProhibited = false;

		tileEntities.addAll(targetPosTileEntities);
		tileEntities.addAll(sourcePosTileEntities);
		tileEntities = tileEntities.stream().distinct().sorted(Comparator.comparingDouble(t -> Math.min(t.getPos().distanceSqToCenter(target.x, target.y, target.z), t.getPos().distanceSqToCenter(source.x, source.y, source.z)))).collect(Collectors.toList());

		for (RiftStabilizerBlockEntity te : tileEntities) {
			if (!te.isDisabled() && te.getFilter(type) && (!(entity instanceof EntityPlayer) || !(te.isOwnedBy((entity)) && te.ignoresOwner()) && !te.isAllowed(entity))) {
				riftStabilizer = te;
				targetPosProhibited = te.getPos().distanceSqToCenter(target.x, target.y, target.z) < te.getPos().distanceSqToCenter(source.x, source.y, source.z);
				break;
			}
		}

		if (riftStabilizer != null) {
			BlockPos pos = riftStabilizer.getPos();
			Vec3d centerPos = getCenter(new AxisAlignedBB(pos));
			Vec3d from = targetPosProhibited ? target : source;
			Vec3d distance = from.subtract(centerPos);

			if (entity instanceof EntityPlayer) {
				EntityPlayer player = ((EntityPlayer) entity);

				world.playSound(null, source.x, source.y, source.z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.5F);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.riftStabilizer), Utils.localize(targetPosProhibited ? "messages.securitycraft:rift_stabilizer.no_teleport_to" : "messages.securitycraft:rift_stabilizer.no_teleport_from"), TextFormatting.RED);

				if (riftStabilizer.isModuleEnabled(ModuleType.HARMING))
					player.attackEntityFrom(DamageSource.FALL, 5.0F);
			}

			riftStabilizer.setLastTeleport(Math.max(Math.abs(distance.x), Math.max(Math.abs(distance.y), Math.abs(distance.z))) - 0.5D, type);

			if (riftStabilizer.isModuleEnabled(ModuleType.REDSTONE)) {
				int signalLength = riftStabilizer.getSignalLength();

				world.setBlockState(pos, world.getBlockState(pos).cycleProperty(RiftStabilizerBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(world, pos, SCContent.riftStabilizer);

				if (signalLength > 0)
					world.scheduleUpdate(pos, SCContent.riftStabilizer, signalLength);
			}

			return true;
		}

		return false;
	}

	@SubscribeEvent
	public static void onNoteBlockPlayed(NoteBlockEvent.Play event) {
		handlePlayedNote(event.getWorld(), event.getPos(), event.getVanillaNoteId(), event.getInstrument().name());
	}

	private static void handlePlayedNote(World world, BlockPos pos, int vanillaNoteId, String instrumentName) {
		List<SonicSecuritySystemBlockEntity> sonicSecuritySystems = BlockEntityTracker.SONIC_SECURITY_SYSTEM.getTileEntitiesInRange(world, pos);

		for (SonicSecuritySystemBlockEntity te : sonicSecuritySystems) {
			// If the SSS is disabled, don't listen to any notes
			if (!te.isActive())
				continue;

			// If the SSS is recording, record the note being played
			if (te.isRecording())
				te.recordNote(vanillaNoteId, instrumentName);
			// If the SSS is active, check to see if the note being played matches the saved combination.
			// If so, toggle its redstone power output on
			else
				te.listenToNote(vanillaNoteId, instrumentName);
		}
	}

	private static String getRandomTip() {
		//@formatter:off
		String[] tips = {
				"messages.securitycraft:tip.scHelp",
				"messages.securitycraft:tip.patreon",
				"messages.securitycraft:tip.discord",
				"messages.securitycraft:tip.scserver",
				"messages.securitycraft:tip.outdated"
		};
		//@formatter:on

		return tips[SecurityCraft.RANDOM.nextInt(isOutdated() ? tips.length : tips.length - 1)];
	}

	private static boolean isOutdated() {
		return ForgeVersion.getResult(Loader.instance().activeModContainer()).status == Status.OUTDATED;
	}

	private static Vec3d getCenter(AxisAlignedBB vec) {
		return new Vec3d(vec.minX + (vec.maxX - vec.minX) * 0.5D, vec.minY + (vec.maxY - vec.minY) * 0.5D, vec.minZ + (vec.maxZ - vec.minZ) * 0.5D);
	}
}
