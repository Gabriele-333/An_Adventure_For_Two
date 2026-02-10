package net.saturnx.aaft.mixin;/*
 * This file is part of An Adventure For Two.
 * Copyright (c) 2026, SaturnX Studios, All rights reserved.
 *
 * An Adventure For Two is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * An Adventure For Two is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with An Adventure For Two.  If not, see <http://www.gnu.org/licenses/lgpl>.
 *
 * File created on: 10/02/2026
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.saturnx.aaft.AAFT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
    private static final ResourceLocation SPLASHES_LOCATION =
            ResourceLocation.fromNamespaceAndPath(AAFT.MOD_ID, "texts/splashes.txt");
    private static final RandomSource RANDOM = RandomSource.create();

    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void aaft$overrideSplash(CallbackInfoReturnable<SplashRenderer> cir) {
        List<String> splashes = loadSplashes();
        if (splashes.isEmpty()) {
            cir.setReturnValue(null);
            return;
        }

        String text = splashes.get(RANDOM.nextInt(splashes.size()));
        cir.setReturnValue(new SplashRenderer(text));
    }

    private static List<String> loadSplashes() {
        Minecraft mc = Minecraft.getInstance();
        ResourceManager resourceManager = mc.getResourceManager();
        Optional<Resource> resource = resourceManager.getResource(SPLASHES_LOCATION);
        if (resource.isEmpty()) {
            return List.of();
        }

        try (BufferedReader reader = resource.get().openAsReader()) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }
}
