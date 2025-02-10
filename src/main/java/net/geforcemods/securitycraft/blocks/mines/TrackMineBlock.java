package net.geforcemods.securitycraft.blocks.mines;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IBlockWithNoDrops;
import net.minecraft.block.BlockRail;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TrackMineBlock extends BlockRail implements IExplosive, ITileEntityProvider, IBlockWithNoDrops {
	public TrackMineBlock() {
		setSoundType(SoundType.METAL);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getPlayerRelativeBlockHardness, state, player, level, pos);
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
		if (placer instanceof EntityPlayer) {
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldStack = player.getHeldItem(hand);
		Item heldItem = heldStack.getItem();

		if (heldItem == SCContent.mineRemoteAccessTool)
			return false;

		if (isActive(world, pos) && isDefusable() && heldItem == SCContent.wireCutters && defuseMine(world, pos)) {
			if (!player.isCreative())
				heldStack.damageItem(1, player);

			world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return true;
		}

		if (!isActive(world, pos) && heldItem == Items.FLINT_AND_STEEL && activateMine(world, pos)) {
			if (!player.isCreative())
				heldStack.damageItem(1, player);

			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return true;
		}

		return false;
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive()) {
			world.destroyBlock(pos, false);
			world.newExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.smallerMineExplosion ? 3.0F : 6.0F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
			cart.setDead();
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive()) {
			world.destroyBlock(pos, false);
			world.newExplosion((Entity) null, pos.getX(), pos.up().getY(), pos.getZ(), ConfigHandler.smallerMineExplosion ? 3.0F : 6.0F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
		}
	}

	@Override
	public boolean activateMine(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TrackMineBlockEntity && !((TrackMineBlockEntity) te).isActive()) {
			((TrackMineBlockEntity) te).activate();
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive()) {
			((TrackMineBlockEntity) te).deactivate();
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		return te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TrackMineBlockEntity();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Collections.emptyList();
	}
}
