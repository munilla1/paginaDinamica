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

import com.model.Producto;
import com.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/descargar")
public class DescargaController {

    private final ProductoRepository productoRepository;

    public DescargaController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public ResponseEntity<Resource> descargar(@RequestParam("productoId") Long productoId, HttpSession session) {
        if (!isPagoValido(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // 🔹 Obtener el producto desde la base de datos
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // 🔐 Invalida la sesión para evitar descargas repetidas
        session.removeAttribute("paymentIntentId");
        session.removeAttribute("productoId");

        return servirArchivo(producto.getRutaArchivo()); // 🔹 Ruta del archivo del producto
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}



