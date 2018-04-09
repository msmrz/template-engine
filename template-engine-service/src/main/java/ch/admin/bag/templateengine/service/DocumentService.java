/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package ch.admin.bag.templateengine.service;

import ch.admin.bag.templateengine.model.DocumentFormat;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Service
public class DocumentService {

    final private RestTemplate restTemplate;

    public DocumentService() {
        this.restTemplate = new RestTemplate();
    }

    public void generateDocument(String templateUrl, OutputStream out, Map<String, Object> documentDataMap, DocumentFormat documentFormat) {
        try {
            byte[] documentTemplate = restTemplate.getForObject(templateUrl, byte[].class);

            if (documentTemplate != null) {
                generateDocument(new ByteArrayInputStream(documentTemplate), out, documentDataMap, documentFormat);
            } else {
                throw new DocumentServiceException(String.format("Document template is empty!. [url=%s]", templateUrl));
            }
        } catch (RestClientException e) {
            throw new DocumentServiceException(String.format("Unable to read document template. [url=%s]", templateUrl), e);
        }
    }

    public void generateDocument(InputStream in, OutputStream out, Map<String, Object> documentDataMap, DocumentFormat documentFormat) {
        try {
            IXDocReport doc = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            IContext context = doc.createContext();
            context.putMap(documentDataMap);

            if (DocumentFormat.PDF == documentFormat) {
                Options options = Options.getTo(ConverterTypeTo.PDF).via(
                        ConverterTypeVia.ODFDOM);
                doc.convert(context, options, out);
            } else {
                doc.process(context, out);
            }
        } catch (IOException | XDocReportException e) {
            throw new DocumentServiceException("An error occurred when generating document.", e);
        }
    }
}
