package com.experis.course.springlamiapizzeriacrud.controller;

import com.experis.course.springlamiapizzeriacrud.model.Ingredient;
import com.experis.course.springlamiapizzeriacrud.model.Pizza;
import com.experis.course.springlamiapizzeriacrud.repository.IngredientRepository;
import com.experis.course.springlamiapizzeriacrud.repository.PizzaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {
    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    PizzaRepository pizzaRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("ingredientList", ingredientRepository.findByOrderByName());

        return "ingredients/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("ingredient", new Ingredient());

        return "ingredients/form";
    }

    @PostMapping("/create")
    public String store(
            @Valid @ModelAttribute("ingredient") Ingredient formIngredient,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredientList", ingredientRepository.findByOrderByName());

            return "ingredients/form";
        }

        try {
            if (ingredientRepository.existsByName(formIngredient.getName())) {
                throw new RuntimeException("Ingredient with name: " + formIngredient.getName() + " already exists!");
            }

            formIngredient.setName(formIngredient.getName().toLowerCase());

            ingredientRepository.save(formIngredient);

            return "redirect:/ingredients";

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model
    ) {
        Optional<Ingredient> result = ingredientRepository.findById(id);

        if (result.isPresent()) {
            model.addAttribute("ingredient", result.get());

            return "/ingredients/form";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient with id " + id + " not found");
        }
    }

    @PostMapping("/edit/{id}")
    public String store(
            @PathVariable Integer id,
            @Valid @ModelAttribute("ingredient") Ingredient formIngredient,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "/ingredients/form";
        }

        Ingredient editIngredient = ingredientRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        editIngredient.setName(formIngredient.getName());

        Ingredient editedIngredient = ingredientRepository.save(editIngredient);

        redirectAttributes.addFlashAttribute("message",
                "Ingredient: " + editedIngredient.getName() + " modified!");

        return "redirect:/ingredients";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes
    ) {

        Ingredient deleteIngredient = ingredientRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        for (Pizza pizza : pizzaRepository.findAll()) {
            if (pizza.getIngredients().contains(deleteIngredient)) {
                pizza.getIngredients().remove(deleteIngredient);
                pizzaRepository.save(pizza);
            }
        }

        ingredientRepository.delete(deleteIngredient);

        redirectAttributes.addFlashAttribute("message",
                "Ingredient: " + deleteIngredient.getName() + " deleted!");

        return "redirect:/ingredients";
    }
}
