package net.geforcemods.securitycraft.blockentities;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class RetinalScannerBlockEntity extends DisguisableBlockEntity implements IViewActivated, ITickingBlockEntity {
	private static GameProfileCache profileCache;
	private static MinecraftSessionService sessionService;
	private static Executor mainThreadExecutor;
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private GameProfile ownerProfile;
	private int viewCooldown = 0;

	public RetinalScannerBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeRetinalScanner, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		checkView(level, pos);
	}

	@Override
	public void onEntityViewed(LivingEntity entity){
		if(!level.isClientSide)
		{
			BlockState state = level.getBlockState(worldPosition);
			if(!state.getValue(RetinalScannerBlock.POWERED) && !EntityUtils.isInvisible(entity)){
				if(!(entity instanceof Player) && !activatedByEntities.get())
					return;

				if(entity instanceof Player player && !getOwner().isOwner(player) && !ModuleUtils.isAllowed(this, entity)) {
					PlayerUtils.sendMessageToPlayer((Player) entity, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner().getName())), ChatFormatting.RED);
					return;
				}

				level.setBlockAndUpdate(worldPosition, state.setValue(RetinalScannerBlock.POWERED, true));
				level.getBlockTicks().scheduleTick(new BlockPos(worldPosition), SCContent.RETINAL_SCANNER.get(), getSignalLength());

				if(entity instanceof Player player && sendMessage.get())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", entity.getName()), ChatFormatting.GREEN);
			}
		}
	}

	@Override
	public int getDefaultViewCooldown() {
		return getSignalLength() + 30;
	}

	@Override
	public int getViewCooldown() {
		return viewCooldown;
	}

	@Override
	public void setViewCooldown(int viewCooldown) {
		this.viewCooldown = viewCooldown;
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities, sendMessage, signalLength };
	}

	public static void setProfileCache(GameProfileCache profileCacheIn) {
		profileCache = profileCacheIn;
	}

	public static void setSessionService(MinecraftSessionService sessionServiceIn) {
		sessionService = sessionServiceIn;
	}

	public static void setMainThreadExecutor(Executor mainThreadExecutorIn) {
		mainThreadExecutor = mainThreadExecutorIn;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);

		if(!StringUtil.isNullOrEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner")) && ownerProfile != null)
		{
			CompoundTag ownerProfileTag = new CompoundTag();
			NbtUtils.writeGameProfile(ownerProfileTag, ownerProfile);
			tag.put("ownerProfile", ownerProfileTag);
		}

		return tag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains("ownerProfile", 10)) {
			setPlayerProfile(NbtUtils.readGameProfile(tag.getCompound("ownerProfile")));
		}
	}

	@Override
	public void onOwnerChanged(BlockState state, Level world, BlockPos pos, Player player) {
		super.onOwnerChanged(state, world, pos, player);
		setPlayerProfile(new GameProfile(null, getOwner().getName()));
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return ownerProfile;
	}

	public void setPlayerProfile(@Nullable GameProfile profile) {
		synchronized (this) {
			ownerProfile = profile;
		}

		updatePlayerProfile();
	}

	public void updatePlayerProfile() {
		if (profileCache == null && ServerLifecycleHooks.getCurrentServer() != null)
			setProfileCache(ServerLifecycleHooks.getCurrentServer().getProfileCache());
		if(sessionService == null && ServerLifecycleHooks.getCurrentServer() != null)
			setSessionService(ServerLifecycleHooks.getCurrentServer().getSessionService());
		if(mainThreadExecutor == null && ServerLifecycleHooks.getCurrentServer() != null)
			setMainThreadExecutor(ServerLifecycleHooks.getCurrentServer());

		updateGameProfile(ownerProfile, profile -> {
			this.ownerProfile = profile;
			this.setChanged();
		});
	}

	private void updateGameProfile(GameProfile input, Consumer<GameProfile> onChanged) {
		if (ConfigHandler.SERVER.retinalScannerFace.get() && input != null && !StringUtil.isNullOrEmpty(input.getName()) && (!input.isComplete() || !input.getProperties().containsKey("textures")) && profileCache != null && sessionService != null) {
			profileCache.getAsync(input.getName(), result -> Util.backgroundExecutor().execute(() -> {
				Util.ifElse(result, gameProfile -> {
					Property textures = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), (Object)null);

					if (textures == null) {
						gameProfile = sessionService.fillProfileProperties(gameProfile, true);
					}

					GameProfile profile = gameProfile;
					mainThreadExecutor.execute(() -> {
						profileCache.add(profile);
						onChanged.accept(profile);
					});
				}, () -> mainThreadExecutor.execute(() -> onChanged.accept(input)));
			}));
		}
		else {
			onChanged.accept(input);
		}
	}
}
