package com.experis.course.springlamiapizzeriacrud.controller;

import com.experis.course.springlamiapizzeriacrud.model.Discount;
import com.experis.course.springlamiapizzeriacrud.model.Pizza;
import com.experis.course.springlamiapizzeriacrud.repository.DiscountRepository;
import com.experis.course.springlamiapizzeriacrud.repository.PizzaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @GetMapping("/create")
    public String create(
            @RequestParam Integer pizzaId,
            Model model
    ) {
        Pizza pizza = pizzaRepository
                .findById(pizzaId)
                .orElseThrow(
                        () -> new RuntimeException("Pizza con id " + pizzaId + " non trovata")
                );

        Discount discount = new Discount();

        discount.setStartDate(LocalDate.now());
        discount.setEndDate(LocalDate.now().plusDays(7));
        discount.setPizza(pizza);

        model.addAttribute("discount", discount);

        return "discounts/form";
    }

    @PostMapping("/create")
    public String store(
            @Valid @ModelAttribute("discount") Discount formDiscount,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "discounts/form";
        }

        try {
            discountRepository.save(formDiscount);
        } catch (RuntimeException e) {

            return "discounts/form";
        }

        return "redirect:/pizzas/show/" + formDiscount.getPizza().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model
    ) {
        Discount discount = discountRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Discount with id " + id + " not found")
                );

        model.addAttribute("discount", discount);

        return "discounts/form";
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("discount") Discount formDiscount,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "discounts/form";
        }

        try {
            discountRepository.save(formDiscount);
        } catch (RuntimeException e) {

            return "discounts/form";
        }

        return "redirect:/pizzas/show/" + formDiscount.getPizza().getId();
    }
}
