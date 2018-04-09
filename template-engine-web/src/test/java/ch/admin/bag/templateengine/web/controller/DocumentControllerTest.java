package ch.admin.bag.templateengine.web.controller;

import ch.admin.bag.templateengine.model.DocumentFormat;
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
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.admin.bag.templateengine.web.controller.DocumentControllerTest.DocumentData.INVOICE_DOCUMENT_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocumentControllerTest {

    @LocalServerPort
    private int port;

    @Test
    public void shouldGenerateDocumentBasedOnUrlTemplateAndReturnFilledPdf() {

        final RestTemplate template = new RestTemplate();

        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(DocumentController.PAR_DOCUMENT_DATA, INVOICE_DOCUMENT_DATA);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final String url = DocumentRestRequestBuilder.build((builder) -> builder
                .host("localhost")
                .port(port)
                .templateUrl("http://localhost:" + port + "/document-templates/invoice")
                .documentFormat(DocumentFormat.PDF)
        );

        final HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<byte[]> result = template.exchange(url, HttpMethod.POST, requestEntity, byte[].class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        final HttpHeaders httpHeaders = result.getHeaders();
        assertEquals(DocumentFormat.PDF.mimeContentType, httpHeaders.getContentType() != null ? httpHeaders.getContentType().toString() : null);

        assertNotNull(result.getBody());
//        assertTrue(result.getBody().length > 20000);
    }

    @Test
    public void shouldGenerateDocumentBasedOnTemplatePassedAndReturnFilledPdf() {

        final RestTemplate template = new RestTemplate();

        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(DocumentController.PAR_DOCUMENT_TEMPLATE, new ClassPathResource("/sample-templates/invoice.odt"));
        map.add(DocumentController.PAR_DOCUMENT_DATA, INVOICE_DOCUMENT_DATA);

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
        assertEquals("invoice.pdf", httpHeaders.getContentDisposition().getFilename());
        assertEquals(DocumentFormat.PDF.mimeContentType, httpHeaders.getContentType() != null ? httpHeaders.getContentType().toString() : null);

        assertNotNull(result.getBody());
//        assertTrue(result.getBody().length > 20000);
    }

    @Test
    public void shouldGenerateDocumentBasedOnTemplatePassedAndReturnFilledOdt() {

        final RestTemplate template = new RestTemplate();

        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(DocumentController.PAR_DOCUMENT_TEMPLATE, new ClassPathResource("/sample-templates/invoice.odt"));
        map.add(DocumentController.PAR_DOCUMENT_DATA, INVOICE_DOCUMENT_DATA);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final String url = DocumentRestRequestBuilder.build((builder) -> builder
                .host("localhost")
                .port(port)
        );

        final HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        final ResponseEntity<byte[]> result = template.exchange(url, HttpMethod.POST, requestEntity, byte[].class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        final HttpHeaders httpHeaders = result.getHeaders();
        assertEquals("invoice.odt", httpHeaders.getContentDisposition().getFilename());
        assertEquals(DocumentFormat.ODT.mimeContentType, httpHeaders.getContentType() != null ? httpHeaders.getContentType().toString() : null);

        assertNotNull(result.getBody());
        assertTrue(result.getBody().length > 20000);
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


    class DocumentData {
        static final String INVOICE_DOCUMENT_DATA = "{\n" +
                "  \"order_id\": \"12345678\",\n" +
                "  \"reference\": \"Konkordia\",\n" +
                "  \"customer_to\": \"Elon Musk\",\n" +
                "  \"customer_address\": \"T. Hovorného\",\n" +
                "  \"customer_address_no\": \"1537\",\n" +
                "  \"customer_zip\": \"26305\",\n" +
                "  \"customer_city\": \"Piešťany\",\n" +
                "  \"customer_country\": \"Česká Republika\",\n" +
                "  \"customer_vat_no\": \"CZ7710250713\",\n" +
                "  \"btime\": \"8.3. 2018\",\n" +
                "  \"userName\": \"emusk\",\n" +
                "  \"companyFax\": \"732 054 110\",\n" +
                "  \"companyEmail\": \"ellon.musk@gmail.com\",\n" +
                "  \"company_name\": \"αβ solutions ag\",\n" +
                "  \"items\" : [\n" +
                "    {\n" +
                "      \"amount\": 156,\n" +
                "      \"text\": \"SpaceX\",\n" +
                "      \"rate\": 125,\n" +
                "      \"subtotal\": 7659\n" +
                "    },\n" +
                "    {\n" +
                "      \"amount\": 1,\n" +
                "      \"text\": \"Tesla\",\n" +
                "      \"rate\": 1250,\n" +
                "      \"subtotal\": 1250\n" +
                "    }\n" +
                "  ],\n" +
                "  \"total\": 8909,\n" +
                "  \"vat\": 21,\n" +
                "  \"total_with_vat\": 12456\n" +
                "}";
    }
}
