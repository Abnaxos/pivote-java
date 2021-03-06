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

import ch.piratenpartei.pivote.logback.Logback
import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class PiTest {

    static void main(String... args) {
        Logback.setup()
        int argOffset = 0
        String test = null
        for ( String arg in args ) {
            argOffset++
            if ( arg.startsWith("-") ) {
                def level = Level.toLevel("${arg.substring(1)}", null)
                if ( level == null ) {
                    System.err.println("Unknown log level: ${arg.substring(1)}")
                }
                else {
                    for ( l in ["ch.piratenpartei"] ) {
                        LoggerFactory.getLogger(l).setLevel(level)
                    }
                }
            }
            else {
                test = arg
                break
            }
        }
        if ( test == null ) {
            System.err.println "USAGE: [-logLevel] TestName [testArgs]"
            System.exit(1)
        }
        Class.forName(PiTest.getPackage().getName() + "." + test).main((argOffset < args.length ? args[argOffset..args.length - 1] : []) as String[])
    }

}
