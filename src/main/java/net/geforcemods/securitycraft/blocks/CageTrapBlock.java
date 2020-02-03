package net.geforcemods.securitycraft.blocks;

import org.apache.logging.log4j.util.TriConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CageTrapBlock extends DisguisableBlock implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public CageTrapBlock(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).doesNotBlockMovement());
		setDefaultState(stateContainer.getBaseState().with(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx){
		return state.get(DEACTIVATED) ? super.getCollisionShape(state, world, pos, ctx) : VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			CageTrapTileEntity tileEntity = (CageTrapTileEntity) world.getTileEntity(pos);
			boolean isPlayer = entity instanceof PlayerEntity;

			if(isPlayer || (entity instanceof MobEntity && tileEntity.capturesMobs())){
				if((isPlayer && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner((PlayerEntity)entity)))
					return;

				if(BlockUtils.getBlockProperty(world, pos, DEACTIVATED))
					return;

				BlockPos topMiddle = pos.up(4);
				String ownerName = ((IOwnable)world.getTileEntity(pos)).getOwner().getName();

				BlockModifier placer = new BlockModifier(world, new MutableBlockPos(pos), tileEntity.getOwner());

				placer.loop((w, p, o) -> {
					if(w.isAirBlock(p))
					{
						if(p.equals(topMiddle))
							w.setBlockState(p, SCContent.horizontalReinforcedIronBars.getDefaultState());
						else
							w.setBlockState(p, ((ReinforcedPaneBlock)SCContent.reinforcedIronBars).getStateForPlacement(w, p));
					}
				});
				placer.loop((w, p, o) -> {
					TileEntity te = w.getTileEntity(p);

					if(te instanceof IOwnable)
						((IOwnable)te).getOwner().set(o);
				});
				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);

				if(isPlayer && PlayerUtils.isPlayerOnline(ownerName))
					PlayerUtils.getPlayerFromName(ownerName).sendMessage(new TranslationTextComponent("["+ TextFormatting.BLACK + ClientUtils.localize(SCContent.cageTrap.getTranslationKey()) + TextFormatting.RESET + "] " + ClientUtils.localize("messages.securitycraft:cageTrap.captured").replace("#player", ((PlayerEntity) entity).getName().getFormattedText()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity player)
	{
		return getDefaultState().with(DEACTIVATED, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CageTrapTileEntity().intersectsEntities();
	}

	public static class BlockModifier
	{
		private World world;
		private MutableBlockPos pos;
		private BlockPos origin;
		private Owner owner;

		public BlockModifier(World world, MutableBlockPos origin, Owner owner)
		{
			this.world = world;
			pos = origin.move(-1, 1, -1);
			this.origin = origin.toImmutable();
			this.owner = owner;
		}

		public void loop(TriConsumer<World,MutableBlockPos,Owner> ifTrue)
		{
			for(int y = 0; y < 4; y++)
			{
				for(int x = 0; x < 3; x++)
				{
					for(int z = 0; z < 3; z++)
					{
						//skip the middle column above the cage trap, but not the place where the horiztonal iron bars are
						if(!(x == 1 && z == 1 && y != 3))
							ifTrue.accept(world, pos, owner);

						pos.move(0, 0, 1);
					}

					pos.move(1, 0, -3);
				}

				pos.move(-3, 1, 0);
			}

			pos.setPos(origin); //reset the mutable block pos for the next usage
		}

		@FunctionalInterface
		public interface TriFunction<T,U,V,R>
		{
			R apply(T t, U u, V v);
		}
	}
}
