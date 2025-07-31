package com.workintech.s17d2.rest;

import com.workintech.s17d2.enums.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import jakarta.annotation.PostConstruct;
import com.workintech.s17d2.model.Developer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.workintech.s17d2.tax.Taxable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/developers")
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
        Developer juniorDev = new Developer(123, "Oğuzhan", 25000.00, Experience.JUNIOR);
        Developer midDev = new Developer(456, "Elozi", 30000.00, Experience.MID);
        Developer seniorDev = new Developer(789, "Elif", 50000.00, Experience.SENIOR);
        developers.put(juniorDev.getId(), juniorDev);
        developers.put(midDev.getId(), midDev);
        developers.put(seniorDev.getId(), seniorDev);
    }

    @GetMapping
    public List<Developer> findAll() {
        return developers.values().stream().toList();
    }

    @GetMapping("/{id}")
    public Developer findById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDev(@RequestBody Developer newDev) {
        double newSalary = newDev.getSalary();

        if (newDev.getExperience().equals(Experience.JUNIOR)) {
            newSalary = newSalary -(newSalary * taxable.getSimpleTaxRate());
            newDev = new JuniorDeveloper(newDev.getId(), newDev.getName(), newSalary);
        } else if (newDev.getExperience().equals(Experience.MID)) {
            newSalary = newSalary - (newSalary * taxable.getMiddleTaxRate());
            newDev = new MidDeveloper(newDev.getId(), newDev.getName(), newSalary);
        } else if (newDev.getExperience().equals(Experience.SENIOR)) {
            newSalary = newSalary - (newSalary * taxable.getUpperTaxRate());
            newDev = new SeniorDeveloper(newDev.getId(), newDev.getName(), newSalary);
        } else {
            throw new RuntimeException("Invalid experience type: " + newDev.getExperience());
        }
        developers.put(newDev.getId(), newDev);
        return newDev;
    }

    @PutMapping("/{id}")
    public Developer updateDev(@PathVariable int id, @RequestBody Developer updateDev) {
        developers.put(updateDev.getId(), updateDev);
        return updateDev;
    }

    @DeleteMapping("/{id}")
    public void removeDev(@PathVariable int id) {
        developers.remove(id);
    }


}
