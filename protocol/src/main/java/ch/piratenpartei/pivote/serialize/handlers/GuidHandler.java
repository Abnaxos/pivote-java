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
package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;
import java.util.UUID;

import ch.piratenpartei.pivote.serialize.DataIO;
import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.Handler;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class GuidHandler implements Handler {

    @Override
    public Object read(DataInput input) throws IOException {
        return input.readGuid();
    }

    @Override
    public void write(DataOutput output, Object value) throws IOException {
        output.writeGuid(DataIO.check(UUID.class, value));
    }
}
