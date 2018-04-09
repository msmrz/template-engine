package ch.admin.bag.templateengine.web.mock;

import ch.admin.bag.templateengine.model.DocumentFormat;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MockDocumentTemplateSource {

    static final String INVOICE_PATH = "/document-templates/invoice";
    static final String LOREM_PATH = "/document-templates?template=lorem.odt";

    @GetMapping(value = INVOICE_PATH)
    public void downloadInvoiceDocumentTemplate(HttpServletResponse response) throws IOException {
        response.setContentType(DocumentFormat.ODT.mimeContentType);
        IOUtils.copy(new ClassPathResource("/sample-templates/invoice.odt").getInputStream(), response.getOutputStream());
    }

    @GetMapping(value = LOREM_PATH)
    public void downloadLoremDocumentTemplate(HttpServletResponse response) throws IOException {
        response.setContentType(DocumentFormat.ODT.mimeContentType);
        IOUtils.copy(new ClassPathResource("/sample-templates/lorem.odt").getInputStream(), response.getOutputStream());
    }
}
