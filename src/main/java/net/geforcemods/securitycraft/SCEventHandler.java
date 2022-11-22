package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(modid = SecurityCraft.MODID)
public class SCEventHandler {
	private static final Integer NOTE_DELAY = 9;
	public static final Map<Player, MutablePair<Integer, Deque<NoteWrapper>>> PLAYING_TUNES = new HashMap<>();

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			PLAYING_TUNES.forEach((player, pair) -> {
				int ticksRemaining = pair.getLeft();

				if (ticksRemaining == 0) {
					if (PlayerUtils.getSelectedItemStack(player, SCContent.PORTABLE_TUNE_PLAYER.get()).isEmpty()) {
						pair.setLeft(-1);
						return;
					}

					NoteWrapper note = pair.getRight().poll();

					if (note != null) {
						SoundEvent sound = NoteBlockInstrument.valueOf(note.instrumentName().toUpperCase()).getSoundEvent();
						float pitch = (float) Math.pow(2.0D, (note.noteID() - 12) / 12.0D);

						player.level.playSound(null, player.blockPosition(), sound, SoundSource.RECORDS, 3.0F, pitch);
						handlePlayedNote(player.level, player.blockPosition(), note.noteID(), note.instrumentName());
						player.gameEvent(GameEvent.NOTE_BLOCK_PLAY);
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
				Iterator<Entry<Player, MutablePair<Integer, Deque<NoteWrapper>>>> entries = PLAYING_TUNES.entrySet().iterator();

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
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SendTip());
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		ServerPlayer player = (ServerPlayer) event.getEntity();

		if (player.getCamera() instanceof SecurityCamera cam) {
			if (player.level.getBlockEntity(cam.blockPosition()) instanceof SecurityCameraBlockEntity camBe)
				camBe.stopViewing();

			cam.discard();
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		Level level = entity.level;

		if (event.getSource() == CustomDamageSources.ELECTRICITY)
			level.playSound(null, entity.blockPosition(), SCSounds.ELECTRIFIED.event, SoundSource.BLOCKS, 0.25F, 1.0F);

		if (!level.isClientSide && entity instanceof ServerPlayer player && PlayerUtils.isPlayerMountedOnCamera(entity))
			((SecurityCamera) player.getCamera()).stopViewing(player);
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if (!stack.isEmpty() && !stack.is(SCTags.Items.CAN_INTERACT_WITH_DOORS)) {
			Block block = event.getLevel().getBlockState(event.getPos()).getBlock();

			if (block == SCContent.KEYPAD_DOOR.get())
				event.setUseItem(Result.DENY);
			else if (block == SCContent.REINFORCED_DOOR.get() || block == SCContent.REINFORCED_IRON_TRAPDOOR.get() || block == SCContent.SCANNER_DOOR.get())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getEntity())) {
			event.setCanceled(true);
			return;
		}

		Level level = event.getLevel();
		BlockEntity be = level.getBlockEntity(event.getPos());
		BlockState state = level.getBlockState(event.getPos());
		Block block = state.getBlock();

		if (be instanceof ILockable lockable && lockable.isLocked() && lockable.disableInteractionWhenLocked(level, event.getPos(), event.getEntity())) {
			if (!event.getEntity().isShiftKeyDown()) {
				if (event.getHand() == InteractionHand.MAIN_HAND) {
					MutableComponent blockName = Utils.localize(block.getDescriptionId());

					PlayerUtils.sendMessageToPlayer(event.getEntity(), blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), ChatFormatting.DARK_RED, false);
				}

				event.setCanceled(true);
				return;
			}
		}

		if (!level.isClientSide) {
			if (event.getItemStack().getItem() == Items.REDSTONE && be instanceof IEMPAffected empAffected && empAffected.isShutDown()) {
				empAffected.reactivate();

				if (!event.getEntity().isCreative())
					event.getItemStack().shrink(1);

				event.getEntity().swing(event.getHand());
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
				return;
			}

			if (PlayerUtils.isHoldingItem(event.getEntity(), SCContent.KEY_PANEL, event.getHand())) {
				for (IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles()) {
					if (pc.getOriginalBlock() == block) {
						event.setUseBlock(Result.DENY);
						event.setUseItem(Result.ALLOW);
					}
				}

				return;
			}

			if (PlayerUtils.isHoldingItem(event.getEntity(), SCContent.CODEBREAKER, event.getHand()) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}

			if (be instanceof INameSetter nameable && (be instanceof SecurityCameraBlockEntity || be instanceof PortableRadarBlockEntity) && PlayerUtils.isHoldingItem(event.getEntity(), Items.NAME_TAG, event.getHand()) && event.getEntity().getItemInHand(event.getHand()).hasCustomHoverName()) {
				ItemStack nametag = event.getEntity().getItemInHand(event.getHand());

				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);

				if (nameable.getCustomName().equals(nametag.getHoverName())) {
					PlayerUtils.sendMessageToPlayer(event.getEntity(), Component.translatable(be.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.alreadyMatches", nameable.getCustomName()), ChatFormatting.RED);
					return;
				}

				if (!event.getEntity().isCreative())
					nametag.shrink(1);

				nameable.setCustomName(nametag.getHoverName());
				PlayerUtils.sendMessageToPlayer(event.getEntity(), Component.translatable(be.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.named", nameable.getCustomName()), ChatFormatting.RED);
				return;
			}
		}

		//outside !world.isRemote for properly checking the interaction
		//all the sentry functionality for when the sentry is diguised
		List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(event.getPos()));

		if (!sentries.isEmpty())
			event.setCanceled(sentries.get(0).mobInteract(event.getEntity(), event.getHand()) == InteractionResult.SUCCESS); //cancel if an action was taken
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getEntity())) {
			event.setCanceled(true);
			return;
		}

		ItemStack stack = event.getEntity().getMainHandItem();
		Item held = stack.getItem();
		Level level = event.getLevel();
		BlockPos pos = event.getPos();

		if (held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
			UniversalBlockReinforcerItem.convertBlock(level.getBlockState(pos), level, stack, pos, event.getEntity());
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event) {
		if (!(event.getLevel() instanceof Level level))
			return;

		//don't let players in creative mode break the disguise block. it's not possible to break it in other gamemodes
		if (event.getPlayer().isCreative()) {
			List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(event.getPos()));

			if (!sentries.isEmpty()) {
				event.setCanceled(true);
				return;
			}
		}

		if (!level.isClientSide()) {
			BlockPos pos = event.getPos();

			if (level.getBlockEntity(pos) instanceof IModuleInventory be) {
				for (int i = 0; i < be.getMaxNumberOfModules(); i++) {
					if (!be.getInventory().get(i).isEmpty()) {
						ItemStack stack = be.getInventory().get(i);
						ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);

						LevelUtils.addScheduledTask(level, () -> level.addFreshEntity(item));
						be.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

						if (be instanceof LinkableBlockEntity lbe)
							lbe.createLinkedBlockAction(new ILinkedAction.ModuleRemoved(((ModuleItem) stack.getItem()).getModuleType(), false), lbe);

						if (be instanceof SecurityCameraBlockEntity cam) {
							BlockPos camPos = cam.getBlockPos();
							BlockState camState = level.getBlockState(camPos);

							level.updateNeighborsAt(camPos.relative(camState.getValue(SecurityCameraBlock.FACING), -1), camState.getBlock());
						}
					}
				}
			}

			Player player = event.getPlayer();
			BlockState state = event.getState();

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getBlockEntitiesInRange(level, pos).forEach(detector -> detector.log(player, DetectionMode.BREAK, pos, state));
		}
	}

	@SubscribeEvent
	public static void onBlockEventPlace(BlockEvent.EntityPlaceEvent event) {
		if (!(event.getLevel() instanceof Level level) || level.isClientSide())
			return;

		if (event.getEntity() instanceof Player player) {
			BlockPos pos = event.getPos();
			BlockState state = event.getState();

			BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getBlockEntitiesInRange(level, pos).forEach(detector -> detector.log(player, DetectionMode.PLACE, pos, state));
		}
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event) {
		if (event.getLevel().getBlockEntity(event.getPos()) instanceof IOwnable ownable) {
			String name = event.getPlayer().getName().getString();
			String uuid = event.getPlayer().getGameProfile().getId().toString();

			ownable.setOwner(uuid, name);
		}
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingChangeTargetEvent event) {
		if (event.getNewTarget() instanceof Sentry)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onBreakSpeed(BreakSpeed event) {
		if (event.getEntity() != null) {
			Item held = event.getEntity().getMainHandItem().getItem();

			if (held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()) {
				Block block = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(event.getState().getBlock());

				if (block != null)
					event.setNewSpeed(10000.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDestroyEvent(LivingDestroyBlockEvent event) {
		event.setCanceled(event.getEntity() instanceof WitherBoss && event.getState().getBlock() instanceof IReinforcedBlock);
	}

	@SubscribeEvent
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getEntity()) && event.getItemStack().getItem() != SCContent.CAMERA_MONITOR.get())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
		if (event.getItemStack().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ReinforcedCarpetBlock)
			event.setBurnTime(0);
	}

	@SubscribeEvent
	public static void onEntityTeleport(EntityTeleportEvent event) {
		Entity entity = event.getEntity();
		Level level = entity.getLevel();
		List<RiftStabilizerBlockEntity> targetPosBlockEntities = BlockEntityTracker.RIFT_STABILIZER.getBlockEntitiesInRange(level, event.getTarget());
		List<RiftStabilizerBlockEntity> sourcePosBlockEntities = BlockEntityTracker.RIFT_STABILIZER.getBlockEntitiesInRange(level, event.getPrev());
		List<RiftStabilizerBlockEntity> blockEntities = new ArrayList<>();
		TeleportationType type = TeleportationType.getTypeFromEvent(event);
		RiftStabilizerBlockEntity riftStabilizer = null;
		boolean targetPosProhibited = false;

		blockEntities.addAll(targetPosBlockEntities);
		blockEntities.addAll(sourcePosBlockEntities);
		blockEntities = blockEntities.stream().distinct().sorted(Comparator.comparingDouble(b -> Math.min(b.getBlockPos().distToCenterSqr(event.getTarget()), b.getBlockPos().distToCenterSqr(event.getPrev())))).toList();

		for (RiftStabilizerBlockEntity be : blockEntities) {
			if (!be.isDisabled() && be.getFilter(type) && (!(entity instanceof Player player) || !be.getOwner().isOwner(player) && !ModuleUtils.isAllowed(be, player))) {
				riftStabilizer = be;
				targetPosProhibited = be.getBlockPos().distToCenterSqr(event.getTarget()) < be.getBlockPos().distToCenterSqr(event.getPrev());
				break;
			}
		}

		if (riftStabilizer != null) {
			BlockPos pos = riftStabilizer.getBlockPos();
			Vec3 centerPos = new AABB(pos).getCenter();
			Vec3 from = targetPosProhibited ? event.getTarget() : event.getPrev();
			Vec3 distance = from.subtract(centerPos);

			if (entity instanceof Player player) {
				level.playSound(null, event.getPrevX(), event.getPrevY(), event.getPrevZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.5F);
				PlayerUtils.sendMessageToPlayer(player, SCContent.RIFT_STABILIZER.get().getName(), Component.translatable(targetPosProhibited ? "messages.securitycraft:rift_stabilizer.no_teleport_to" : "messages.securitycraft:rift_stabilizer.no_teleport_from"), ChatFormatting.RED);

				if (riftStabilizer.isModuleEnabled(ModuleType.HARMING))
					player.hurt(DamageSource.FALL, 5.0F);
			}

			riftStabilizer.setLastTeleport(Math.max(Math.abs(distance.x), Math.max(Math.abs(distance.y), Math.abs(distance.z))) - 0.5D, type);

			if (riftStabilizer.isModuleEnabled(ModuleType.REDSTONE)) {
				level.setBlockAndUpdate(pos, riftStabilizer.getBlockState().setValue(RiftStabilizerBlock.POWERED, true));
				BlockUtils.updateIndirectNeighbors(level, pos, SCContent.RIFT_STABILIZER.get());
				level.scheduleTick(pos, SCContent.RIFT_STABILIZER.get(), riftStabilizer.getSignalLength());
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onNoteBlockPlayed(NoteBlockEvent.Play event) {
		handlePlayedNote((Level) event.getLevel(), event.getPos(), event.getVanillaNoteId(), event.getInstrument().getSerializedName());
	}

	private static void handlePlayedNote(Level level, BlockPos pos, int vanillaNoteId, String instrumentName) {
		List<SonicSecuritySystemBlockEntity> sonicSecuritySystems = BlockEntityTracker.SONIC_SECURITY_SYSTEM.getBlockEntitiesInRange(level, pos);

		for (SonicSecuritySystemBlockEntity be : sonicSecuritySystems) {
			// If the SSS is disabled, don't listen to any notes
			if (!be.isActive())
				continue;

			// If the SSS is recording, record the note being played
			// Otherwise, check to see if the note being played matches the saved combination.
			// If so, toggle its redstone power output on
			if (be.isRecording())
				be.recordNote(vanillaNoteId, instrumentName);
			else if (be.listenToNote(vanillaNoteId, instrumentName)) {
				be.correctTuneWasPlayed = true;
				be.powerCooldown = be.signalLength.get();

				if (be.isModuleEnabled(ModuleType.REDSTONE)) {
					level.setBlockAndUpdate(be.getBlockPos(), be.getLevel().getBlockState(be.getBlockPos()).setValue(SonicSecuritySystemBlock.POWERED, true));
					BlockUtils.updateIndirectNeighbors(be.getLevel(), be.getBlockPos(), SCContent.SONIC_SECURITY_SYSTEM.get(), Direction.DOWN);
				}
			}
		}
	}

	private static boolean handleCodebreaking(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getEntity().level;

		if (level.getBlockEntity(event.getPos()) instanceof ICodebreakable codebreakable) {
			double chance = ConfigHandler.SERVER.codebreakerChance.get();

			if (chance < 0.0D) {
				Block block = level.getBlockState(event.getPos()).getBlock();

				PlayerUtils.sendMessageToPlayer(event.getEntity(), Utils.localize(block.getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
			}
			else {
				if (event.getEntity().getItemInHand(event.getHand()).getItem() == SCContent.CODEBREAKER.get())
					event.getEntity().getItemInHand(event.getHand()).hurtAndBreak(1, event.getEntity(), p -> p.broadcastBreakEvent(event.getHand()));

				if (event.getEntity().isCreative() || new Random().nextDouble() < chance)
					return codebreakable.onCodebreakerUsed(level.getBlockState(event.getPos()), event.getEntity());
				else {
					PlayerUtils.sendMessageToPlayer(event.getEntity(), Component.translatable(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
					return true;
				}
			}
		}

		return false;
	}
}
