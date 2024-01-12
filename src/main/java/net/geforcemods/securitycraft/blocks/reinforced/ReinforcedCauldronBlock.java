package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedCauldronBlock extends BlockCauldron implements IReinforcedBlock {
	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) entity);
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof ReinforcedCauldronBlockEntity && ((ReinforcedCauldronBlockEntity) te).isAllowedToInteract(player))
				super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
			else
				addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
		}
		else
			super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof ReinforcedCauldronBlockEntity && ((ReinforcedCauldronBlockEntity) te).isAllowedToInteract(player))
			return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);

		return false;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(SCContent.reinforcedCauldron);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(SCContent.reinforcedCauldron);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.CAULDRON);
	}

	@Override
	public ItemStack convertToVanillaStack(ItemStack stackToConvert) throws Exception {
		return new ItemStack(Items.CAULDRON);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new ReinforcedCauldronBlockEntity();
	}
}
