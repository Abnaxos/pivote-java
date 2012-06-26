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
package ch.piratenpartei.pivote;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;

import ch.piratenpartei.pivote.model.crypto.Certificate;
import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import ch.raffael.util.common.UnreachableCodeException;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class FileSystemStorage implements Storage {

    public static final String PROPERTY_STORAGE_DIR = "ch.piratenpartei.pivote.storageDir";

    public static final String CERT_FILE_EXTENSION = ".pi-cert";

    private static final FileFilter PI_CERT_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().endsWith(".pi-cert");
        }
    };

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final SerializationContext serializationContext;
    private final File directory;
    private final Map<UUID, Certificate> certificates = Maps.newHashMap();

    private FileSystemStorage(SerializationContext serializationContext, File directory) {
        this.serializationContext = serializationContext;
        this.directory = directory;
    }

    private synchronized void init() {
        if ( directory.mkdirs() ) {
            log.info("Created storage directory {}", directory);
        }
        if ( !directory.isDirectory() ) {
            log.error("Storage directory {} not found", directory);
        }
        else {
            log.debug("Using storage directory {}", directory);
        }
        for ( File piCertFile : directory.listFiles(PI_CERT_FILTER) ) {
            log.debug("Loading certificate {}", piCertFile);
            DataInput input = null;
            try {
                input = new DataInput(new BufferedInputStream(new FileInputStream(piCertFile)), serializationContext);
                Object cert = input.readObject();
                if ( cert instanceof Certificate ) {
                    putCertificate((Certificate)cert);
                }
                else {
                    log.error("File {} did not contain a certificate but {}", piCertFile, cert);
                }
            }
            catch ( IOException e ) {
                log.error("Error loading certificate file {}", piCertFile, e);
            }
            finally {
                if ( input != null ) {
                    try {
                        input.close();
                    }
                    catch ( Exception e ) {
                        log.error("Error closing certificate file " + piCertFile, e);
                    }
                }
            }
        }
    }

    public static FileSystemStorage newInstance(SerializationContext serializationContext) {
        return newInstance(serializationContext, null);
    }

    public static FileSystemStorage newInstance(SerializationContext serializationContext, String directory) {
        if ( directory == null ) {
            directory = System.getProperty(PROPERTY_STORAGE_DIR);
            if ( directory == null ) {
                switch ( OS.current() ) {
                    case WINDOWS:
                        // FIXME: %APPDATA%?
                        directory = Joiner.on(File.separator).join(System.getProperty("user.home"), "PiVote");
                        break;

                    case MAC:
                        // FIXME: where is it on Mac?
                    case UNIX: {
                        directory = System.getenv("XDG_CONFIG_HOME");
                        if ( directory == null || directory.isEmpty() ) {
                            directory = Joiner.on(File.separator).join(System.getProperty("user.home"), ".config", "PiVote");
                        }
                        else {
                            directory = Joiner.on(File.separator).join(directory, "PiVote");
                        }
                        break;

                    }
                    default:
                        throw new UnreachableCodeException();
                }
            }
        }
        FileSystemStorage fss = new FileSystemStorage(serializationContext, new File(directory));
        fss.init();
        return fss;
    }

    @Override
    public synchronized Map<UUID, Certificate> getCertificates() {
        return ImmutableMap.copyOf(certificates);
    }

    @Override
    public synchronized void putCertificate(Certificate certificate) {
        certificates.put(certificate.getId(), certificate);
    }

}
