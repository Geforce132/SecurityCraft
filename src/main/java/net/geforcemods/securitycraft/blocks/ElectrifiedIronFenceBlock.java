package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.IronFenceBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ElectrifiedIronFenceBlock extends OwnableFenceBlock {
	public ElectrifiedIronFenceBlock(Material material) {
		super(material, MapColor.IRON);
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		Block block = world.getBlockState(pos).getBlock();

		//split up oneliner to be more readable
		if (block != this && !(block instanceof BlockFenceGate) && block != SCContent.electrifiedIronFenceGate) {
			if (block.getDefaultState().getMaterial().isOpaque())
				return block.getDefaultState().getMaterial() != Material.GOURD;
			else
				return false;
		}
		else
			return true;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		hurtOrConvertEntity(world, pos, state, entity);
	}

	public static void hurtOrConvertEntity(World world, BlockPos pos, IBlockState state, Entity entity) {
		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof IOwnable))
			return;

		IOwnable te = (IOwnable) tile;

		if (te instanceof IEMPAffectedBE && ((IEMPAffectedBE) te).isShutDown())
			return;

		if (world.provider.getWorldTime() % 20 != 0)
			return;
		else if (!entity.isEntityAlive() || !state.getBoundingBox(world, pos).offset(pos).grow(0.01D).intersects(entity.getEntityBoundingBox()))
			return;
		else if (entity instanceof EntityItem) //so dropped items don't get destroyed
			return;
		else if (entity instanceof EntityPlayer) { //owner check
			if (te.isOwnedBy(entity))
				return;
		}
		else if (((OwnableBlockEntity) world.getTileEntity(pos)).allowsOwnableEntity(entity))
			return;
		else if (!world.isRemote) {
			EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true);

			entity.onStruckByLightning(lightning);
			entity.extinguish();
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.ELECTRICITY, 6.0F); //3 hearts per attack
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new IronFenceBlockEntity();
	}
}