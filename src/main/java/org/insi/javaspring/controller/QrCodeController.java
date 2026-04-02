package org.insi.javaspring.controller;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.service.QrCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping(value = "/qr/citoyen/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateCitoyenQr(@PathVariable Long id) {

        String qrText = baseUrl + "/verify/citoyen/" + id;

        byte[] qrImage = qrCodeService.generateQrCode(qrText, 220, 220);
        return ResponseEntity.ok(qrImage);
    }
}