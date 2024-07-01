package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ReinforcedMagmaBlock extends BaseReinforcedBlock {
	public ReinforcedMagmaBlock() {
		super(Material.ROCK, MapColor.NETHERRACK, Blocks.MAGMA);
		setLightLevel(0.2F);
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		if (!entity.isImmuneToFire() && entity instanceof EntityLivingBase && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) entity)) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof OwnableBlockEntity && !((OwnableBlockEntity) te).isOwnedBy(entity))
				entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
		}

		super.onEntityWalk(world, pos, entity);
	}

	@Override
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess level, BlockPos pos) {
		return 15728880;
	}

	@Override
	public boolean canEntitySpawn(IBlockState state, Entity entity) {
		return entity.isImmuneToFire();
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP;
	}
}
