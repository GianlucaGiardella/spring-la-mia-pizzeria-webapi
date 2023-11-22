package com.experis.course.springlamiapizzeriacrud.controller;

import com.experis.course.springlamiapizzeriacrud.dto.PizzaDto;
import com.experis.course.springlamiapizzeriacrud.model.Pizza;
import com.experis.course.springlamiapizzeriacrud.repository.IngredientRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping
    public String index(
            @RequestParam Optional<String> search,
            Model model
    ) {

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
    public String show(
            @PathVariable Integer id,
            Model model
    ) {

        Pizza result = pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("pizza", result);

        return "pizzas/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new PizzaDto());

        model.addAttribute("ingredientList", ingredientRepository.findByOrderByName());

        return "pizzas/form";
    }

    @PostMapping("/create")
    public String store(
            @Valid @ModelAttribute("pizza") PizzaDto formPizza,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredientList", ingredientRepository.findByOrderByName());
            return "pizzas/form";
        }

        Pizza storePizza = null;

        try {
            Pizza newPizza = new Pizza();
            newPizza.setId(formPizza.getId());
            newPizza.setName(formPizza.getName());
            newPizza.setPrice(formPizza.getPrice());
            newPizza.setDescription(formPizza.getDescription());
            newPizza.setIngredients(formPizza.getIngredients());

            if (formPizza.getImageFile() != null && !formPizza.getImageFile().isEmpty()) {
                byte[] bytes = formPizza.getImageFile().getBytes();
                newPizza.setImage(bytes);
            }

            storePizza = pizzaRepository.save(newPizza);

        } catch (RuntimeException e) {
            bindingResult.addError(new FieldError(
                    "pizza",
                    "price",
                    formPizza.getPrice(),
                    false,
                    null,
                    null,
                    "Price must be greaten than 0")
            );

            bindingResult.addError(new FieldError(
                    "pizza",
                    "name",
                    formPizza.getName(),
                    false,
                    null,
                    null,
                    "Insert a name")
            );

            return "pizzas/form";

        } catch (IOException e) {
            bindingResult.addError(new FieldError(
                    "pizza",
                    "image",
                    null,
                    false,
                    null,
                    null,
                    "Unable to save file")
            );
        }

        return "redirect:/pizzas/show/" + storePizza.getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model
    ) {
        Pizza dbPizza = pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        PizzaDto pizzaDto = new PizzaDto();
        pizzaDto.setId(dbPizza.getId());
        pizzaDto.setName(dbPizza.getName());
        pizzaDto.setPrice(dbPizza.getPrice());
        pizzaDto.setDescription(dbPizza.getDescription());
        pizzaDto.setIngredients(dbPizza.getIngredients());

        model.addAttribute("pizza", pizzaDto);
        model.addAttribute("ingredientList", ingredientRepository.findByOrderByName());

        return "/pizzas/form";
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("pizza") PizzaDto formPizza,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredientList", ingredientRepository.findByOrderByName());

            return "/pizzas/form";
        }

        Pizza dbPizza = pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            dbPizza.setId(formPizza.getId());
            dbPizza.setName(formPizza.getName());
            dbPizza.setPrice(formPizza.getPrice());
            dbPizza.setDescription(formPizza.getDescription());
            dbPizza.setIngredients(formPizza.getIngredients());

            if (formPizza.getImageFile() != null && !formPizza.getImageFile().isEmpty()) {
                byte[] bytes = formPizza.getImageFile().getBytes();
                dbPizza.setImage(bytes);
            }
        } catch (IOException e) {
            bindingResult.addError(new FieldError(
                    "pizza",
                    "image",
                    null,
                    false,
                    null,
                    null,
                    "Unable to save file")
            );
        }


       /* if (editPizza.getImage() != null && editPizza.getImage().length > 0) {
            editPizza.setImage(formPizza);
        }*/

        Pizza editedPizza = pizzaRepository.save(dbPizza);

        return "redirect:/pizzas/show/" + editedPizza.getId();
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes
    ) {

        Pizza deletePizza = pizzaRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        pizzaRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message",
                "Pizza: " + deletePizza.getName() + " deleted!");

        return "redirect:/pizzas";
    }
}
