package net.geforcemods.securitycraft.items;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class UniversalBlockReinforcerItem extends Item {
	public UniversalBlockReinforcerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!level.isClientSide) {
			maybeRemoveMending(heldItem);
			NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
					return new BlockReinforcerMenu(windowId, inv, UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				}

				@Override
				public Component getDisplayName() {
					return heldItem.getHoverName();
				}
			}, data -> data.writeBoolean(this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()));
		}

		return InteractionResultHolder.consume(heldItem);
	}

	public static boolean convertBlock(BlockState state, Level level, ItemStack stack, BlockPos pos, Player player) { //gets rid of the stuttering experienced with onBlockStartBreak
		if (!player.isCreative()) {
			boolean isReinforcing = isReinforcing(stack);
			Block block = state.getBlock();
			Block convertedBlock = (isReinforcing ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).get(block);
			BlockState convertedState = null;

			if (isReinforcing && convertedBlock instanceof IReinforcedBlock rb)
				convertedState = rb.convertToReinforced(level, pos, state);
			else if (!isReinforcing && block instanceof IReinforcedBlock rb)
				convertedState = rb.convertToVanilla(level, pos, state);

			if (convertedState != null) {
				BlockEntity be = level.getBlockEntity(pos);
				CompoundTag tag = null;

				if ((be instanceof IOwnable ownable && !ownable.isOwnedBy(player)) || !level.mayInteract(player, pos))
					return false;

				if (!level.isClientSide) {
					if (be != null) {
						tag = be.saveWithoutMetadata();

						if (be instanceof IModuleInventory inv)
							inv.dropAllModules();

						if (be instanceof Container container)
							container.clearContent();
						else if (be instanceof LecternBlockEntity lectern)
							lectern.clearContent();
					}

					level.setBlockAndUpdate(pos, convertedState);
					be = level.getBlockEntity(pos);

					if (be != null) { //in case the converted block gets removed immediately after it's set
						if (tag != null)
							be.load(tag);

						if (isReinforcing)
							((IOwnable) be).setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
					}

					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
				}

				return true;
			}
		}

		return false;
	}

	public static boolean isReinforcing(ItemStack stack) {
		return stack.is(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()) || !stack.getOrCreateTag().getBoolean("is_unreinforcing");
	}

	public static void maybeRemoveMending(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		if (enchantments.containsKey(Enchantments.MENDING)) {
			enchantments.remove(Enchantments.MENDING);
			EnchantmentHelper.setEnchantments(enchantments, stack);
		}
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return !EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.MENDING);
	}
}
