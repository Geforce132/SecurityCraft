package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedCauldronTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedCauldronBlock extends CauldronBlock implements IReinforcedBlock, EntityBlock
{
	public ReinforcedCauldronBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext collisionContext)
	{
		if(collisionContext instanceof EntityCollisionContext ctx)
		{
			if(ctx.getEntity().isPresent())
			{
				Entity entity = ctx.getEntity().get();

				if(entity instanceof Player player)
				{
					BlockEntity tile = world.getBlockEntity(pos);

					if(tile instanceof ReinforcedCauldronTileEntity te && te.isAllowedToInteract(player))
						return SHAPE;
					else
						return Shapes.block();
				}
			}
		}

		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof ReinforcedCauldronTileEntity te && te.isAllowedToInteract(player))
			return super.use(state, world, pos, player, hand, hit);

		return InteractionResult.PASS;
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.CAULDRON;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ReinforcedCauldronTileEntity(pos, state);
	}
}
