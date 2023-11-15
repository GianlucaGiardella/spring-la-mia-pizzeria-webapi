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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaRepository pizzaRepository;

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

        return "pizzas/form";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "pizzas/form";
        }

        Pizza newPizza = null;
        try {
            newPizza = pizzaRepository.save(formPizza);
        } catch (RuntimeException e) {
            bindingResult.addError(new FieldError("book", "isbn", formPizza.getPrice(), false, null, null,
                    "Price must be greaten than 0"));

            return "pizzas/form";
        }

        return "redirect:/pizzas/show/" + newPizza.getId();
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,  Model model) {

        Optional<Pizza> result = pizzaRepository.findById(id);

        if(result.isPresent()) {
            model.addAttribute("pizza", result.get());
            return "/pizzas/form";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found");
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return "/pizzas/form";
        }

        Pizza editPizza = pizzaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        editPizza.setName(formPizza.getName());
        editPizza.setImage_url(formPizza.getImage_url());
        editPizza.setDescription(formPizza.getDescription());
        editPizza.setPrice(formPizza.getPrice());

        Pizza editedPizza = pizzaRepository.save(editPizza);

        return "redirect:/pizzas/show/" + editedPizza.getId();
    }


        @PostMapping("/delete/{id}")
        public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {

        Pizza deletePizza = pizzaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        pizzaRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message",
                "Pizza: " + deletePizza.getName() + " deleted!");

        return "redirect:/pizzas";
    }
}
