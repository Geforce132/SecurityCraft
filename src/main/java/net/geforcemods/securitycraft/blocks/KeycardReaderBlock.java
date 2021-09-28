package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class KeycardReaderBlock extends DisguisableBlock  {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeycardReaderBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(!world.isClientSide)
		{
			KeycardReaderBlockEntity te = (KeycardReaderBlockEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, new TranslatableComponent(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			}
			else
			{
				ItemStack stack = player.getItemInHand(hand);
				Item item = stack.getItem();
				boolean isCodebreaker = item == SCContent.CODEBREAKER.get();

				//either no keycard, or an unlinked keycard, or an admin tool
				if((!(item instanceof KeycardItem) || !stack.hasTag() || !stack.getTag().getBoolean("linked")) && !isCodebreaker)
				{
					//only allow the owner and players on the allowlist to open the gui
					if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
						NetworkHooks.openGui((ServerPlayer)player, te, pos);
				}
				else if(item != SCContent.LIMITED_USE_KEYCARD.get()) //limited use keycards are only crafting components now
				{
					if(isCodebreaker)
					{
						if(!player.isCreative())
							stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

						if(new Random().nextInt(3) == 1)
							activate(world, pos, te.getSignalLength());
					}
					else
					{
						MutableComponent feedback = insertCard(world, pos, te, stack, player);

						if(feedback != null)
							PlayerUtils.sendMessageToPlayer(player, new TranslatableComponent(getDescriptionId()), feedback, ChatFormatting.RED);
					}
				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	public MutableComponent insertCard(Level world, BlockPos pos, KeycardReaderBlockEntity te, ItemStack stack, Player player)
	{
		CompoundTag tag = stack.getTag();

		//owner of this keycard reader and the keycard reader the keycard got linked to do not match
		if((ConfigHandler.SERVER.enableTeamOwnership.get() && !PlayerUtils.areOnSameTeam(te.getOwner().getName(), tag.getString("ownerName"))) || !te.getOwner().getUUID().equals(tag.getString("ownerUUID")))
			return new TranslatableComponent("messages.securitycraft:keycardReader.differentOwner");

		//the keycard's signature does not match this keycard reader's
		if(te.getSignature() != tag.getInt("signature"))
			return new TranslatableComponent("messages.securitycraft:keycardReader.wrongSignature");

		int level = ((KeycardItem)stack.getItem()).getLevel();

		//the keycard's level
		if(!te.getAcceptedLevels()[level]) //both are 0 indexed, so it's ok
			return new TranslatableComponent("messages.securitycraft:keycardReader.wrongLevel", level + 1); //level is 0-indexed, so it has to be increased by one to match with the item name

		boolean powered = world.getBlockState(pos).getValue(POWERED);

		if(tag.getBoolean("limited"))
		{
			int uses = tag.getInt("uses");

			if(uses <= 0)
				return new TranslatableComponent("messages.securitycraft:keycardReader.noUses");

			if(!player.isCreative() && !powered) //only remove uses when the keycard reader is not already active
				tag.putInt("uses", --uses);
		}

		if(!powered)
			activate(world, pos, te.getSignalLength());

		return null;
	}

	public static void activate(Level world, BlockPos pos, int signalLength){
		world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(POWERED, true));
		world.updateNeighborsAt(pos, SCContent.KEYCARD_READER.get());
		world.getBlockTicks().scheduleTick(pos, SCContent.KEYCARD_READER.get(), signalLength);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random)
	{
		if(!world.isClientSide){
			world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			world.updateNeighborsAt(pos, SCContent.KEYCARD_READER.get());
		}
	}

	/**
	 * A randomly called display update to be able to add ParticleTypes or other items for display
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand){
		if((state.getValue(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float r = 0.6F + 0.4F;
			float g = Math.max(0.0F, 0.7F - 0.5F);
			float b = Math.max(0.0F, 0.6F - 0.7F);
			Vector3f vec = new Vector3f(r, g, b);

			world.addParticle(new DustParticleOptions(vec, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
	{
		if((blockState.getValue(POWERED)))
			return 15;
		else
			return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite()).setValue(POWERED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeycardReaderBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeKeycardReader, WorldUtils::blockEntityTicker);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
