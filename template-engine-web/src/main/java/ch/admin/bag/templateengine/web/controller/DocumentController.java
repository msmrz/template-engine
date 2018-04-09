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
import ch.admin.bag.templateengine.service.DocumentService;
import ch.admin.bag.templateengine.web.util.FileNameUtils;
import ch.admin.bag.templateengine.web.util.HttpUtils;
import ch.admin.bag.templateengine.web.exception.IllegalRestArgumentException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RestController
public class DocumentController {

    static final String PAR_DOCUMENT_TEMPLATE = "document-template";
    static final String PAR_DOCUMENT_TEMPLATE_URL = "document-template-url";
    static final String PAR_DOCUMENT_FORMAT = "document-format";
    static final String PAR_DOCUMENT_DATA = "document-data";

    private final DocumentService documentService;

    private final ObjectMapper objectMapper;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
        this.objectMapper = new ObjectMapper();
    }

    @RequestMapping(value = "/generate-document", method = RequestMethod.POST)
    @SuppressWarnings("unchecked")
    public ResponseEntity generateDocument(@RequestParam(name = PAR_DOCUMENT_TEMPLATE, required = false) MultipartFile documentTemplate,
                                           @RequestParam(name = PAR_DOCUMENT_TEMPLATE_URL, required = false) String url,
                                           @RequestParam(name = PAR_DOCUMENT_FORMAT, required = false) String documentFormatName,
                                           @RequestParam(name = PAR_DOCUMENT_DATA, required = false) String jsonDocumentData,
                                           HttpServletResponse response) throws IOException {

        if ((documentTemplate == null && isBlank(url)) || (documentTemplate != null && isNotBlank(url))) {
            throw new IllegalRestArgumentException(String.format("Just one of the parameters (%s | %s) is required!", PAR_DOCUMENT_TEMPLATE, PAR_DOCUMENT_TEMPLATE_URL));
        }

        final DocumentFormat documentFormat = DocumentFormat.fromNameIgnoreCase(documentFormatName)
                .orElse(DocumentFormat.ODT);

        final Map<String, Object> documentDataMap = objectMapper.readValue(jsonDocumentData, HashMap.class);

        response.setContentType(documentFormat.mimeContentType);

        if (documentTemplate != null) {
            final String documentFileName = FileNameUtils.replaceOrAppendFileExtension(documentTemplate.getOriginalFilename(), documentFormat.defaultDocumentExtension);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, HttpUtils.createContentDispositionQueryHeader(documentFileName));

            documentService.generateDocument(documentTemplate.getInputStream(), response.getOutputStream(), documentDataMap, documentFormat);
        } else {
            documentService.generateDocument(url, response.getOutputStream(), documentDataMap, documentFormat);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
