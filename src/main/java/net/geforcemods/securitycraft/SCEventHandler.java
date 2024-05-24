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
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber(modid = SecurityCraft.MODID)
public class SCEventHandler {
	private static final Integer NOTE_DELAY = 9;
	public static final Map<PlayerEntity, MutablePair<Integer, Deque<NoteWrapper>>> PLAYING_TUNES = new HashMap<>();

	private SCEventHandler() {}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			PLAYING_TUNES.forEach((player, pair) -> {
				int ticksRemaining = pair.getLeft();

				if (ticksRemaining == 0) {
					if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.PORTABLE_TUNE_PLAYER.get()).isEmpty()) {
						pair.setLeft(-1);
						return;
					}

					NoteWrapper note = pair.getRight().poll();

					if (note != null) {
						SoundEvent sound = NoteBlockInstrument.valueOf(note.instrumentName.toUpperCase()).getSoundEvent();
						float pitch = (float) Math.pow(2.0D, (note.noteID - 12) / 12.0D);

						player.level.playSound(null, player.blockPosition(), sound, SoundCategory.RECORDS, 3.0F, pitch);
						handlePlayedNote(player.level, player.blockPosition(), note.noteID, note.instrumentName);
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
				Iterator<Entry<PlayerEntity, MutablePair<Integer, Deque<NoteWrapper>>>> entries = PLAYING_TUNES.entrySet().iterator();

				while (entries.hasNext()) {
					if (entries.next().getValue().left == -1)
						entries.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (!ConfigHandler.SERVER.disableThanksMessage.get())
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new SendTip());
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

		if (player.getCamera() instanceof SecurityCamera) {
			SecurityCamera cam = (SecurityCamera) player.getCamera();
			TileEntity be = player.level.getBlockEntity(cam.blockPosition());

			if (player.getEffect(Effects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
				player.removeEffect(Effects.NIGHT_VISION);

			if (be instanceof SecurityCameraBlockEntity)
				((SecurityCameraBlockEntity) be).stopViewing();

			cam.remove();
		}
	}

	@SubscribeEvent
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		PasscodeUtils.startHashingThread(event.getServer());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLevelLoad(WorldEvent.Load event) {
		IWorld level = event.getWorld();

		if (level instanceof ServerWorld && ((ServerWorld) level).dimension() == World.OVERWORLD)
			SaltData.refreshLevel(((ServerWorld) level));
	}

	@SubscribeEvent
	public static void onLevelUnload(WorldEvent.Unload event) {
		IWorld level = event.getWorld();

		if (level instanceof ServerWorld && ((ServerWorld) level).dimension() == World.OVERWORLD)
			SaltData.invalidate();
	}

	@SubscribeEvent
	public static void onServerStop(FMLServerStoppedEvent event) {
		PasscodeUtils.stopHashingThread();
	}

	@SubscribeEvent
	public static void onLivingAttacked(LivingAttackEvent event) {
		LivingEntity entity = event.getEntityLiving();

		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			World level = player.level;
			DamageSource damageSource = event.getSource();

			if (!player.isCreative() && damageSource == DamageSource.IN_WALL && !player.isInvulnerableTo(damageSource) && BlockUtils.isInsideUnownedReinforcedBlocks(level, player, player.getEyePosition(1.0F))) {
				player.hurt(CustomDamageSources.IN_REINFORCED_WALL, 10.0F);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World level = entity.level;

		if (event.getSource() == CustomDamageSources.ELECTRICITY)
			level.playSound(null, entity.blockPosition(), SCSounds.ELECTRIFIED.event, SoundCategory.BLOCKS, 0.25F, 1.0F);

		if (!level.isClientSide && entity instanceof ServerPlayerEntity && PlayerUtils.isPlayerMountedOnCamera(entity)) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;

			((SecurityCamera) player.getCamera()).stopViewing(player);
		}
	}

	@SubscribeEvent
	public static void onDismount(EntityMountEvent event) {
		if (ConfigHandler.SERVER.preventReinforcedFloorGlitching.get() && event.isDismounting() && event.getEntityBeingMounted() instanceof BoatEntity && event.getEntityMounting() instanceof PlayerEntity) {
			BoatEntity boat = (BoatEntity) event.getEntityBeingMounted();
			PlayerEntity player = ((PlayerEntity) event.getEntityMounting());

			if (player.isAlive() && !player.abilities.invulnerable) {
				Vector3d incorrectDismountLocation = new Vector3d(boat.getX(), boat.getBoundingBox().maxY, boat.getZ());
				Vector3d dismountLocation = boat.getDismountLocationForPassenger(player);
				Vector3d newCenterPos = dismountLocation.add(0.0F, player.getBbHeight() / 2, 0.0F);
				Vector3d newEyePos = dismountLocation.add(0.0F, player.getEyeHeight(), 0.0F);

				if (dismountLocation.equals(incorrectDismountLocation) && (BlockUtils.isInsideUnownedReinforcedBlocks(player.level, player, newEyePos) || BlockUtils.isInsideUnownedReinforcedBlocks(player.level, player, newCenterPos)))
					event.setCanceled(true);
			}
		}
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if (!stack.isEmpty() && !(stack.getItem() instanceof BlockItem) && !stack.getItem().is(SCTags.Items.CAN_INTERACT_WITH_DOORS)) {
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

			if (block == SCContent.KEYPAD_DOOR.get())
				event.setUseItem(Result.DENY);
			else if (block == SCContent.REINFORCED_DOOR.get() || block == SCContent.REINFORCED_IRON_TRAPDOOR.get() || block == SCContent.SCANNER_DOOR.get())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		PlayerEntity player = event.getPlayer();

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			event.setCanceled(true);
			return;
		}

		World level = event.getWorld();
		BlockPos pos = event.getPos();
		TileEntity be = level.getBlockEntity(pos);
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (be instanceof ILockable && ((ILockable) be).isLocked() && ((ILockable) be).disableInteractionWhenLocked(level, pos, player) && !player.isShiftKeyDown()) {
			if (event.getHand() == Hand.MAIN_HAND) {
				TranslationTextComponent blockName = Utils.localize(block.getDescriptionId());

				PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
			}

			event.setCanceled(true);
			return;
		}

		if (be instanceof IOwnable) {
			IOwnable ownable = (IOwnable) be;
			Owner owner = ownable.getOwner();

			if (!owner.isValidated()) {
				if (ownable.isOwnedBy(player)) {
					owner.setValidated(true);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block.getDescriptionId()), new TranslationTextComponent("messages.securitycraft:ownable.validate"), TextFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block.getDescriptionId()), new TranslationTextComponent("messages.securitycraft:ownable.ownerNotValidated"), TextFormatting.RED);

				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
				return;
			}
		}

		if (!level.isClientSide) {
			if (event.getItemStack().getItem() == Items.REDSTONE && be instanceof IEMPAffected && ((IEMPAffected) be).isShutDown()) {
				((IEMPAffected) be).reactivate();

				if (!player.isCreative())
					event.getItemStack().shrink(1);

				player.swing(event.getHand());
				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
				return;
			}

			ItemStack heldItem = player.getItemInHand(event.getHand());

			if (heldItem.getItem() == SCContent.KEY_PANEL.get() && (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(player))) {
				for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
					if (pc.isUnprotectedBlock(state)) {
						event.setUseBlock(Result.DENY);
						event.setUseItem(Result.ALLOW);
					}
				}

				return;
			}

			TileEntity te = level.getBlockEntity(pos);

			if (heldItem.getItem() == SCContent.CODEBREAKER.get() && te instanceof ICodebreakable) {
				((ICodebreakable) te).handleCodebreaking(player, event.getHand());
				event.setCanceled(true);
				return;
			}
		}

		if (block instanceof DisplayCaseBlock && event.getEntity().isShiftKeyDown() && player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty()) {
			event.setUseBlock(Result.ALLOW);
			event.setUseItem(Result.DENY);
			return;
		}

		//outside !world.isRemote for properly checking the interaction
		//all the sentry functionality for when the sentry is diguised
		List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(pos));

		if (!sentries.isEmpty())
			event.setCanceled(sentries.get(0).mobInteract(player, event.getHand()) == ActionResultType.SUCCESS); //cancel if an action was taken
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
			return;
		}

		ItemStack stack = event.getPlayer().getMainHandItem();
		Item held = stack.getItem();
		World level = event.getWorld();
		BlockPos pos = event.getPos();

		if (held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()) {
			UniversalBlockReinforcerItem.maybeRemoveMending(stack);

			if (UniversalBlockReinforcerItem.convertBlock(level.getBlockState(pos), level, stack, pos, event.getPlayer()))
				event.setCanceled(true); //When the client knows that a block will be converted on the server, it should not destroy that block (e.g. via instamining)
		}
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event) {
		if (!(event.getWorld() instanceof World))
			return;

		World level = (World) event.getWorld();

		//don't let players in creative mode break the disguise block. it's not possible to break it in other gamemodes
		if (event.getPlayer().isCreative()) {
			List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(event.getPos()));

			if (!sentries.isEmpty()) {
				event.setCanceled(true);
				return;
			}
		}

		if (!level.isClientSide()) {
			BlockPos pos = event.getPos();
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory) {
				IModuleInventory be = (IModuleInventory) te;

				if (be.shouldDropModules()) {
					for (int i = 0; i < be.getMaxNumberOfModules(); i++) {
						if (!be.getInventory().get(i).isEmpty()) {
							ItemStack stack = be.getInventory().get(i);
							ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);

							LevelUtils.addScheduledTask(level, () -> level.addFreshEntity(item));
							be.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

							if (be instanceof LinkableBlockEntity) {
								LinkableBlockEntity lbe = (LinkableBlockEntity) be;

								lbe.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) stack.getItem()).getModuleType(), false), lbe);
							}

							if (be instanceof SecurityCameraBlockEntity) {
								SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) be;
								BlockPos camPos = cam.getBlockPos();
								BlockState camState = cam.getLevel().getBlockState(camPos);

								cam.getLevel().updateNeighborsAt(camPos.relative(camState.getValue(SecurityCameraBlock.FACING), -1), camState.getBlock());
							}
						}
					}
				}
			}

			PlayerEntity player = event.getPlayer();
			BlockState state = event.getState();

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getBlockEntitiesInRange(level, pos).forEach(detector -> detector.log(player, DetectionMode.BREAK, pos, state));
		}
	}

	@SubscribeEvent
	public static void onBlockEventPlace(BlockEvent.EntityPlaceEvent event) {
		if (!(event.getWorld() instanceof World))
			return;

		World level = (World) event.getWorld();

		if (!level.isClientSide && event.getEntity() instanceof PlayerEntity) {
			BlockPos pos = event.getPos();
			BlockState state = event.getState();

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getBlockEntitiesInRange(level, pos).forEach(detector -> detector.log((PlayerEntity) event.getEntity(), DetectionMode.PLACE, pos, state));
		}
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event) {
		TileEntity te = event.getLevel().getBlockEntity(event.getPos());

		if (te instanceof IOwnable) {
			String name = event.getPlayer().getName().getString();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			((IOwnable) te).setOwner(uuid, name);
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
		if (event.getTarget() instanceof Sentry)
			((MobEntity) event.getEntity()).setTarget(null);
	}

	@SubscribeEvent
	public static void onLivingDestroyEvent(LivingDestroyBlockEvent event) {
		event.setCanceled(event.getEntity() instanceof WitherEntity && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getPlayer()) && event.getItemStack().getItem() != SCContent.CAMERA_MONITOR.get())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
		Item item = event.getItemStack().getItem();

		if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ReinforcedCarpetBlock)
			event.setBurnTime(0);
	}

	@SubscribeEvent
	public static void onEntityTeleport(EntityTeleportEvent event) {
		Entity entity = event.getEntity();
		World level = entity.getCommandSenderWorld();
		List<RiftStabilizerBlockEntity> targetPosBlockEntities = BlockEntityTracker.RIFT_STABILIZER.getBlockEntitiesInRange(level, event.getTarget());
		List<RiftStabilizerBlockEntity> sourcePosBlockEntities = BlockEntityTracker.RIFT_STABILIZER.getBlockEntitiesInRange(level, event.getPrev());
		List<RiftStabilizerBlockEntity> blockEntities = new ArrayList<>();
		TeleportationType type = TeleportationType.getTypeFromEvent(event);
		RiftStabilizerBlockEntity riftStabilizer = null;
		boolean targetPosProhibited = false;

		blockEntities.addAll(targetPosBlockEntities);
		blockEntities.addAll(sourcePosBlockEntities);
		blockEntities = blockEntities.stream().distinct().sorted(Comparator.comparingDouble(b -> Math.min(b.getBlockPos().distSqr(event.getTarget(), true), b.getBlockPos().distSqr(event.getPrev(), true)))).collect(Collectors.toList());

		for (RiftStabilizerBlockEntity be : blockEntities) {
			if (!be.isDisabled() && be.getFilter(type) && (!(entity instanceof PlayerEntity) || !(be.isOwnedBy((entity)) && be.ignoresOwner()) && !be.isAllowed(entity))) {
				riftStabilizer = be;
				targetPosProhibited = be.getBlockPos().distSqr(event.getTarget(), true) < be.getBlockPos().distSqr(event.getPrev(), true);
				break;
			}
		}

		if (riftStabilizer != null) {
			BlockPos pos = riftStabilizer.getBlockPos();
			Vector3d centerPos = new AxisAlignedBB(pos).getCenter();
			Vector3d from = targetPosProhibited ? event.getTarget() : event.getPrev();
			Vector3d distance = from.subtract(centerPos);

			if (entity instanceof PlayerEntity) {
				PlayerEntity player = ((PlayerEntity) entity);

				level.playSound(null, event.getPrevX(), event.getPrevY(), event.getPrevZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.5F);
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RIFT_STABILIZER.get().getDescriptionId()), new TranslationTextComponent(targetPosProhibited ? "messages.securitycraft:rift_stabilizer.no_teleport_to" : "messages.securitycraft:rift_stabilizer.no_teleport_from"), TextFormatting.RED);

				if (riftStabilizer.isModuleEnabled(ModuleType.HARMING))
					player.hurt(DamageSource.FALL, 5.0F);
			}

			riftStabilizer.setLastTeleport(Math.max(Math.abs(distance.x), Math.max(Math.abs(distance.y), Math.abs(distance.z))) - 0.5D, type);

			if (riftStabilizer.isModuleEnabled(ModuleType.REDSTONE)) {
				int signalLength = riftStabilizer.getSignalLength();

				level.setBlockAndUpdate(pos, riftStabilizer.getBlockState().cycle(RiftStabilizerBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(level, pos, SCContent.RIFT_STABILIZER.get());

				if (signalLength > 0)
					level.getBlockTicks().scheduleTick(pos, SCContent.RIFT_STABILIZER.get(), signalLength);
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onNoteBlockPlayed(NoteBlockEvent.Play event) {
		handlePlayedNote((World) event.getWorld(), event.getPos(), event.getVanillaNoteId(), event.getInstrument().getSerializedName());
	}

	private static void handlePlayedNote(World level, BlockPos pos, int vanillaNoteId, String instrumentName) {
		List<SonicSecuritySystemBlockEntity> sonicSecuritySystems = BlockEntityTracker.SONIC_SECURITY_SYSTEM.getBlockEntitiesInRange(level, pos);

		for (SonicSecuritySystemBlockEntity te : sonicSecuritySystems) {
			// If the SSS is disabled, don't listen to any notes
			if (!te.isActive())
				continue;

			// If the SSS is recording, record the note being played
			// Otherwise, check to see if the note being played matches the saved combination.
			// If so, toggle its redstone power output on
			if (te.isRecording())
				te.recordNote(vanillaNoteId, instrumentName);
			else
				te.listenToNote(vanillaNoteId, instrumentName);
		}
	}
}
