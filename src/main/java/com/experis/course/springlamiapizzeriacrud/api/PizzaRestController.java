package com.experis.course.springlamiapizzeriacrud.api;

import com.experis.course.springlamiapizzeriacrud.model.Pizza;
import com.experis.course.springlamiapizzeriacrud.repository.PizzaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/pizzas")
@CrossOrigin
public class PizzaRestController {
    @Autowired
    PizzaRepository pizzaRepository;

    @GetMapping
    public List<Pizza> index(@RequestParam Optional<String> search) {
        if (search.isPresent()) {
            return pizzaRepository.findByNameContainingIgnoreCase(search.get());
        } else {
            return pizzaRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    public Pizza details(@PathVariable Integer id) {
        return pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Pizza create(@Valid @RequestBody Pizza pizza) {
        pizza.setId(null);

        try {
            return pizzaRepository.save(pizza);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Pizza update(
            @PathVariable Integer id,
            @Valid @RequestBody Pizza pizza
    ) {
        pizza.setId(id);

        Pizza editPizza = pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        editPizza.setName(pizza.getName());
        editPizza.setImage(pizza.getImage());
        editPizza.setPrice(pizza.getPrice());
        editPizza.setDescription(pizza.getDescription());
        editPizza.setIngredients(pizza.getIngredients());


        return pizzaRepository.save(editPizza);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        pizzaRepository.deleteById(id);
    }

    @GetMapping("/page")
    public Page<Pizza> pagedIndex(
            @RequestParam(name = "size", defaultValue = "4") Integer size,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {

        return pizzaRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/page/v2")
    public Page<Pizza> pagedIndexV2(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        return pizzaRepository.findAll(pageable);
    }
}
