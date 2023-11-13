package com.experis.course.springlamiapizzeriacrud.controller;

import com.experis.course.springlamiapizzeriacrud.model.Pizza;
import com.experis.course.springlamiapizzeriacrud.repository.PizzaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    PizzaRepository pizzaRepository;

    @GetMapping
    public String index(@RequestParam Optional<String> search, Model model) {

        List<Pizza> pizzaList;

        if (search.isPresent()) {
            pizzaList = pizzaRepository.findByNameContainingIgnoreCase(search.get());
        } else {
            pizzaList = pizzaRepository.findAll();
        }

        model.addAttribute("pizzaList", pizzaList);

        return "pizzas/list";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable Integer id, Model model) {

        Optional<Pizza> result = pizzaRepository.findById(id);

        if (result.isPresent()) {
            model.addAttribute("pizza", result.get());

            return "pizzas/show";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found!");
        }
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "pizzas/create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "pizzas/create";
        }

        Pizza newPizza = null;
        try {
            newPizza = pizzaRepository.save(formPizza);
        } catch (RuntimeException e) {
            bindingResult.addError(new FieldError("book", "isbn", formPizza.getPrice(), false, null, null,
                    "Price must be greaten than 0"));

            return "pizzas/create";
        }

        return "redirect:/pizzas/show/" + newPizza.getId();
    }
}
