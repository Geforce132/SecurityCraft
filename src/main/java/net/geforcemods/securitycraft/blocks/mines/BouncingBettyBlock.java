package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.SecurityCraftBlockEntity;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

public class BouncingBettyBlock extends ExplosiveBlock implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

	public BouncingBettyBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		if (!world.getBlockState(pos.below()).isAir())
			return;
		else if (world.getBlockState(pos).getValue(DEACTIVATED))
			world.destroyBlock(pos, true);
		else
			explode(world, pos);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public void onEntityIntersected(Level world, BlockPos pos, Entity entity) {
		if(!EntityUtils.doesEntityOwn(entity, world, pos))
			if(entity instanceof LivingEntity lEntity)
				explode(world, pos);
	}
	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player){
		if(!player.isCreative() && !EntityUtils.doesPlayerOwn(player, world, pos))
			explode(world, pos);
	}

	@Override
	public boolean activateMine(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(!state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(Level world, BlockPos pos){
		if(world.isClientSide || world.getBlockState(pos).getValue(DEACTIVATED))
			return;

		world.destroyBlock(pos, false);
		BouncingBetty bouncingBettyEntity = new BouncingBetty(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
		bouncingBettyEntity.fuse = 15;
		bouncingBettyEntity.setDeltaMovement(bouncingBettyEntity.getDeltaMovement().multiply(1, 0, 1).add(0, 0.5D, 0));
		WorldUtils.addScheduledTask(world, () -> world.addFreshEntity(bouncingBettyEntity));
		bouncingBettyEntity.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.tnt.primed")), 1.0F, 1.0F);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state){
		return new ItemStack(asItem());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(Level world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SecurityCraftBlockEntity(pos, state).intersectsEntities();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeAbstract, WorldUtils::blockEntityTicker);
	}
}
