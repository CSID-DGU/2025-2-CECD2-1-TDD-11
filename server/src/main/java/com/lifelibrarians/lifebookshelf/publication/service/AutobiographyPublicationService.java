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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        // 빈 페이지 계산하기
        int contentPages = chapters.size();
        int totalPages = 1 + contentPages; // 표지 포함

        int remainder = totalPages % 4;

        List<Integer> emptyPages = new ArrayList<>();
        if (remainder != 0) {
            int toAdd = 4 - remainder;
            for (int i = 0; i < toAdd; i++) {
                emptyPages.add(i);
            }
        }

        context.setVariable("emptyPages", emptyPages);

        String html = templateEngine.process("autobiography-publication", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        
        ClassPathResource fontResource = new ClassPathResource("fonts/NanumGothic.ttf");
        String fontPath = fontResource.getFile().getAbsolutePath();
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    public String uploadPdfToS3(Long autobiographyId, String name) throws IOException, DocumentException {
        byte[] pdfBytes = generatePdf(autobiographyId);
        
        String safeName = (name == null || name.isBlank()) ? "autobiography" : name;
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "publications/" + safeName + "_" + autobiographyId + "_" + date + ".pdf";
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/pdf");
        metadata.setContentLength(pdfBytes.length);
        
        amazonS3Client.putObject(new PutObjectRequest(
                bucket, 
                fileName, 
                new ByteArrayInputStream(pdfBytes), 
                metadata
        ));
        
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
