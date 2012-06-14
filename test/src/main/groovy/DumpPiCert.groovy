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

import ch.piratenpartei.pivote.model.crypto.VoterCertificate
import ch.piratenpartei.pivote.serialize.DataInput
import ch.piratenpartei.pivote.serialize.SerializationContext

class DumpPiCert {

    static void main(String... args) {
        if ( args.length > 1 ) {
            println "USAGE: groovy DumpPiCert.groovy /path/to/file.pi-cert"
            System.exit 1
        }

        def filename
        if ( args.length == 0 ) {
            def dir = new File("${System.getProperty("user.home")}/.config/PiVote")
            def picerts = []
            dir.eachFile {f -> println f}
            dir.eachFileMatch(~/.*\.pi-cert/, { f ->
                picerts << f
            })
            def i = 0
            picerts.each { c ->
                println "${i++}: ${c}"
            }
            print ">>> "
            filename = picerts[System.in.newReader().readLine() as Integer]
        }

        SerializationContext context = new SerializationContext();
        DataInput input = new DataInput(new BufferedInputStream(new FileInputStream(filename)), context)
        VoterCertificate cert = input.readObject() as VoterCertificate
        if ( !input.eof() ) {
            println("ERROR: EOF expected!")
        }
        input.close()
        for ( p in cert.metaPropertyValues ) {
            println "${p.name} = ${p.value}"
        }
    }

}
