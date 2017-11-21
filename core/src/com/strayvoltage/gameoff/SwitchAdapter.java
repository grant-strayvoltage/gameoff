package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class SwitchAdapter {
	
	Array<SwitchHandler> targets;
	
	public SwitchAdapter() {
		targets = new Array<SwitchHandler>();
	}
	
	public void addTarget(SwitchHandler target) {
		targets.add(target);
	}
	
	public void removeTarget(SwitchHandler target) {
		targets.removeValue(target, true);
	}
	
	public void trigger(final Switch source) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				for(SwitchHandler h : targets)
					h.handleSwitch(source);
			}
		});
	}
	
	

}
