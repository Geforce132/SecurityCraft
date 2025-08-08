package net.geforcemods.securitycraft.mixin.reinforced;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;

/**
 * Allows reinforced cobweb to be mined faster using swords
 */
@Mixin(ToolMaterial.class)
public class ToolMaterialMixin {
	@SuppressWarnings({"rawtypes", "unchecked"})
	@WrapOperation(method = "applySwordProperties", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
	private static List securitycraft$addReinforcedCobwebToSwordProperties(Object minesAndDropsCobweb, Object overrideSwordInstantlyMinesSpeed, Object overrideSwordEfficientSpeed, Operation<List> original) {
		List list = new ArrayList();

		list.add(Tool.Rule.minesAndDrops(HolderSet.direct(SCContent.REINFORCED_COBWEB.getDelegate()), 15.0F));
		list.addAll(original.call(minesAndDropsCobweb, overrideSwordInstantlyMinesSpeed, overrideSwordEfficientSpeed));
		return list;
	}
}
