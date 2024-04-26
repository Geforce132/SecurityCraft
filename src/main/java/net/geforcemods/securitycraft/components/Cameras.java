package net.geforcemods.securitycraft.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record Cameras(List<Camera> cameras) implements TooltipProvider {
	public static final int MAX_CAMERAS = 30;
	public static final Cameras EMPTY = new Cameras(List.of());
	//@formatter:off
	public static final Codec<Cameras> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Camera.CODEC.sizeLimitedListOf(MAX_CAMERAS).fieldOf("cameras").forGetter(Cameras::cameras))
			.apply(instance, Cameras::new));
	public static final StreamCodec<ByteBuf, Cameras> STREAM_CODEC = StreamCodec.composite(
			Camera.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_CAMERAS)), Cameras::cameras,
			Cameras::new);
	//@formatter:on

	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> lineAdder, TooltipFlag flag) {
		lineAdder.accept(Utils.localize("tooltip.securitycraft:cameraMonitor", cameras.size() + "/" + MAX_CAMERAS).setStyle(Utils.GRAY_STYLE));
	}

	public int size() {
		return cameras.size();
	}

	public boolean hasCameraAdded() {
		return !cameras.isEmpty();
	}

	public boolean isCameraAdded(GlobalPos view) {
		return cameras.stream().map(Camera::globalPos).anyMatch(view::equals);
	}

	public List<Camera> filledOrderedList() {
		List<Camera> sortedCameras = new ArrayList<>(cameras);
		List<Camera> toReturn = new ArrayList<>();
		int indexToCheck = 0;

		sortedCameras.sort(Comparator.comparing(c -> c.index));

		for (int i = 1; i <= 30; i++) {
			if (indexToCheck >= sortedCameras.size())
				toReturn.add(null);
			else {
				Camera existingCamera = sortedCameras.get(indexToCheck);

				if (existingCamera.index() != i)
					toReturn.add(null);
				else {
					toReturn.add(existingCamera);
					indexToCheck++;
				}
			}
		}

		return toReturn;
	}

	public static boolean add(ItemStack stack, Cameras cameras, GlobalPos view) {
		if (cameras != null && cameras.cameras.size() < MAX_CAMERAS) {
			List<Camera> sortedCameras = cameras.cameras.stream().sorted(Comparator.comparing(c -> c.index)).toList();
			int nextFreeIndex = 0;

			for (int i = 1; i <= MAX_CAMERAS; i++) {
				if (i > sortedCameras.size() || sortedCameras.get(i - 1).index != i) {
					nextFreeIndex = i;
					break;
				}
			}

			if (nextFreeIndex > 0) {
				List<Camera> newCameraList = new ArrayList<>(cameras.cameras);
				Cameras newCameras;

				newCameraList.add(new Camera(nextFreeIndex, view));
				newCameras = new Cameras(newCameraList);
				stack.set(SCContent.CAMERAS, newCameras);
				return true;
			}
		}

		return false;
	}

	public static boolean remove(ItemStack stack, Cameras cameras, GlobalPos pos) {
		if (cameras != null && cameras.hasCameraAdded()) {
			List<Camera> newCameraList = new ArrayList<>(cameras.cameras);

			newCameraList.removeIf(camera -> camera.globalPos.equals(pos));

			if (newCameraList.size() != cameras.cameras.size()) {
				stack.set(SCContent.CAMERAS, new Cameras(newCameraList));
				return true;
			}
		}

		return false;
	}

	public record Camera(int index, GlobalPos globalPos) {
		//@formatter:off
		public static final Codec<Camera> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("index").forGetter(Camera::index),
						GlobalPos.CODEC.fieldOf("global_pos").forGetter(Camera::globalPos))
				.apply(instance, Camera::new));
		public static final StreamCodec<ByteBuf, Camera> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Camera::index,
				GlobalPos.STREAM_CODEC, Camera::globalPos,
				Camera::new);
		//@formatter:on
	}
}
