package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class KeycardLockBlockEntity extends KeycardReaderBlockEntity {
	protected BooleanOption exactLevel = new BooleanOption("exactLevel", true);
	private boolean setUp = false;

	public KeycardLockBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYCARD_LOCK_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public InteractionResult onRightClickWithActionItem(ItemStack stack, InteractionHand hand, Player player, boolean isCodebreaker, boolean isKeycardHolder) {
		if (!isSetUp() && isOwnedBy(player)) {
			if (stack.getItem() instanceof KeycardItem item) {
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

				setUp = true;
				setAcceptedLevels(levels);
				setSignature(stack.getOrCreateTag().getInt("signature"));
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.setup_successful." + keySuffix, item.getLevel() + 1), ChatFormatting.GREEN);
				return InteractionResult.SUCCESS;
			}
			else {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getBlockState().getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.not_set_up"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}
		}

		return super.onRightClickWithActionItem(stack, hand, player, isCodebreaker, isKeycardHolder);
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

		super.onOptionChanged(option);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("set_up", setUp);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
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
