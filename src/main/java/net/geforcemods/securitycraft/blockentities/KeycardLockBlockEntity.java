package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class KeycardLockBlockEntity extends KeycardReaderBlockEntity {
	protected BooleanOption exactLevel = new BooleanOption("exactLevel", true);
	private boolean setUp = false;

	@Override
	public boolean onRightClickWithActionItem(ItemStack stack, EntityPlayer player, boolean isCodebreaker, boolean isKeycardHolder) {
		if (!isSetUp() && isOwnedBy(player)) {
			if (stack.getItem() instanceof KeycardItem) {
				KeycardItem item = (KeycardItem) stack.getItem();
				boolean[] levels = {
						false, false, false, false, false
				};
				String keySuffix;

				if (exactLevel.get()) {
					levels[item.getLevel()] = true;
					keySuffix = "exact";
				}
				else {
					for (int i = item.getLevel(); i < 5; i++) {
						levels[i] = true;
					}

					keySuffix = "above";
				}

				if (!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());

				setUp = true;
				setAcceptedLevels(levels);
				setSignature(stack.getTagCompound().getInteger("signature"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(world.getBlockState(pos).getBlock()), Utils.localize("messages.securitycraft:keycard_lock.setup_successful." + keySuffix, item.getLevel() + 1), TextFormatting.GREEN);
				return true;
			}
			else {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(world.getBlockState(pos).getBlock()), Utils.localize("messages.securitycraft:keycard_lock.not_set_up"), TextFormatting.RED);
				return false;
			}
		}

		return super.onRightClickWithActionItem(stack, player, isCodebreaker, isKeycardHolder);
	}

	public boolean isSetUp() {
		return setUp;
	}

	public void reset() {
		setUp = false;
		setAcceptedLevels(new boolean[] {
				false, false, false, false, false
		});
		setSignature(0);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == exactLevel) {
			boolean[] acceptedLevels = getAcceptedLevels();
			boolean swap = false;

			for (int i = 0; i < acceptedLevels.length; i++) {
				if (swap)
					acceptedLevels[i] = !acceptedLevels[i];
				else if (acceptedLevels[i])
					swap = true;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("set_up", setUp);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setUp = tag.getBoolean("set_up");
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DENYLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendDenylistMessage, signalLength, disabled, exactLevel
		};
	}
}
