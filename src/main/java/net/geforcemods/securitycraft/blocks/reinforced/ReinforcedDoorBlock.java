package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.ReinforcedDoorBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReinforcedDoorBlock extends BlockDoor implements ITileEntityProvider {
	private final float destroyTimeForOwner;

	public ReinforcedDoorBlock(Material material) {
		super(material);
		setBlockUnbreakable();
		setSoundType(SoundType.METAL);
		destroyTimeForOwner = 5.0F;
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getPlayerRelativeBlockHardness, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess level, BlockPos pos, EntityPlayer player) {
		return ConfigHandler.alwaysDrop || super.canHarvestBlock(level, pos, player);
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		onNeighborChanged(world, pos, fromPos);
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 *
	 * @param access The world the change occured in
	 * @param firstDoorPos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(IBlockAccess access, BlockPos firstDoorPos, BlockPos neighbor) {
		World level = (World) access;
		IBlockState firstDoorState = level.getBlockState(firstDoorPos).getActualState(access, firstDoorPos);
		Block neighborBlock = level.getBlockState(neighbor).getBlock();

		if (firstDoorState.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
			BlockPos blockBelow = firstDoorPos.down();
			IBlockState stateBelow = level.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				level.setBlockToAir(firstDoorPos);
			else if (neighborBlock != this)
				onNeighborChanged(level, blockBelow, neighbor);
		}
		else {
			boolean drop = false;
			BlockPos blockAbove = firstDoorPos.up();
			IBlockState stateAbove = level.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this) {
				level.setBlockToAir(firstDoorPos);
				drop = true;
			}

			if (!level.isSideSolid(firstDoorPos.down(), EnumFacing.UP)) {
				level.setBlockToAir(firstDoorPos);
				drop = true;

				if (stateAbove.getBlock() == this)
					level.setBlockToAir(blockAbove);
			}

			if (drop) {
				if (!level.isRemote)
					dropBlockAsItem(level, firstDoorPos, firstDoorState, 0);
			}
			else if (neighborBlock != this) {
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, firstDoorPos) || BlockUtils.hasActiveSCBlockNextTo(level, firstDoorPos.up());
				EnumFacing directionToCheck = firstDoorState.getValue(FACING).rotateY();
				BlockPos secondDoorPos = firstDoorPos.offset(directionToCheck);
				IBlockState secondDoorState = level.getBlockState(secondDoorPos).getActualState(access, secondDoorPos);

				if (!(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(HINGE) == EnumHingePosition.RIGHT && firstDoorState.getValue(HINGE) != secondDoorState.getValue(HINGE))) {
					secondDoorPos = firstDoorPos.offset(directionToCheck.getOpposite());
					secondDoorState = level.getBlockState(secondDoorPos).getActualState(access, secondDoorPos);

					if (!(secondDoorState != null && secondDoorState.getBlock() == SCContent.reinforcedDoor && secondDoorState.getValue(HINGE) == EnumHingePosition.LEFT && firstDoorState.getValue(HINGE) != secondDoorState.getValue(HINGE)))
						secondDoorPos = null;
				}

				boolean hasSecondDoorActiveSCBlock = secondDoorPos != null && (BlockUtils.hasActiveSCBlockNextTo(level, secondDoorPos) || BlockUtils.hasActiveSCBlockNextTo(level, secondDoorPos.up()));
				boolean shouldBeOpen = hasActiveSCBlock != hasSecondDoorActiveSCBlock || hasActiveSCBlock;

				if (shouldBeOpen != firstDoorState.getValue(OPEN))
					setDoorState(level, firstDoorPos, firstDoorState, shouldBeOpen);

				if (secondDoorPos != null && shouldBeOpen != secondDoorState.getValue(OPEN))
					setDoorState(level, secondDoorPos, secondDoorState, shouldBeOpen);
			}
		}
	}

	public void setDoorState(World level, BlockPos pos, IBlockState state, boolean open) {
		level.setBlockState(pos, state.withProperty(OPEN, open), 2);
		level.playEvent((EntityPlayer) null, open ? 1005 : 1011, pos, 0);
		level.markBlockRangeForRenderUpdate(pos, pos);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (!ConfigHandler.vanillaToolBlockBreaking && te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		TileEntity te = world.getTileEntity(pos);

		return te != null && te.receiveClientEvent(id, param);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return new ItemStack(SCContent.reinforcedDoorItem);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : SCContent.reinforcedDoorItem;
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ReinforcedDoorBlockEntity();
	}
}