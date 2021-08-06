package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedCauldronTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedCauldronBlock extends CauldronBlock implements IReinforcedBlock {

	public ReinforcedCauldronBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		Entity entity = ctx.getEntity();

		if (entity instanceof PlayerEntity) {
			PlayerEntity player = ((PlayerEntity)entity);
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof ReinforcedCauldronTileEntity && ((ReinforcedCauldronTileEntity)te).isAllowedToInteract(player))
				return SHAPE;
			else
				return VoxelShapes.fullCube();
		}

		return SHAPE;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof ReinforcedCauldronTileEntity && ((ReinforcedCauldronTileEntity)te).isAllowedToInteract(player)) {
			return super.onBlockActivated(state, world, pos, player, hand, hit);
		}

		return ActionResultType.PASS;
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.CAULDRON;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return getDefaultState().with(LEVEL, vanillaState.get(LEVEL));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ReinforcedCauldronTileEntity();
	}
}
