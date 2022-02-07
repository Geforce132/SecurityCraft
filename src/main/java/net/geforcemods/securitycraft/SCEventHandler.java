package net.geforcemods.securitycraft;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;

import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.SonicSecuritySystemTracker;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
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
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber(modid = SecurityCraft.MODID)
public class SCEventHandler {
	public static final Integer NOTE_DELAY = 10;
	public static final Map<PlayerEntity, MutablePair<Integer, Deque<NoteWrapper>>> PLAYING_TUNES = new HashMap<>();

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
			TileEntity tile = player.level.getBlockEntity(cam.blockPosition());

			if (tile instanceof SecurityCameraBlockEntity)
				((SecurityCameraBlockEntity) tile).stopViewing();

			cam.remove();
		}
	}

	@SubscribeEvent
	public static void onDamageTaken(LivingHurtEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.level;

		if (event.getSource() == CustomDamageSources.ELECTRICITY)
			world.playSound(null, entity.blockPosition(), SCSounds.ELECTRIFIED.event, SoundCategory.BLOCKS, 0.25F, 1.0F);

		if (!world.isClientSide && entity instanceof ServerPlayerEntity && PlayerUtils.isPlayerMountedOnCamera(entity)) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;

			((SecurityCamera) player.getCamera()).stopViewing(player);
		}
	}

	//disallow rightclicking doors, fixes wrenches from other mods being able to switch their state
	//side effect for keypad door: it is now only openable with an empty hand
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void highestPriorityOnRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if (!stack.isEmpty() && item != SCContent.UNIVERSAL_BLOCK_REMOVER.get() && item != SCContent.UNIVERSAL_BLOCK_MODIFIER.get() && item != SCContent.UNIVERSAL_OWNER_CHANGER.get() && item != SCContent.SONIC_SECURITY_SYSTEM_ITEM.get()) {
			if (!(item instanceof BlockItem)) {
				Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
				boolean isKeypadDoor = block == SCContent.KEYPAD_DOOR.get();

				if (item == SCContent.CODEBREAKER.get() && isKeypadDoor)
					return;

				if (isKeypadDoor || block == SCContent.REINFORCED_DOOR.get() || block == SCContent.REINFORCED_IRON_TRAPDOOR.get() || block == SCContent.SCANNER_DOOR.get())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
			return;
		}

		World world = event.getWorld();
		TileEntity te = world.getBlockEntity(event.getPos());
		BlockState state = world.getBlockState(event.getPos());
		Block block = state.getBlock();

		if (te instanceof ILockable && ((ILockable) te).isLocked() && ((ILockable) te).disableInteractionWhenLocked(world, event.getPos(), event.getPlayer())) {
			if (event.getHand() == Hand.MAIN_HAND) {
				TranslationTextComponent blockName = Utils.localize(block.getDescriptionId());

				PlayerUtils.sendMessageToPlayer(event.getPlayer(), blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
			}

			event.setCanceled(true);
			return;
		}

		if (!world.isClientSide) {
			if (PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.KEY_PANEL, event.getHand())) {
				for (IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles()) {
					if (pc.getOriginalBlock() == block) {
						event.setUseBlock(Result.DENY);
						event.setUseItem(Result.ALLOW);
					}
				}

				return;
			}

			if (PlayerUtils.isHoldingItem(event.getPlayer(), SCContent.CODEBREAKER, event.getHand()) && handleCodebreaking(event)) {
				event.setCanceled(true);
				return;
			}

			if (te instanceof INameSetter && (te instanceof SecurityCameraBlockEntity || te instanceof PortableRadarBlockEntity) && PlayerUtils.isHoldingItem(event.getPlayer(), Items.NAME_TAG, event.getHand()) && event.getPlayer().getItemInHand(event.getHand()).hasCustomHoverName()) {
				ItemStack nametag = event.getPlayer().getItemInHand(event.getHand());
				INameSetter nameable = (INameSetter) te;

				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);

				if (nameable.getCustomName().equals(nametag.getHoverName())) {
					PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslationTextComponent(te.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.alreadyMatches", nameable.getCustomName()), TextFormatting.RED);
					return;
				}

				if (!event.getPlayer().isCreative())
					nametag.shrink(1);

				nameable.setCustomName(nametag.getHoverName());
				PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslationTextComponent(te.getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:naming.named", nameable.getCustomName()), TextFormatting.RED);
				return;
			}
		}

		//outside !world.isRemote for properly checking the interaction
		//all the sentry functionality for when the sentry is diguised
		List<Sentry> sentries = world.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(event.getPos()));

		if (!sentries.isEmpty())
			event.setCanceled(sentries.get(0).mobInteract(event.getPlayer(), event.getHand()) == ActionResultType.SUCCESS); //cancel if an action was taken
	}

	@SubscribeEvent
	public static void onLeftClickBlock(LeftClickBlock event) {
		if (PlayerUtils.isPlayerMountedOnCamera(event.getPlayer())) {
			event.setCanceled(true);
			return;
		}

		ItemStack stack = event.getPlayer().getMainHandItem();
		Item held = stack.getItem();
		World world = event.getWorld();
		BlockPos pos = event.getPos();

		if (held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get() || held == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
			UniversalBlockReinforcerItem.convertBlock(world.getBlockState(pos), world, stack, pos, event.getPlayer());
	}

	@SubscribeEvent
	public static void onBlockEventBreak(BlockEvent.BreakEvent event) {
		if (!(event.getWorld() instanceof World))
			return;

		if (!event.getWorld().isClientSide()) {
			if (event.getWorld().getBlockEntity(event.getPos()) instanceof IModuleInventory) {
				IModuleInventory te = (IModuleInventory) event.getWorld().getBlockEntity(event.getPos());

				for (int i = 0; i < te.getMaxNumberOfModules(); i++)
					if (!te.getInventory().get(i).isEmpty()) {
						ItemStack stack = te.getInventory().get(i);
						ItemEntity item = new ItemEntity((World) event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
						LevelUtils.addScheduledTask(event.getWorld(), () -> event.getWorld().addFreshEntity(item));

						te.onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType());

						if (te instanceof LinkableBlockEntity) {
							((LinkableBlockEntity) te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[] {
									stack, ((ModuleItem) stack.getItem()).getModuleType()
							}, (LinkableBlockEntity) te);
						}

						if (te instanceof SecurityCameraBlockEntity) {
							SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) te;

							cam.getLevel().updateNeighborsAt(cam.getBlockPos().relative(cam.getLevel().getBlockState(cam.getBlockPos()).getValue(SecurityCameraBlock.FACING), -1), cam.getLevel().getBlockState(cam.getBlockPos()).getBlock());
						}
					}
			}
		}

		List<Sentry> sentries = ((World) event.getWorld()).getEntitiesOfClass(Sentry.class, new AxisAlignedBB(event.getPos()));

		//don't let people break the disguise block
		if (!sentries.isEmpty() && !sentries.get(0).getDisguiseModule().isEmpty()) {
			ItemStack disguiseModule = sentries.get(0).getDisguiseModule();
			Block block = ((ModuleItem) disguiseModule.getItem()).getBlockAddon(disguiseModule.getTag());

			if (block == event.getWorld().getBlockState(event.getPos()).getBlock())
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onOwnership(OwnershipEvent event) {
		TileEntity te = event.getWorld().getBlockEntity(event.getPos());

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
	public static void onNoteBlockPlayed(NoteBlockEvent.Play event) {
		handlePlayedNote((World) event.getWorld(), event.getPos(), event.getVanillaNoteId(), event.getInstrument().getSerializedName());
	}

	private static void handlePlayedNote(World world, BlockPos pos, int vanillaNoteId, String instrumentName) {
		List<SonicSecuritySystemBlockEntity> sonicSecuritySystems = SonicSecuritySystemTracker.getSonicSecuritySystemsInRange(world, pos);

		for (SonicSecuritySystemBlockEntity te : sonicSecuritySystems) {
			// If the SSS is disabled, don't listen to any notes
			if (!te.isActive())
				continue;

			// If the SSS is recording, record the note being played
			if (te.isRecording())
				te.recordNote(vanillaNoteId, instrumentName);
			// If the SSS is active, check to see if the note being played matches the saved combination.
			// If so, toggle its redstone power output on
			else if (te.listenToNote(vanillaNoteId, instrumentName)) {
				te.correctTuneWasPlayed = true;
				te.powerCooldown = te.signalLength.get();

				if (te.hasModule(ModuleType.REDSTONE)) {
					world.setBlockAndUpdate(te.getBlockPos(), te.getLevel().getBlockState(te.getBlockPos()).setValue(SonicSecuritySystemBlock.POWERED, true));
					BlockUtils.updateIndirectNeighbors(te.getLevel(), te.getBlockPos(), SCContent.SONIC_SECURITY_SYSTEM.get(), Direction.DOWN);
				}
			}
		}
	}

	private static boolean handleCodebreaking(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getPlayer().level;
		TileEntity tileEntity = world.getBlockEntity(event.getPos());

		if (tileEntity instanceof IPasswordProtected && ((IPasswordProtected) tileEntity).isCodebreakable()) {
			if (ConfigHandler.SERVER.allowCodebreakerItem.get()) {
				if (event.getPlayer().getItemInHand(event.getHand()).getItem() == SCContent.CODEBREAKER.get())
					event.getPlayer().getItemInHand(event.getHand()).hurtAndBreak(1, event.getPlayer(), p -> p.broadcastBreakEvent(event.getHand()));

				if (event.getPlayer().isCreative() || new Random().nextInt(3) == 1)
					return ((IPasswordProtected) tileEntity).onCodebreakerUsed(world.getBlockState(event.getPos()), event.getPlayer());
				else {
					PlayerUtils.sendMessageToPlayer(event.getPlayer(), new TranslationTextComponent(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
					return true;
				}
			}
			else {
				Block block = world.getBlockState(event.getPos()).getBlock();

				PlayerUtils.sendMessageToPlayer(event.getPlayer(), Utils.localize(block.getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
			}
		}

		return false;
	}
}
