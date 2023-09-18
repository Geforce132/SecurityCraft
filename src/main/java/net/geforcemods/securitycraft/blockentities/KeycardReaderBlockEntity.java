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
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;

public class KeycardReaderBlockEntity extends DisguisableBlockEntity implements INamedContainerProvider, ILockable, ICodebreakable {
	private boolean[] acceptedLevels = {
			true, false, false, false, false
	};
	private int signature = 0;
	protected BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	protected IntOption signalLength = new SignalLengthOption(this::getBlockPos, 60);
	protected DisabledOption disabled = new DisabledOption(false);

	public KeycardReaderBlockEntity() {
		this(SCContent.KEYCARD_READER_BLOCK_ENTITY.get());
	}

	public KeycardReaderBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		CompoundNBT acceptedLevelsTag = new CompoundNBT();

		for (int i = 1; i <= 5; i++) {
			acceptedLevelsTag.putBoolean("lvl" + i, acceptedLevels[i - 1]);
		}

		tag.put("acceptedLevels", acceptedLevelsTag);
		tag.putInt("signature", signature);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

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
		if (tag.contains("acceptedLevels", NBT.TAG_COMPOUND)) {
			CompoundNBT acceptedLevelsTag = tag.getCompound("acceptedLevels");

			for (int i = 1; i <= 5; i++) {
				acceptedLevels[i - 1] = acceptedLevelsTag.getBoolean("lvl" + i);
			}
		}

		signature = tag.getInt("signature");
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, PlayerEntity player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !state.getValue(BlockStateProperties.POWERED);
	}

	@Override
	public void useCodebreaker(BlockState state, PlayerEntity player) {
		if (!level.isClientSide)
			activate();
	}

	public ActionResultType onRightClickWithActionItem(ItemStack stack, Hand hand, PlayerEntity player, boolean isCodebreaker, boolean isKeycardHolder) {
		if (isCodebreaker) {
			double chance = ConfigHandler.SERVER.codebreakerChance.get();

			if (chance < 0.0D)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
			else {
				if (isOwnedBy(player) || CodebreakerItem.wasRecentlyUsed(stack))
					return ActionResultType.PASS;

				boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
				CompoundNBT tag = stack.getOrCreateTag();

				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				tag.putLong(CodebreakerItem.LAST_USED_TIME, System.currentTimeMillis());
				tag.putBoolean(CodebreakerItem.WAS_SUCCESSFUL, isSuccessful);

				if (isSuccessful)
					activate();
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
			}
		}
		else {
			if (isKeycardHolder) {
				ItemContainer holderInventory = ItemContainer.keycardHolder(stack);
				IFormattableTextComponent feedback = null;

				for (int i = 0; i < holderInventory.getContainerSize(); i++) {
					ItemStack keycardStack = holderInventory.getItem(i);

					if (keycardStack.getItem() instanceof KeycardItem && keycardStack.hasTag()) {
						feedback = insertCard(keycardStack, player);

						if (feedback == null)
							return ActionResultType.SUCCESS;
					}
				}

				if (feedback == null)
					PlayerUtils.sendMessageToPlayer(player, new TranslationTextComponent(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:keycard_holder.no_keycards"), TextFormatting.RED);
				else
					PlayerUtils.sendMessageToPlayer(player, new TranslationTextComponent(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:keycard_holder.fail"), TextFormatting.RED);
			}
			else {
				IFormattableTextComponent feedback = insertCard(stack, player);

				if (feedback != null)
					PlayerUtils.sendMessageToPlayer(player, new TranslationTextComponent(getBlockState().getBlock().getDescriptionId()), feedback, TextFormatting.RED);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public IFormattableTextComponent insertCard(ItemStack stack, PlayerEntity player) {
		CompoundNBT tag = stack.getTag();
		Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

		//owner of this keycard reader and the keycard reader the keycard got linked to do not match
		if ((ConfigHandler.SERVER.enableTeamOwnership.get() && !PlayerUtils.areOnSameTeam(getOwner(), keycardOwner)) || !getOwner().getUUID().equals(keycardOwner.getUUID()))
			return new TranslationTextComponent("messages.securitycraft:keycardReader.differentOwner");

		//the keycard's signature does not match this keycard reader's
		if (getSignature() != tag.getInt("signature"))
			return new TranslationTextComponent("messages.securitycraft:keycardReader.wrongSignature");

		int keycardLevel = ((KeycardItem) stack.getItem()).getLevel();

		//the keycard's level
		if (!getAcceptedLevels()[keycardLevel]) //both are 0 indexed, so it's ok
			return new TranslationTextComponent("messages.securitycraft:keycardReader.wrongLevel", keycardLevel + 1); //level is 0-indexed, so it has to be increased by one to match with the item name

		boolean powered = level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED);

		if (tag.getBoolean("limited")) {
			int uses = tag.getInt("uses");

			if (uses <= 0)
				return new TranslationTextComponent("messages.securitycraft:keycardReader.noUses");

			if (!player.isCreative() && !powered)
				tag.putInt("uses", --uses);
		}

		if (!powered)
			activate();

		return null;
	}

	public void activate() {
		Block block = getBlockState().getBlock();

		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, true));
		BlockUtils.updateIndirectNeighbors(level, worldPosition, block);
		level.getBlockTicks().scheduleTick(worldPosition, block, getSignalLength());
	}

	public void setAcceptedLevels(boolean[] acceptedLevels) {
		this.acceptedLevels = acceptedLevels;
	}

	public boolean[] getAcceptedLevels() {
		return acceptedLevels;
	}

	public void setSignature(int signature) {
		this.signature = signature;
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
				sendMessage, signalLength, disabled
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new KeycardReaderMenu(windowId, inv, level, worldPosition);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}
}
