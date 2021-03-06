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
package ch.piratenpartei.pivote.ui.cert;

import javax.swing.ImageIcon;

import ch.piratenpartei.pivote.PiVoteResources;

import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.Forward;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Resources extends PiVoteResources {

    @Default("New Certificate...")
    String newCertificate();

    @Default("cert_add.png")
    ImageIcon newCertificateIcon();

    @Default("Delete Certificate")
    String deleteCertificate();

    @Default("cert_delete.png")
    ImageIcon deleteCertificateIcon();

    @Default("Set Certificate Password")
    String setCertificatePassword();

    @Default("cert_pass.png")
    ImageIcon setCertificatePasswordIcon();

    @Default("Export Certificate...")
    String exportCertificate();

    @Default("export.png")
    ImageIcon exportCertificateIcon();

    @Default("Import Certificate...")
    String importCertificate();

    @Default("import.png")
    ImageIcon importCertificateIcon();

    @Default("waiting.png")
    ImageIcon certificateWaitingForApproval();

    @Forward(method = "okIcon")
    ImageIcon certificateApproved();
}
