package net.geforcemods.securitycraft.items;

import java.util.Map;

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
			World world = player.getEntityWorld();
			IBlockState state = world.getBlockState(pos);
			Block block = world.getBlockState(pos).getBlock();

			for (Block rb : IReinforcedBlock.BLOCKS) {
				IReinforcedBlock reinforcedBlock = (IReinforcedBlock) rb;

				if (reinforcedBlock.getVanillaBlocks().contains(block)) {
					IBlockState convertedState = null;
					TileEntity te = world.getTileEntity(pos);
					NBTTagCompound tag = null;

					if (te != null)
						tag = te.writeToNBT(new NBTTagCompound());

					if (reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount()) {
						for (int i = 0; i < reinforcedBlock.getAmount(); i++) {
							if (block.equals(reinforcedBlock.getVanillaBlocks().get(i)))
								convertedState = rb.getStateFromMeta(i);
						}
					}
					else
						convertedState = rb.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos)));

					if (convertedState != null) { //shouldn't happen, but just to be safe
						if (te instanceof IInventory)
							((IInventory) te).clear();

						world.setBlockState(pos, convertedState);
						te = world.getTileEntity(pos);

						if (te != null) { //in case the converted state gets removed immediately after it is placed down
							if (tag != null)
								te.readFromNBT(tag);

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
