package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class UniversalBlockReinforcerItem extends Item {
	public UniversalBlockReinforcerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide && player instanceof ServerPlayer) {
			NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
					return new BlockReinforcerMenu(windowId, inv, UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				}

				@Override
				public Component getDisplayName() {
					return Component.translatable(getDescriptionId());
				}
			}, data -> data.writeBoolean(this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()));
		}

		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	public static boolean convertBlock(BlockState vanillaState, Level level, ItemStack stack, BlockPos pos, Player player) //gets rid of the stuttering experienced with onBlockStartBreak
	{
		if (!player.isCreative()) {
			Block block = vanillaState.getBlock();
			Block rb = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(block);

			if (rb != null) {
				BlockState convertedState = ((IReinforcedBlock) rb).getConvertedState(vanillaState);
				BlockEntity be = level.getBlockEntity(pos);
				CompoundTag tag = null;

				if (be != null) {
					tag = be.saveWithoutMetadata();

					if (be instanceof Container container)
						container.clearContent();
				}

				level.setBlockAndUpdate(pos, convertedState);
				be = level.getBlockEntity(pos);

				if (be != null) { //in case the converted block gets removed immediately after it's set
					if (tag != null)
						be.load(tag);

					((IOwnable) be).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
				}

				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
				return false;
			}
		}

		return true;
	}
}
