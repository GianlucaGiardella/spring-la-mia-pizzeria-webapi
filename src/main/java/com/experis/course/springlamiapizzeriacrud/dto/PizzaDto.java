package com.experis.course.springlamiapizzeriacrud.dto;

import com.experis.course.springlamiapizzeriacrud.model.Ingredient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class PizzaDto {
    private Integer id;

    @Size(max = 50, message = "Max 50 characters")
    @Size(min = 3, message = "Min 3 characters")
    private String name;

    @Size(min = 3, message = "Min 3 characters")
    private String description;

    private MultipartFile imageFile;

    @NotNull(message = "Insert a number")
    @Min(value = 1, message = "Min 1€")
    private BigDecimal price;

    private List<Ingredient> ingredients;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
