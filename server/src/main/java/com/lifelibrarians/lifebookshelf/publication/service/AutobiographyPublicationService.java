package com.lifelibrarians.lifebookshelf.publication.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AutobiographyPublicationService {

    private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyChapterRepository autobiographyChapterRepository;
    private final TemplateEngine templateEngine;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public byte[] generatePdf(Long autobiographyId) throws IOException, DocumentException {
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(() -> new IllegalArgumentException("Autobiography not found"));

        List<AutobiographyChapter> chapters = autobiographyChapterRepository
                .findByAutobiographyIdOrderByCreatedAtAsc(autobiographyId);

        Context context = new Context();
        context.setVariable("autobiography", autobiography);
        context.setVariable("chapters", chapters);

        // 빈 페이지 계산
        int contentPages = chapters.size();
        int totalPages = 1 + contentPages;

        int remainder = totalPages % 4;
        List<Integer> emptyPages = new ArrayList<>();
        if (remainder != 0) {
            int toAdd = 4 - remainder;
            for (int i = 0; i < toAdd; i++) {
                emptyPages.add(i);
            }
        }

        context.setVariable("emptyPages", emptyPages);

        // HTML 렌더링
        String html = templateEngine.process("autobiography-publication", context);

        // PDF 생성
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        // 폰트: ClassPathResource.getFile() 금지 → InputStream 기반 복사
        ClassPathResource fontResource = new ClassPathResource("fonts/NanumGothic.ttf");
        File tempFont = File.createTempFile("NanumGothic", ".ttf");
        try (InputStream is = fontResource.getInputStream();
             FileOutputStream fos = new FileOutputStream(tempFont)) {
            is.transferTo(fos);
        }

        renderer.getFontResolver().addFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    public String uploadPdfToS3(Long autobiographyId, String name) throws IOException, DocumentException {

        byte[] pdfBytes;
        try {
            pdfBytes = generatePdf(autobiographyId);
        } catch (Exception e) {
            log.error("[ERROR] PDF 생성 실패 - autobiographyId={}", autobiographyId, e);
            throw e;
        }

        if (pdfBytes.length == 0) {
            log.error("[ERROR] PDF 바이트 크기가 0입니다 - autobiographyId={}", autobiographyId);
            throw new IllegalStateException("PDF generation failed: empty file");
        }

        String safeName = (name == null || name.isBlank()) ? "autobiography" : name;
        safeName = safeName.replaceAll("[^a-zA-Z0-9가-힣_\\-]", "_");

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "publications/" + safeName + "_" + autobiographyId + "_" + date + ".pdf";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/pdf");
        metadata.setContentLength(pdfBytes.length);

        try {
            amazonS3Client.putObject(new PutObjectRequest(
                    bucket,
                    fileName,
                    new ByteArrayInputStream(pdfBytes),
                    metadata
            ));
        } catch (Exception e) {
            log.error("[ERROR] S3 업로드 실패 - fileName={}, bucket={}", fileName, bucket, e);
            throw e;
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
