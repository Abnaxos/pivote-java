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

import ch.piratenpartei.pivote.serialize.DataIO;
import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.types.LangString;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class LangStringHandler implements Handler {

    public static final LangString.Language[] LANGUAGES = LangString.Language.values();

    @Override
    public Object read(DataInput input) throws IOException {
        LangString langString = new LangString();
        int count = input.readUInt32().intValue();
        for ( int i = 0; i < count; i++ ) {
            langString.set(input.readEnum(LangString.Language.class), input.readString());
        }
        return langString;
    }

    @Override
    public void write(DataOutput output, Object value) throws IOException {
        LangString langstr = DataIO.check(LangString.class, value);
        int count = 0;
        for ( LangString.Language lang : LANGUAGES ) {
            if ( langstr.get(lang) != null ) {
                count++;
            }
        }
        output.writeInt32(count);
        for ( LangString.Language lang : LANGUAGES ) {
            String str = langstr.get(lang);
            if ( str != null ) {
                output.writeEnum(lang);
                output.writeString(str);
            }
        }
    }
}
