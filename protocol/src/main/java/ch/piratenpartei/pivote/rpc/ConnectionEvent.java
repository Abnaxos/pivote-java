/*
 * Copyright 2012 Piratenpartei Schweiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.piratenpartei.pivote.rpc;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.Event;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ConnectionEvent extends Event<Connection> {

    private final int byteCount;

    public ConnectionEvent(@NotNull Connection source, int byteCount) {
        super(source);
        this.byteCount = byteCount;
    }

    public int getByteCount() {
        return byteCount;
    }
}
