package net.geforcemods.securitycraft.blockentities;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class RetinalScannerBlockEntity extends DisguisableBlockEntity implements IViewActivated, ITickingBlockEntity, ILockable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new SignalLengthOption(60);
	private DoubleOption maximumDistance = new DoubleOption("maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D, true) {
		@Override
		public String getKey(Block block) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private DisabledOption disabled = new DisabledOption(false);
	private ResolvableProfile ownerProfile;
	private int viewCooldown = 0;

	public RetinalScannerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.RETINAL_SCANNER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		checkView(level, pos);
	}

	@Override
	public boolean onEntityViewed(LivingEntity entity, BlockHitResult hitResult) {
		if (!isLocked() && !isDisabled()) {
			BlockState state = getBlockState();

			if (state.getValue(RetinalScannerBlock.FACING) != hitResult.getDirection())
				return false;

			int signalLength = getSignalLength();

			if ((!state.getValue(RetinalScannerBlock.POWERED) || signalLength == 0) && !Utils.isEntityInvisible(entity)) {
				if (entity instanceof Player player) {
					Owner viewingPlayer = new Owner(player);

					if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.PLAYER_HEAD)
						viewingPlayer = PlayerUtils.getSkullOwner(player);

					if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);
						return true;
					}

					if (sendMessage.get())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), ChatFormatting.GREEN);
				}
				else if (activatedOnlyByPlayer())
					return false;

				level.setBlockAndUpdate(worldPosition, state.cycle(RetinalScannerBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.RETINAL_SCANNER.get());

				if (signalLength > 0)
					level.scheduleTick(new BlockPos(worldPosition), SCContent.RETINAL_SCANNER.get(), signalLength);

				return true;
			}
		}
		else if (entity instanceof Player player) {
			if (isLocked() && sendMessage.get()) {
				MutableComponent blockName = Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId());

				PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), ChatFormatting.DARK_RED, false);
			}
			else if (isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);

			return true;
		}

		return false;
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals(signalLength.getName())) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(RetinalScannerBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}

		super.onOptionChanged(option);
	}

	@Override
	public int getDefaultViewCooldown() {
		return getSignalLength() + 30;
	}

	@Override
	public int getViewCooldown() {
		return viewCooldown;
	}

	@Override
	public void setViewCooldown(int viewCooldown) {
		this.viewCooldown = viewCooldown;
		setChanged();
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public double getMaximumDistance() {
		return maximumDistance.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				activatedByEntities, sendMessage, signalLength, disabled, maximumDistance
		};
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		if (!StringUtil.isNullOrEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner")) && ownerProfile != null)
			tag.put("ownerProfile", ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, ownerProfile).getOrThrow());
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		if (tag.contains("ownerProfile"))
			ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag.get("ownerProfile")).resultOrPartial(name -> LOGGER.error("Failed to load profile from player head: {}", name)).ifPresent(this::setOwnerProfile);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level world, BlockPos pos, Player player) {
		setOwnerProfile(new ResolvableProfile(new GameProfile(Util.NIL_UUID, getOwner().getName())));
		super.onOwnerChanged(state, world, pos, player);
	}

	public void setOwnerProfile(ResolvableProfile ownerProfile) {
		this.ownerProfile = ownerProfile;
	}

	@Nullable
	public ResolvableProfile getPlayerProfile() {
		return ownerProfile;
	}
}
