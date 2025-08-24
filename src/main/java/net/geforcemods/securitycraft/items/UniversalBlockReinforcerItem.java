package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UniversalBlockReinforcerItem extends Item {
	public UniversalBlockReinforcerItem(Item.Properties properties) {
		super(properties);

		DispenserBlock.registerBehavior(this, new OptionalDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(BlockSource source, ItemStack stack) {
				BlockState state = source.state();

				if (state.is(SCContent.REINFORCED_DISPENSER)) {
					Level level = source.level();
					BlockPos modifyPos = source.pos().relative(state.getValue(DispenserBlock.FACING));
					BlockState modifyState = level.getBlockState(modifyPos);

					setSuccess(convertBlock(modifyState, level, stack, modifyPos, null, ((IOwnable) source.blockEntity()).getOwner()));

					if (isSuccess() && level instanceof ServerLevel serverLevel)
						stack.hurtAndBreak(1, serverLevel, null, i -> {});

					return stack;
				}

				return defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!level.isClientSide) {
			maybeRemoveMending(level.registryAccess(), heldItem);
			player.openMenu(new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
					return new BlockReinforcerMenu(windowId, inv, UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				}

				@Override
				public Component getDisplayName() {
					return heldItem.getHoverName();
				}

				@Override
				public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
					MenuProvider.super.writeClientSideData(menu, buffer);
					buffer.writeBoolean(UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				}
			});
		}

		return InteractionResult.CONSUME;
	}

	public static boolean convertBlock(BlockState state, Level level, ItemStack stack, BlockPos pos, Player player) {
		if (!player.isCreative() && level.mayInteract(player, pos)) {
			boolean result = convertBlock(state, level, stack, pos, player, new Owner(player));

			if (result && !level.isClientSide)
				stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));

			return result;
		}
		else
			return false;
	}

	public static boolean convertBlock(BlockState state, Level level, ItemStack stack, BlockPos pos, Player player, Owner owner) {
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

			if (be instanceof IOwnable ownable && ((player != null && !ownable.isOwnedBy(player)) || !ownable.isOwnedBy(owner)))
				return false;

			if (!level.isClientSide) {
				if (be != null) {
					tag = be.saveWithoutMetadata(level.registryAccess());

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
						be.loadWithComponents(tag, level.registryAccess());

					if (isReinforcing)
						((IOwnable) be).setOwner(owner.getUUID(), owner.getName());
				}
			}

			return true;
		}

		return false;
	}

	public static boolean isReinforcing(ItemStack stack) {
		return stack.is(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()) || !stack.has(SCContent.UNREINFORCING);
	}

	public static void maybeRemoveMending(HolderLookup.Provider lookupProvider, ItemStack stack) {
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		Holder<Enchantment> mending = lookupProvider.lookup(Registries.ENCHANTMENT).get().getOrThrow(Enchantments.MENDING);

		if (enchantments != null && enchantments.getLevel(mending) > 0) {
			ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);

			mutable.set(mending, 0);
			stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
		}
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).keySet().stream().noneMatch(e -> e.is(Enchantments.MENDING));
	}
}
