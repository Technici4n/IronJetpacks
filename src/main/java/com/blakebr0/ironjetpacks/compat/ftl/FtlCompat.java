package com.blakebr0.ironjetpacks.compat.ftl;

import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.base.SimpleItemEnergyIo;

public class FtlCompat {
    public static void init() {
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            double maxInput = jetpack.item.getMaxInput(null);
            double maxOutput = jetpack.item.getMaxOutput(null);
            EnergyApi.ITEM.register(SimpleItemEnergyIo.getProvider(jetpack.capacity, maxInput, maxOutput), jetpack.item);
        }
    }
}
