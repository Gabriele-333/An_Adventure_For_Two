package net.saturnx.aaft.client;/*
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
 * File created on: 09/01/2026
 */

public final class ClientGameState {

    private static boolean waitingForSecondPlayer = false;

    public static void startWaiting() {
        waitingForSecondPlayer = true;
    }

    public static void stopWaiting() {
        waitingForSecondPlayer = false;
    }

    public static boolean isWaiting() {
        return waitingForSecondPlayer;
    }
}