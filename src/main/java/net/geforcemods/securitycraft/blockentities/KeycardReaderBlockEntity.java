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
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants.NBT;

public class KeycardReaderBlockEntity extends DisguisableBlockEntity implements ILockable, ICodebreakable {
	private boolean[] acceptedLevels = {
			true, false, false, false, false
	};
	private int signature = 0;
	protected BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	protected IntOption signalLength = new SignalLengthOption(this::getPos, 60);
	protected DisabledOption disabled = new DisabledOption(false);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound acceptedLevelsTag = new NBTTagCompound();

		for (int i = 1; i <= 5; i++) {
			acceptedLevelsTag.setBoolean("lvl" + i, acceptedLevels[i - 1]);
		}

		tag.setTag("acceptedLevels", acceptedLevelsTag);
		tag.setInteger("signature", signature);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		//carry over old data
		if (tag.hasKey("passLV")) {
			boolean oldRequiresExactKeycard = false;
			int oldPassLV = tag.getInteger("passLV") - 1; //old data was 1-indexed, new one is 0-indexed

			if (tag.hasKey("requiresExactKeycard"))
				oldRequiresExactKeycard = tag.getBoolean("requiresExactKeycard");

			for (int i = 0; i < 5; i++) {
				acceptedLevels[i] = oldRequiresExactKeycard ? i == oldPassLV : i >= oldPassLV;
			}
		}

		//don't try to load this data if it doesn't exist, otherwise everything will be "false"
		if (tag.hasKey("acceptedLevels", NBT.TAG_COMPOUND)) {
			NBTTagCompound acceptedLevelsTag = tag.getCompoundTag("acceptedLevels");

			for (int i = 1; i <= 5; i++) {
				acceptedLevels[i - 1] = acceptedLevelsTag.getBoolean("lvl" + i);
			}
		}

		signature = tag.getInteger("signature");

		if (tag.hasKey("sendMessage"))
			sendDenylistMessage.setValue(tag.getBoolean("sendMessage"));
	}

	@Override
	public boolean shouldAttemptCodebreak(EntityPlayer player) {
		return !world.getBlockState(pos).getValue(KeypadBlock.POWERED);
	}

	@Override
	public void useCodebreaker(EntityPlayer player) {
		if (!world.isRemote)
			activate();
	}

	public boolean onRightClickWithActionItem(ItemStack stack, EntityPlayer player, boolean isCodebreaker, boolean isKeycardHolder) {
		Block block = world.getBlockState(pos).getBlock();

		if (isCodebreaker) {
			double chance = ConfigHandler.codebreakerChance;

			if (chance < 0.0D)
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(block), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
			else {
				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());

				if (CodebreakerItem.wasRecentlyUsed(stack))
					return false;

				boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
				NBTTagCompound tag = stack.getTagCompound();

				stack.damageItem(1, player);
				tag.setLong(CodebreakerItem.LAST_USED_TIME, System.currentTimeMillis());
				tag.setBoolean(CodebreakerItem.WAS_SUCCESSFUL, isSuccessful);

				if (isSuccessful)
					activate();
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
			}
		}
		else {
			if (isKeycardHolder) {
				ItemContainer holderInventory = ItemContainer.keycardHolder(stack);
				ITextComponent feedback = null;

				for (int i = 0; i < holderInventory.getSizeInventory(); i++) {
					ItemStack keycardStack = holderInventory.getStackInSlot(i);

					if (keycardStack.getItem() instanceof KeycardItem && keycardStack.hasTagCompound()) {
						feedback = insertCard(keycardStack, player);

						if (feedback == null)
							return true;
					}
				}

				if (feedback == null)
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block), Utils.localize("messages.securitycraft:keycard_holder.no_keycards"), TextFormatting.RED);
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block), Utils.localize("messages.securitycraft:keycard_holder.fail"), TextFormatting.RED);
			}
			else {
				ITextComponent feedback = insertCard(stack, player);

				if (feedback != null)
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(block), feedback, TextFormatting.RED);
			}
		}

		return true;
	}

	public ITextComponent insertCard(ItemStack stack, EntityPlayer player) {
		NBTTagCompound tag = stack.getTagCompound();
		Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

		//owner of this keycard reader and the keycard reader the keycard got linked to do not match
		if ((ConfigHandler.enableTeamOwnership && !TeamUtils.areOnSameTeam(getOwner(), keycardOwner)) || !getOwner().getUUID().equals(keycardOwner.getUUID()))
			return new TextComponentTranslation("messages.securitycraft:keycardReader.differentOwner");

		//the keycard's signature does not match this keycard reader's
		if (getSignature() != tag.getInteger("signature"))
			return new TextComponentTranslation("messages.securitycraft:keycardReader.wrongSignature");

		int level = ((KeycardItem) stack.getItem()).getLevel();

		//the keycard's level
		if (!getAcceptedLevels()[level]) //both are 0 indexed, so it's ok
			return new TextComponentTranslation("messages.securitycraft:keycardReader.wrongLevel", level + 1); //level is 0-indexed, so it has to be increased by one to match with the item name

		//don't consider the block powered if the signal length is 0, because players need to be able to toggle it off
		boolean powered = world.getBlockState(pos).getValue(KeycardReaderBlock.POWERED) && getSignalLength() > 0;

		if (!powered) {
			if (tag.getBoolean("limited")) {
				int uses = tag.getInteger("uses");

				if (uses <= 0)
					return new TextComponentTranslation("messages.securitycraft:keycardReader.noUses");

				if (!player.isCreative())
					tag.setInteger("uses", --uses);
			}

			activate();
		}

		return null;
	}

	public void activate() {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int signalLength = getSignalLength();

		world.setBlockState(pos, state.cycleProperty(KeycardReaderBlock.POWERED));
		BlockUtils.updateIndirectNeighbors(world, pos, block);

		if (signalLength > 0)
			world.scheduleUpdate(pos, block, signalLength);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(KeycardReaderBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.keycardReader);
		}
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
}
