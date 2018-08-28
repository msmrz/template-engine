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

package ch.admin.bag.templateengine.web.controller;

import ch.admin.bag.templateengine.model.DocumentFormat;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicImageTest {

    @LocalServerPort
    private int port;

    @Test
    public void shouldGenerateDocumentBasedOnTemplatePassedAndReturnFilledPdf() {

        final RestTemplate template = new RestTemplate();

        String jsonData = null;
        try {
            File f = ResourceUtils.getFile("classpath:sample-templates/report-with-qr-code.json");
            jsonData = FileUtils.readFileToString(f, Charset.defaultCharset());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(DocumentController.PAR_DOCUMENT_TEMPLATE, new ClassPathResource("/sample-templates/report-with-qr-code.docx"));
        map.add(DocumentController.PAR_DOCUMENT_DATA, jsonData);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final String url = DocumentRestRequestBuilder.build((builder) -> builder
                .host("localhost")
                .port(port)
                .documentFormat(DocumentFormat.PDF)
        );

        final HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        final ResponseEntity<byte[]> result = template.exchange(url, HttpMethod.POST, requestEntity, byte[].class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        final HttpHeaders httpHeaders = result.getHeaders();
        assertEquals("report-with-qr-code.pdf", httpHeaders.getContentDisposition().getFilename());
        assertEquals(DocumentFormat.PDF.mimeContentType, httpHeaders.getContentType().toString());

        assertNotNull(result.getBody());

//        assertTrue(result.getBody().length > 20000);
    }


    private static class DocumentRestRequestBuilder {

        private int port = -1;

        private String scheme = "http";

        private String host;

        private DocumentFormat documentFormat;

        private String templateUrl;


        private DocumentRestRequestBuilder() {
        }

        DocumentRestRequestBuilder host(String host) {
            this.host = host;
            return this;
        }

        DocumentRestRequestBuilder port(int port) {
            this.port = port;
            return this;
        }

        DocumentRestRequestBuilder templateUrl(String templateUrl) {
            this.templateUrl = templateUrl;
            return this;
        }

        DocumentRestRequestBuilder documentFormat(DocumentFormat documentFormat) {
            this.documentFormat = documentFormat;
            return this;
        }

        static String build(final Consumer<DocumentRestRequestBuilder> block) {
            final DocumentRestRequestBuilder builder = new DocumentRestRequestBuilder();
            block.accept(builder);

            Map<String, String> paramsMap = new HashMap<>();
            if (builder.documentFormat != null) {
                paramsMap.put(DocumentController.PAR_DOCUMENT_FORMAT, builder.documentFormat.name().toLowerCase());
            }

            if (builder.templateUrl != null) {
                paramsMap.put(DocumentController.PAR_DOCUMENT_TEMPLATE_URL, builder.templateUrl);
            }

            String search = paramsMap.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));


            StringBuilder sb = new StringBuilder(builder.scheme + "://").append(builder.host);

            if (builder.port > 0) {
                sb.append(":").append(builder.port);
            }
            sb.append("/generate-document");

            if (!paramsMap.isEmpty()) {
                sb.append("?").append(search);
            }

            return sb.toString();
        }
    }

}
