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
package ch.piratenpartei.pivote.model.crypto;

import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;
import com.google.common.base.Objects;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize(@Field(name = "name", type = "enum"))
public abstract class CertificateAttribute extends AbstractPiVoteSerializable {

    private Name name;
    private Object value;

    @Override
    protected void toString(Objects.ToStringHelper toString) {
        super.toString(toString);
        toString.add(String.valueOf(getName()), value);
    }

    public Name getName() {
        return this.name;
    }

    public void setName(Name name) {
        observable.firePropertyChange("name", this.name, this.name = name);
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        observable.firePropertyChange("value", this.value, this.value = value);
    }

    public static enum Name {
        NONE, GROUP_ID, LANGUAGE
    }

}
