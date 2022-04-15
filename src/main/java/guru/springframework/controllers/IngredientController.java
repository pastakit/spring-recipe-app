package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@Controller
public class IngredientController {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    @Autowired
    public IngredientController(RecipeService recipeService,
                                IngredientService ingredientService,
                                UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }


    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        RecipeCommand dto = recipeService.findCommandById(Long.valueOf(recipeId));
        model.addAttribute("recipe", dto);

        return "recipe/ingredient/list";
    }

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable Long recipeId,
                                  @PathVariable Long id,
                                  Model model){

        IngredientCommand command = ingredientService.findByRecipeIdAndId(recipeId, id);

        model.addAttribute("ingredient", command);

        return "recipe/ingredient/show";

    }

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id,
                                       Model model){

        IngredientCommand command = ingredientService.findByRecipeIdAndId(Long.valueOf(recipeId), Long.valueOf(id));

        model.addAttribute("ingredient", command);
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";

    }

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredient/new")
    public String createRecipeIngredient(@PathVariable String recipeId,
                                         Model model){
        // recipe id
        Recipe recipe = recipeService.findById(Long.valueOf(recipeId));
        if(recipe==null){
            log.error("Recipe Id not found");
        }
        // command
        IngredientCommand command = new IngredientCommand();
        command.setRecipeId(Long.valueOf(recipeId));
        model.addAttribute("ingredient", command);

        // init uom
        command.setUom(new UnitOfMeasureCommand());

        //list uom
        Set<UnitOfMeasureCommand> uomList = unitOfMeasureService.listAllUoms();
        model.addAttribute("uomList", uomList);

        return "recipe/ingredient/ingredientform";

    }

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredient/{ingredientId}/delete")
    public String deleteRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String ingredientId,
                                         Model model){

        ingredientService.deleteByRecipeIdAndId(Long.valueOf(recipeId),Long.valueOf(ingredientId));

        return "redirect:/recipe/"+ recipeId +"/ingredients";

    }


    @PostMapping
    @RequestMapping("/recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@PathVariable String recipeId,
                               @ModelAttribute IngredientCommand command){

        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

        log.debug("saved receipe id:" + savedCommand.getRecipeId());
        log.debug("saved ingredient id:" + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";

    }

//    // show ingredient
//
//    @GetMapping
//    @RequestMapping("/recipe/{recipeId}/ingredient/{ingredientId}/show")
//    public String showIngredient(@PathVariable String recipeId,
//                                 @PathVariable String ingredientId,
//                                 Model model){
//
//    }



}
