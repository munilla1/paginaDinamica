package com.controladores;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/descargar")
public class DescargaController {

    private final Map<Integer, String> archivosProductos = Map.of(
        1, "src/main/resources/static/pdfs/infografiaSpring.jpg",
        2, "src/main/resources/static/pdfs/IA.jpg",
        3, "src/main/resources/static/pdfs/spring.jpg"
    );

    @GetMapping
    public ResponseEntity<Resource> descargar(@RequestParam("productoId") int productoId, HttpSession session) {
        if (!isPagoValido(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // 🔐 Invalida la sesión para evitar descargas repetidas
        session.removeAttribute("paymentIntentId");
        session.removeAttribute("productoId");

        return servirArchivo(archivosProductos.get(productoId));
    }


    private boolean isPagoValido(HttpSession session) {
        return session.getAttribute("paymentIntentId") != null;
    }


    private ResponseEntity<Resource> servirArchivo(String filePathStr) {
        if (filePathStr == null) return ResponseEntity.notFound().build();

        try {
            Path filePath = Paths.get(filePathStr);
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filePath.getFileName())
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}


