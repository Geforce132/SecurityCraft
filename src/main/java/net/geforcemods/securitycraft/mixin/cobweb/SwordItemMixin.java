package net.geforcemods.securitycraft.mixin.cobweb;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCTags;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.Tool;

/**
 * Allows reinforced cobweb to be mined faster using swords
 */
@Mixin(SwordItem.class)
public class SwordItemMixin {
	@SuppressWarnings("rawtypes")
	@WrapOperation(method = "createToolProperties", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
	private static List securitycraft$addReinforcedCobwebToSwordProperties(Object minesAndDropsCobweb, Object overrideSwordEfficientSpeed, Operation<List> original) {
		List list = new ArrayList();

		list.add(Tool.Rule.minesAndDrops(SCTags.Blocks.REINFORCED_COBWEB, 15.0F));
		list.addAll(original.call(minesAndDropsCobweb, overrideSwordEfficientSpeed));
		return list;
	}
}
