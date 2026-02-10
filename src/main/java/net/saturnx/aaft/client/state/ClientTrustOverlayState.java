package net.saturnx.aaft.client.state;/*
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

public final class ClientTrustOverlayState {
    public static final int MIN_TRUST = 0;
    public static final int MAX_TRUST = 127;

    private static int trust = 64;

    private ClientTrustOverlayState() {
    }

    public static int getTrust() {
        return trust;
    }

    public static void setTrust(int value) {
        trust = Math.max(MIN_TRUST, Math.min(MAX_TRUST, value));
    }
}
