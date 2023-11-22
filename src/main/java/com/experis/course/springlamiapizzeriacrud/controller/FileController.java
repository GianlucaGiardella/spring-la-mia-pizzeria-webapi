package com.experis.course.springlamiapizzeriacrud.controller;

import com.experis.course.springlamiapizzeriacrud.model.Pizza;
import com.experis.course.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/files")
public class FileController {
    @Autowired
    PizzaRepository pizzaRepository;


    @GetMapping("/image/{pizzaId}")
    public ResponseEntity<byte[]> serveCover(@PathVariable Integer pizzaId) {
        try {
            Pizza pizza = pizzaRepository
                    .findById(pizzaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            byte[] dbImage = pizza.getImage();

            if (dbImage != null && dbImage.length > 0) {
                MediaType mediaType = MediaType.IMAGE_JPEG;

                return ResponseEntity.ok().contentType(mediaType).body(dbImage);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
