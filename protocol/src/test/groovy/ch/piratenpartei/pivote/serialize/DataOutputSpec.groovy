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


import ch.piratenpartei.pivote.serialize.types.Data
import ch.piratenpartei.pivote.serialize.types.UInt32
import ch.raffael.util.common.logging.LogUtil
import org.joda.time.LocalDateTime
import org.slf4j.Logger
import spock.lang.Shared
import spock.lang.Specification

import static java.lang.System.*

/**
 * Note that this spec assumes that DataInput works correctly. It tests DataOutput by
 * writing data and re-reading it using DataInput.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class DataOutputSpec extends Specification {

    @Shared
    private SerializationContext context = new SerializationContext()
    @Shared
    private Logger log = LogUtil.getLogger(this)

    private ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()
    private DataOutput output = new DataOutput(byteArrayOut, context)
    private DataInput dataInput = null

    private DataInput input() {
        if ( dataInput == null ) {
            dataInput = new DataInput(new ByteArrayInputStream(byteArrayOut.toByteArray()), context)
        }
    }

    def "read/write DateTime"() {
      when:
        def t = new LocalDateTime()
        log.info("DateTime: {}", t)
        output.writeDateTime(t)

      then:
        input().readDateTime() == t
    }

    def "read/write Guid"() {
      when:
        def guid = UUID.randomUUID()
        log.info("GUID: {}", guid)
        output.writeGuid(guid)

      then:
        input().readGuid() == guid
    }

    def "read/write UInt32"() {
      when:
        def val = new UInt32((long)Integer.MAX_VALUE + (long)new Random(nanoTime()).nextInt(Integer.MAX_VALUE) + 1L)
        log.info("UInt32: {}", val)
        output.writeUInt32(val)

      then:
        input().readUInt32() == val
    }

    def "read/write String"() {
      when:
        def str = "The Swiss Pirate Party rocks! \u00E4\u00F6\u00FC\u00DF" // äöüß
        log.info("String: {}", str)
        output.writeString(str)

      then:
        input().readString() == str
    }

    def "read/write CString"() {
      when:
        def str = "The Swiss Pirate Party rocks!"
        log.info("CString: {}", str)
        output.writeCString(str)

      then:
        input().readCString() == str
    }

    def "read/write Data"() {
      when:
        def data = new Data({ ->
            def rnd = new Random(nanoTime())
            def bytes = new byte[rnd.nextInt(250)+250]
            bytes.length.times { i ->
                bytes[i] = rnd.nextInt(256)
                if ( i % 16 == 0 ) {
                    rnd.seed = nanoTime()
                }
            }
            return bytes
        }())
        log.info("Data: {}", data)
        output.writeData(data)

      then:
        input().readData() == data

    }

}
