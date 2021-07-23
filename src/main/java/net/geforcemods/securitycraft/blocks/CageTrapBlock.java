package net.geforcemods.securitycraft.blocks;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.tileentity.ReinforcedIronBarsTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CageTrapBlock extends DisguisableBlock implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public CageTrapBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx){
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof CageTrapTileEntity te)
		{
			if(ctx instanceof EntityCollisionContext esc)
			{
				Entity entity = esc.getEntity();

				if(entity instanceof Player && (te.getOwner().isOwner((Player)entity) || ModuleUtils.isAllowed(te, entity)))
					return getCorrectShape(state, world, pos, ctx, te);
				if(entity instanceof Mob && !state.getValue(DEACTIVATED))
					return te.capturesMobs() ? Shapes.empty() : getCorrectShape(state, world, pos, ctx, te);
				else if(entity instanceof ItemEntity)
					return getCorrectShape(state, world, pos, ctx, te);
			}

			return state.getValue(DEACTIVATED) ? getCorrectShape(state, world, pos, ctx, te) : Shapes.empty();
		}
		else return Shapes.empty(); //shouldn't happen
	}

	private VoxelShape getCorrectShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx, DisguisableTileEntity disguisableTe)
	{
		ItemStack moduleStack = disguisableTe.getModule(ModuleType.DISGUISE);

		if(!moduleStack.isEmpty() && (((ModuleItem)moduleStack.getItem()).getBlockAddons(moduleStack.getTag()).size() > 0))
			return super.getCollisionShape(state, world, pos, ctx);
		else return Shapes.block();
	}

	@Override
	public void onEntityIntersected(Level world, BlockPos pos, Entity entity) {
		if(!world.isClientSide){
			CageTrapTileEntity tileEntity = (CageTrapTileEntity) world.getBlockEntity(pos);
			boolean isPlayer = entity instanceof Player;

			if(isPlayer || (entity instanceof Mob && tileEntity.capturesMobs())){
				if((isPlayer && ((IOwnable)world.getBlockEntity(pos)).getOwner().isOwner((Player)entity)))
					return;

				BlockState state = world.getBlockState(pos);

				if(state.getValue(DEACTIVATED))
					return;

				BlockPos topMiddle = pos.above(4);
				String ownerName = ((IOwnable)world.getBlockEntity(pos)).getOwner().getName();

				BlockModifier placer = new BlockModifier(world, new BlockPos.MutableBlockPos().set(pos), tileEntity.getOwner());

				placer.loop((w, p, o) -> {
					if(w.isEmptyBlock(p))
					{
						if(p.equals(topMiddle))
							w.setBlockAndUpdate(p, SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get().defaultBlockState());
						else
							w.setBlockAndUpdate(p, ((ReinforcedPaneBlock)SCContent.REINFORCED_IRON_BARS.get()).getStateForPlacement(w, p));
					}
				});
				placer.loop((w, p, o) -> {
					BlockEntity te = w.getBlockEntity(p);

					if(te instanceof IOwnable)
						((IOwnable)te).setOwner(o.getUUID(), o.getName());

					if(te instanceof ReinforcedIronBarsTileEntity)
						((ReinforcedIronBarsTileEntity)te).setCanDrop(false);
				});
				world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
				world.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 3.0F, 1.0F);

				if(isPlayer && PlayerUtils.isPlayerOnline(ownerName))
					PlayerUtils.sendMessageToPlayer(ownerName, Utils.localize(SCContent.CAGE_TRAP.get().getDescriptionId()), Utils.localize("messages.securitycraft:cageTrap.captured", ((Player) entity).getName(), Utils.getFormattedCoordinates(pos)), ChatFormatting.BLACK);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		ItemStack stack = player.getItemInHand(hand);

		if(stack.getItem() == SCContent.WIRE_CUTTERS.get())
		{
			if(!state.getValue(DEACTIVATED))
			{
				world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));

				if(!player.isCreative())
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}
		else if(stack.getItem() == Items.REDSTONE)
		{
			if(state.getValue(DEACTIVATED))
			{
				world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));

				if(!player.isCreative())
					stack.shrink(1);

				world.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player player)
	{
		return defaultBlockState().setValue(DEACTIVATED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CageTrapTileEntity(pos, state).intersectsEntities();
	}

	public static class BlockModifier
	{
		private Level world;
		private BlockPos.MutableBlockPos pos;
		private BlockPos origin;
		private Owner owner;

		public BlockModifier(Level world, BlockPos.MutableBlockPos origin, Owner owner)
		{
			this.world = world;
			pos = origin.move(-1, 1, -1);
			this.origin = origin.immutable();
			this.owner = owner;
		}

		public void loop(TriConsumer<Level,BlockPos.MutableBlockPos,Owner> ifTrue)
		{
			for(int y = 0; y < 4; y++)
			{
				for(int x = 0; x < 3; x++)
				{
					for(int z = 0; z < 3; z++)
					{
						//skip the middle column above the cage trap, but not the place where the horizontal iron bars are
						if(!(x == 1 && z == 1 && y != 3))
							ifTrue.accept(world, pos, owner);

						pos.move(0, 0, 1);
					}

					pos.move(1, 0, -3);
				}

				pos.move(-3, 1, 0);
			}

			pos.set(origin); //reset the mutable block pos for the next usage
		}
	}
}
