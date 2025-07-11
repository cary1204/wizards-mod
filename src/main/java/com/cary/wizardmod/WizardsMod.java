package com.cary.wizardmod;

import com.cary.wizardmod.ModItems.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WizardsMod implements ModInitializer {
	public static final String MOD_ID = "wizards-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
	}
}