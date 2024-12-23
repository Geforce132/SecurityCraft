package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDisguisable {
	public default ItemStack getDisguisedStack(IBlockAccess world, BlockPos pos) {
		IBlockState disguisedState = getDisguisedBlockState(world.getTileEntity(pos));

		if (disguisedState != null) {
			ItemStack stack = new ItemStack(disguisedState.getBlock());

			if (stack.getItem().getHasSubtypes())
				stack.setItemDamage(disguisedState.getBlock().getMetaFromState(disguisedState));

			return stack;
		}

		return getDefaultStack();
	}

	public default ItemStack getDefaultStack() {
		return new ItemStack((Block) this);
	}

	public static IBlockState getDisguisedBlockStateUnknown(TileEntity tile) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());

		if (state.getBlock() instanceof IDisguisable)
			return ((IDisguisable) state.getBlock()).getDisguisedBlockState(tile);
		else
			return null;
	}

	public default IBlockState getDisguisedBlockState(TileEntity tile) {
		if (tile instanceof IModuleInventory) {
			IModuleInventory te = (IModuleInventory) tile;

			return getDisguisedBlockStateFromStack(tile.getWorld(), tile.getPos(), te.isModuleEnabled(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY);
		}

		return null;
	}

	public default IBlockState getDisguisedBlockStateFromStack(IBlockAccess world, BlockPos pos, ItemStack module) {
		if (!module.isEmpty()) {
			if (!module.hasTagCompound())
				module.setTagCompound(new NBTTagCompound());

			IBlockState disguisedState = NBTUtil.readBlockState(module.getTagCompound().getCompoundTag("SavedState"));

			if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR)
				return disguisedState;
			else if (world != null && pos != null) { //fallback, mainly for upgrading old worlds from before the state selector existed
				ItemStack disguisedStack = ModuleItem.getAddonAsStack(module);
				Block block = Block.getBlockFromItem(disguisedStack.getItem());
				boolean hasMeta = disguisedStack.getHasSubtypes();

				disguisedState = hasMeta ? block.getStateFromMeta(disguisedStack.getItemDamage()) : block.getDefaultState();

				if (block != this)
					return disguisedState.getActualState(world, pos);
			}
		}

		return null;
	}

	public static BlockFaceShape getDisguisedBlockFaceShape(IBlockAccess world, BlockPos pos, EnumFacing face) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IModuleInventory && ((IModuleInventory) te).isModuleEnabled(ModuleType.DISGUISE)) {
			ItemStack module = ((IModuleInventory) te).getModule(ModuleType.DISGUISE);

			if (!module.hasTagCompound())
				module.setTagCompound(new NBTTagCompound());

			IBlockState disguisedState = NBTUtil.readBlockState(module.getTagCompound().getCompoundTag("SavedState"));

			if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR)
				return disguisedState.getBlockFaceShape(world, pos, face);
			else {
				Block block = ModuleItem.getBlockAddon(module);

				if (block == null)
					return BlockFaceShape.SOLID;
				else
					return block.getDefaultState().getBlockFaceShape(world, pos, face);
			}
		}

		return BlockFaceShape.SOLID;
	}

	@SideOnly(Side.CLIENT)
	public static boolean shouldDisguisedSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (!(world.getTileEntity(pos) instanceof IModuleInventory))
			return true;

		IModuleInventory te = (IModuleInventory) world.getTileEntity(pos);

		if (te.isModuleEnabled(ModuleType.DISGUISE)) {
			ItemStack disguiseModule = te.getModule(ModuleType.DISGUISE);

			if (!disguiseModule.hasTagCompound())
				disguiseModule.setTagCompound(new NBTTagCompound());

			IBlockState disguisedState = NBTUtil.readBlockState(disguiseModule.getTagCompound().getCompoundTag("SavedState"));

			if (disguisedState != null && disguisedState.getBlock() != Blocks.AIR) {
				// If this block has a disguise module added with a transparent block inserted.
				if (!disguisedState.isOpaqueCube() || !disguisedState.isFullCube())
					return DisguisableBlock.checkForSideTransparency(world, world.getBlockState(pos.offset(side)), pos.offset(side));
			}
			else {
				Block blockToDisguiseAs = ModuleItem.getBlockAddon(disguiseModule);

				// If this block has a disguise module added with a transparent block inserted.
				if (blockToDisguiseAs != null && (!blockToDisguiseAs.getDefaultState().isOpaqueCube() || !blockToDisguiseAs.getDefaultState().isFullCube()))
					return DisguisableBlock.checkForSideTransparency(world, world.getBlockState(pos.offset(side)), pos.offset(side));
			}
		}

		return true;
	}
}
