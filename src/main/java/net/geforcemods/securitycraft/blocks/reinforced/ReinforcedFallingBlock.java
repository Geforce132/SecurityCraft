package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.FallingOwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReinforcedFallingBlock extends BaseReinforcedBlock {
	public ReinforcedFallingBlock(Block disguisedBlock) {
		super(disguisedBlock);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess level, BlockPos pos, EntityPlayer player) {
		return ConfigHandler.alwaysDrop || super.canHarvestBlock(level, pos, player);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return convertToVanillaState(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess level, BlockPos pos) {
		return convertToVanillaState(state).getMapColor(level, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestTool(vanillaState);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().isToolEffective(type, vanillaState);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestLevel(vanillaState);
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return convertToVanillaState(state).isTranslucent();
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isRemote && (world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0) {
			if (world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				if (!world.isRemote && world.getTileEntity(pos) instanceof IOwnable)
					world.spawnEntity(new FallingOwnableBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos), ((IOwnable) world.getTileEntity(pos)).getOwner()));
			}
			else {
				BlockPos blockpos;

				world.setBlockToAir(pos);

				for (blockpos = pos.down(); (world.isAirBlock(blockpos) || canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

				if (blockpos.getY() > 0)
					world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	@Override
	public int tickRate(World world) {
		return 2;
	}

	public static boolean canFallThrough(IBlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (rand.nextInt(16) == 0 && canFallThrough(world.getBlockState(pos.down()))) {
			double particleX = pos.getX() + rand.nextFloat();
			double particleY = pos.getY() - 0.05D;
			double particleZ = pos.getZ() + rand.nextFloat();

			world.spawnParticle(EnumParticleTypes.FALLING_DUST, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
		}
	}
}
