package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KeycardReaderBlockEntity extends DisguisableBlockEntity implements MenuProvider, ILockable, ICodebreakable {
	private boolean[] acceptedLevels = {
			true, false, false, false, false
	};
	private int signature = 0;
	protected BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	protected IntOption signalLength = new SignalLengthOption(60);
	protected DisabledOption disabled = new DisabledOption(false);

	public KeycardReaderBlockEntity(BlockPos pos, BlockState state) {
		this(SCContent.KEYCARD_READER_BLOCK_ENTITY.get(), pos, state);
	}

	public KeycardReaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		CompoundTag acceptedLevelsTag = new CompoundTag();

		for (int i = 1; i <= 5; i++) {
			acceptedLevelsTag.putBoolean("lvl" + i, acceptedLevels[i - 1]);
		}

		tag.put("acceptedLevels", acceptedLevelsTag);
		tag.putInt("signature", signature);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		//carry over old data
		if (tag.contains("passLV")) {
			boolean oldRequiresExactKeycard = false;
			int oldPassLV = tag.getInt("passLV") - 1; //old data was 1-indexed, new one is 0-indexed

			if (tag.contains("requiresExactKeycard"))
				oldRequiresExactKeycard = tag.getBoolean("requiresExactKeycard");

			for (int i = 0; i < 5; i++) {
				acceptedLevels[i] = oldRequiresExactKeycard ? i == oldPassLV : i >= oldPassLV;
			}
		}

		//don't try to load this data if it doesn't exist, otherwise everything will be "false"
		if (tag.contains("acceptedLevels", Tag.TAG_COMPOUND)) {
			CompoundTag acceptedLevelsTag = tag.getCompound("acceptedLevels");

			for (int i = 1; i <= 5; i++) {
				acceptedLevels[i - 1] = acceptedLevelsTag.getBoolean("lvl" + i);
			}
		}

		signature = tag.getInt("signature");

		if (tag.contains("sendMessage"))
			sendDenylistMessage.setValue(tag.getBoolean("sendMessage"));
	}

	@Override
	public boolean shouldAttemptCodebreak(Player player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !getBlockState().getValue(BlockStateProperties.POWERED);
	}

	@Override
	public void useCodebreaker(Player player) {
		if (!level.isClientSide)
			activate();
	}

	public InteractionResult onRightClickWithActionItem(ItemStack stack, InteractionHand hand, Player player, boolean isCodebreaker, boolean isKeycardHolder) {
		if (isCodebreaker) {
			double chance = ConfigHandler.SERVER.codebreakerChance.get();

			if (chance < 0.0D)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
			else {
				if (isOwnedBy(player) || CodebreakerItem.wasRecentlyUsed(stack))
					return InteractionResult.PASS;

				boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
				CompoundTag tag = stack.getOrCreateTag();

				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				tag.putLong(CodebreakerItem.LAST_USED_TIME, System.currentTimeMillis());
				tag.putBoolean(CodebreakerItem.WAS_SUCCESSFUL, isSuccessful);

				if (isSuccessful)
					activate();
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
			}
		}
		else {
			if (isKeycardHolder) {
				ItemContainer holderInventory = ItemContainer.keycardHolder(stack);
				MutableComponent feedback = null;

				for (int i = 0; i < holderInventory.getContainerSize(); i++) {
					ItemStack keycardStack = holderInventory.getItem(i);

					if (keycardStack.getItem() instanceof KeycardItem && keycardStack.hasTag()) {
						feedback = insertCard(keycardStack, player);

						if (feedback == null)
							return InteractionResult.SUCCESS;
					}
				}

				if (feedback == null)
					PlayerUtils.sendMessageToPlayer(player, Component.translatable(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:keycard_holder.no_keycards"), ChatFormatting.RED);
				else
					PlayerUtils.sendMessageToPlayer(player, Component.translatable(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:keycard_holder.fail"), ChatFormatting.RED);
			}
			else {
				MutableComponent feedback = insertCard(stack, player);

				if (feedback != null)
					PlayerUtils.sendMessageToPlayer(player, Component.translatable(getBlockState().getBlock().getDescriptionId()), feedback, ChatFormatting.RED);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public MutableComponent insertCard(ItemStack stack, Player player) {
		CompoundTag tag = stack.getTag();
		Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

		//owner of this keycard reader and the keycard reader the keycard got linked to do not match
		if ((ConfigHandler.SERVER.enableTeamOwnership.get() && !TeamUtils.areOnSameTeam(getOwner(), keycardOwner)) || !getOwner().getUUID().equals(keycardOwner.getUUID()))
			return Component.translatable("messages.securitycraft:keycardReader.differentOwner");

		//the keycard's signature does not match this keycard reader's
		if (getSignature() != tag.getInt("signature"))
			return Component.translatable("messages.securitycraft:keycardReader.wrongSignature");

		int keycardLevel = ((KeycardItem) stack.getItem()).getLevel();

		//the keycard's level
		if (!getAcceptedLevels()[keycardLevel]) //both are 0 indexed, so it's ok
			return Component.translatable("messages.securitycraft:keycardReader.wrongLevel", keycardLevel + 1); //level is 0-indexed, so it has to be increased by one to match with the item name

		//don't consider the block powered if the signal length is 0, because players need to be able to toggle it off
		boolean powered = level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED) && getSignalLength() > 0;

		if (!powered) {
			if (tag.getBoolean("limited")) {
				int uses = tag.getInt("uses");

				if (uses <= 0)
					return Component.translatable("messages.securitycraft:keycardReader.noUses");

				if (!player.isCreative())
					tag.putInt("uses", --uses);
			}

			activate();
		}

		return null;
	}

	public void activate() {
		Block block = getBlockState().getBlock();
		int signalLength = getSignalLength();

		level.setBlockAndUpdate(worldPosition, getBlockState().cycle(BlockStateProperties.POWERED));
		BlockUtils.updateIndirectNeighbors(level, worldPosition, block);

		if (signalLength > 0)
			level.scheduleTick(worldPosition, block, signalLength);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
		}

		super.onOptionChanged(option);
	}

	public void setAcceptedLevels(boolean[] acceptedLevels) {
		this.acceptedLevels = acceptedLevels;
		setChanged();
	}

	public boolean[] getAcceptedLevels() {
		return acceptedLevels;
	}

	public void setSignature(int signature) {
		this.signature = signature;
		setChanged();
	}

	public int getSignature() {
		return signature;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendDenylistMessage, signalLength, disabled
		};
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new KeycardReaderMenu(windowId, inv, level, worldPosition);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}
}
