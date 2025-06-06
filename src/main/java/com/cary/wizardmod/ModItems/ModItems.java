package com.cary.wizardmod.ModItems;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import com.cary.wizardmod.WizardsMod;

import java.util.function.Function;

public class ModItems {

    public static final Item fireball_staff = registerItem("fireball_staff", FireballStaff::new, new Item.Settings().maxCount(1));
    public static final Item magma_core = registerItem("magma_core", Item::new, new Item.Settings().maxCount(16));
    public static final Item staff_handle = registerItem("staff_handle", Item::new, new Item.Settings().maxCount(16));

    public static Item registerItem(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registerKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WizardsMod.MOD_ID, name));
        return Items.register(registerKey, factory, settings);
    }

    private static void customWeapons(FabricItemGroupEntries entries) {
        entries.add(fireball_staff);
    }

    private static void customIngredient(FabricItemGroupEntries entries) {
        entries.add(magma_core);
        entries.add(staff_handle);
    }

    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::customWeapons);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::customIngredient);
    }
}