package net.geforcemods.securitycraft.compat.distanthorizons;

import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;

import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;

public class DistantHorizonsCompat extends DhApiBeforeRenderEvent {
	public static void registerEvent() {
		DhApiEventRegister.on(DhApiBeforeRenderEvent.class, new DistantHorizonsCompat());
	}

	@Override
	public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> param) {
		if (FrameFeedHandler.isCapturingCamera())
			param.cancelEvent(); //Do not render Distant Horizon chunks within frame feeds
	}
}
