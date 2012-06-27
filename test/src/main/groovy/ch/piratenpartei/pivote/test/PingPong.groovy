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

package ch.piratenpartei.pivote.test

import ch.piratenpartei.pivote.rpc.Connection
import ch.piratenpartei.pivote.serialize.SerializationContext
import com.google.common.net.HostAndPort
import org.slf4j.LoggerFactory

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class PingPong {

    static void main(String... args) {
        if ( args.length != 1 ) {
            System.err.println "No host specified"
        }
        def log = LoggerFactory.getLogger(PingPong)
        SerializationContext serCtx = new SerializationContext()
        Connection connection = new Connection(serCtx, HostAndPort.fromString(args[0]))
        connection.connect()
        Runtime.getRuntime().addShutdownHook({
            connection.disconnect()
        })
        println "PRESS ENTER TO DISCONNECT"
        System.in.newReader().readLine()
        System.exit(1)
    }

}
