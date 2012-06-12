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
package ch.piratenpartei.pivote.serialize;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataOutput implements Closeable {

    private final OutputStream output;
    private final SerializationContext context;

    public DataOutput(OutputStream output, SerializationContext context) {
        this.output = output;
        this.context = context;
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
