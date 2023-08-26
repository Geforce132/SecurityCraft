package net.geforcemods.securitycraft.items;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
		if (!player.capabilities.isCreativeMode) {
			boolean isReinforcing = isReinforcing(stack);
			World world = player.getEntityWorld();
			IBlockState state = world.getBlockState(pos);
			Block blockToConvert = state.getBlock();

			for (Block securityCraftBlock : IReinforcedBlock.BLOCKS) {
				IReinforcedBlock reinforcedBlock = (IReinforcedBlock) securityCraftBlock;

				if (isReinforcing && reinforcedBlock.getVanillaBlocks().contains(blockToConvert) || !isReinforcing && securityCraftBlock == blockToConvert) {
					IBlockState convertedState = null;
					TileEntity te = world.getTileEntity(pos);
					NBTTagCompound tag = null;

					if (te != null)
						tag = te.writeToNBT(new NBTTagCompound());

					if (reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount()) {
						if (isReinforcing) {
							for (int i = 0; i < reinforcedBlock.getAmount(); i++) {
								Block vanillaBlock = reinforcedBlock.getVanillaBlocks().get(i);

								if (blockToConvert.equals(vanillaBlock))
									convertedState = securityCraftBlock.getStateFromMeta(i);
							}
						}
						else
							convertedState = reinforcedBlock.getVanillaBlocks().get(securityCraftBlock.getMetaFromState(state)).getDefaultState();
					}
					else {
						if (isReinforcing)
							convertedState = securityCraftBlock.getStateFromMeta(blockToConvert.getMetaFromState(blockToConvert.getActualState(state, world, pos)));
						else
							convertedState = reinforcedBlock.getVanillaBlocks().get(0).getStateFromMeta(blockToConvert.getMetaFromState(blockToConvert.getActualState(state, world, pos)));
					}

					if (convertedState != null) {
						if (te instanceof IInventory)
							((IInventory) te).clear();

						if (!isReinforcing && te instanceof IOwnable && !((IOwnable) te).isOwnedBy(player))
							return false;

						world.setBlockState(pos, convertedState);
						te = world.getTileEntity(pos);

						if (te != null) { //in case the converted state gets removed immediately after it is placed down
							if (tag != null)
								te.readFromNBT(tag);

							if (isReinforcing)
								((IOwnable) te).setOwner(player.getGameProfile().getId().toString(), player.getName());
						}

						stack.damageItem(1, player);
					}

					return true;
				}
			}
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
