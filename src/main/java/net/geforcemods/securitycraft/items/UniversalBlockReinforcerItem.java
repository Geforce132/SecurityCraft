package net.geforcemods.securitycraft.items;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UniversalBlockReinforcerItem extends Item {
	public UniversalBlockReinforcerItem(int damage) {
		setMaxDamage(damage);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorDefaultDispenseItem() {
			private final BehaviorDefaultDispenseItem defaultDispenseItemBehavior = new BehaviorDefaultDispenseItem();

			@Override
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				IBlockState state = source.getBlockState();

				if (state.getBlock() == SCContent.reinforcedDispenser) {
					World level = source.getWorld();
					BlockPos modifyPos = source.getBlockPos().offset(state.getValue(BlockDispenser.FACING));
					IBlockState modifyState = level.getBlockState(modifyPos);

					if (convertBlock(modifyState, level, stack, modifyPos, ((IOwnable) source.getBlockTileEntity()).getOwner())) {
						if (!level.isRemote && stack.attemptDamageItem(1, level.rand, null))
							stack.setCount(0);
					}

					if (!level.isAirBlock(modifyPos))
						return stack;
				}

				return defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack heldItem = player.getHeldItem(hand);

		if (!world.isRemote) {
			maybeRemoveMending(heldItem);
			player.openGui(SecurityCraft.MODID, Screens.BLOCK_REINFORCER.ordinal(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	public static boolean convertBlock(ItemStack stack, BlockPos pos, EntityPlayer player) {
		World world = player.getEntityWorld();

		if (!player.capabilities.isCreativeMode && world.isBlockModifiable(player, pos)) {
			IBlockState state = world.getBlockState(pos);
			boolean result = convertBlock(state, world, stack, pos, new Owner(player));

			if (result && !world.isRemote)
				stack.damageItem(1, player);

			return result;
		}
		else
			return false;
	}

	public static boolean convertBlock(IBlockState state, World world, ItemStack stack, BlockPos pos, Owner owner) {
		boolean isReinforcing = isReinforcing(stack);
		Block blockToConvert = state.getBlock();
		IBlockState convertedState = null;

		if (isReinforcing) {
			Block convertedBlock = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(blockToConvert);

			if (convertedBlock instanceof IReinforcedBlock)
				convertedState = ((IReinforcedBlock) convertedBlock).convertToReinforcedState(state);
		}
		else if (blockToConvert instanceof IReinforcedBlock) {
			try {
				convertedState = ((IReinforcedBlock) blockToConvert).convertToVanillaState(state);
			}
			catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}

		if (convertedState != null) {
			TileEntity te = world.getTileEntity(pos);
			NBTTagCompound tag = null;

			if (!isReinforcing && te instanceof IOwnable && !((IOwnable) te).isOwnedBy(owner))
				return false;

			if (!world.isRemote) {
				if (te != null) {
					tag = te.writeToNBT(new NBTTagCompound());

					if (te instanceof IModuleInventory)
						((IModuleInventory) te).dropAllModules();

					if (te instanceof IInventory)
						((IInventory) te).clear();
				}

				world.setBlockState(pos, convertedState);
				te = world.getTileEntity(pos);

				if (te != null) { //in case the converted state gets removed immediately after it is placed down
					if (tag != null)
						te.readFromNBT(tag);

					if (isReinforcing)
						((IOwnable) te).setOwner(owner.getUUID(), owner.getName());
				}
			}

			return true;
		}

		return false;
	}

	public static boolean isReinforcing(ItemStack stack) {
		if (stack.getItem() == SCContent.universalBlockReinforcerLvL1)
			return true;

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		return !stack.getTagCompound().getBoolean("is_unreinforcing");
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
