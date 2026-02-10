package net.saturnx.aaft.server;/*
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

public final class SharedTrustState {
    public static final int MIN_TRUST = 0;
    public static final int MAX_TRUST = 127;
    public static final int TRUST_TICKS = 8;
    public static final int TRUST_TICK_COUNT = TRUST_TICKS + 1;

    private static int trust = 64;

    private SharedTrustState() {
    }

    public static int getTrust() {
        return trust;
    }

    public static void increaseTrust(int amount) {
        if (amount <= 0) {
            return;
        }
        trust = Math.min(MAX_TRUST, trust + amount);
    }

    public static void decreaseTrust(int amount) {
        if (amount <= 0) {
            return;
        }
        trust = Math.max(MIN_TRUST, trust - amount);
    }

    public static int getHighlightedTickIndex() {
        int min = MIN_TRUST;
        int max = MAX_TRUST;
        int clamped = Math.max(min, Math.min(max, trust));
        int mid = min + (max - min) / 2;

        int marginValue = 8;
        int effectiveMin = Math.min(max, min + marginValue);
        int effectiveMax = Math.max(min, max - marginValue);
        int effectiveRange = Math.max(1, effectiveMax - effectiveMin);

        int[] tickValues = new int[TRUST_TICK_COUNT];
        for (int i = 0; i < tickValues.length; i++) {
            tickValues[i] = effectiveMin + (effectiveRange * i) / TRUST_TICKS;
        }

        int highlightIndex = 0;
        if (clamped > mid) {
            for (int i = 0; i < tickValues.length; i++) {
                if (tickValues[i] <= clamped) {
                    highlightIndex = i;
                }
            }
        } else if (clamped < mid) {
            highlightIndex = tickValues.length - 1;
            for (int i = 0; i < tickValues.length; i++) {
                if (tickValues[i] >= clamped) {
                    highlightIndex = i;
                    break;
                }
            }
        } else {
            int closest = Integer.MAX_VALUE;
            for (int i = 0; i < tickValues.length; i++) {
                int distance = Math.abs(tickValues[i] - clamped);
                if (distance < closest) {
                    closest = distance;
                    highlightIndex = i;
                }
            }
        }

        return highlightIndex + 1;
    }
}
