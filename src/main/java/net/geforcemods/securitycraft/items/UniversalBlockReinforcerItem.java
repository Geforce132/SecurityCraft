package net.geforcemods.securitycraft.items;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalBlockReinforcerItem extends Item {
	public UniversalBlockReinforcerItem(Item.Properties properties) {
		super(properties);

		DispenserBlock.registerBehavior(this, new OptionalDispenseBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(IBlockSource source, ItemStack stack) {
				BlockState state = source.getBlockState();

				if (state.is(SCContent.REINFORCED_DISPENSER.get())) {
					ServerWorld level = source.getLevel();
					BlockPos modifyPos = source.getPos().relative(state.getValue(DispenserBlock.FACING));
					BlockState modifyState = level.getBlockState(modifyPos);

					setSuccess(convertBlock(modifyState, level, stack, modifyPos, ((IOwnable) source.getEntity()).getOwner()));

					if (isSuccess() && !level.isClientSide) {
						if (stack.hurt(1, level.getRandom(), null))
							stack.setCount(0);
					}

					return stack;
				}

				return defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!level.isClientSide) {
			maybeRemoveMending(heldItem);
			NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
				@Override
				public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
					return new BlockReinforcerMenu(windowId, inv, UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				}

				@Override
				public ITextComponent getDisplayName() {
					return heldItem.getHoverName();
				}
			}, data -> data.writeBoolean(this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()));
		}

		return ActionResult.consume(heldItem);
	}

	public static boolean convertBlock(BlockState state, World level, ItemStack stack, BlockPos pos, PlayerEntity player) {
		if (!player.isCreative() && level.mayInteract(player, pos)) {
			boolean result = convertBlock(state, level, stack, pos, new Owner(player));

			if (result && !level.isClientSide)
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));

			return result;
		}
		else
			return false;
	}

	public static boolean convertBlock(BlockState state, World level, ItemStack stack, BlockPos pos, Owner owner) {
		boolean isReinforcing = isReinforcing(stack);
		Block block = state.getBlock();
		Block convertedBlock = (isReinforcing ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).get(block);
		BlockState convertedState = null;

		if (isReinforcing && convertedBlock instanceof IReinforcedBlock)
			convertedState = ((IReinforcedBlock) convertedBlock).convertToReinforced(level, pos, state);
		else if (!isReinforcing && block instanceof IReinforcedBlock)
			convertedState = ((IReinforcedBlock) block).convertToVanilla(level, pos, state);

		if (convertedState != null) {
			TileEntity be = level.getBlockEntity(pos);
			CompoundNBT tag = null;

			if (be instanceof IOwnable && !((IOwnable) be).isOwnedBy(owner))
				return false;

			if (!level.isClientSide) {
				if (be != null) {
					tag = be.save(new CompoundNBT());

					if (be instanceof IModuleInventory)
						((IModuleInventory) be).dropAllModules();

					if (be instanceof IInventory)
						((IInventory) be).clearContent();
					else if (be instanceof ReinforcedHopperBlockEntity)
						level.removeBlockEntity(pos); //because ReinforcedHopperBlock overrides Block#is to mimic the vanilla hopper, the BE needs to be removed here (usually this is done in Block#onRemove)
					else if (be instanceof LecternTileEntity)
						((LecternTileEntity) be).clearContent();
				}

				level.setBlockAndUpdate(pos, convertedState);
				be = level.getBlockEntity(pos);

				if (be != null) { //in case the converted state gets removed immediately after it is placed down
					if (tag != null)
						be.load(convertedState, tag);

					if (isReinforcing)
						((IOwnable) be).setOwner(owner.getUUID(), owner.getName());
				}
			}

			return true;
		}

		return false;
	}

	public static boolean isReinforcing(ItemStack stack) {
		return stack.getItem() == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get() || !stack.getOrCreateTag().getBoolean("is_unreinforcing");
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
