package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;


    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             RecipeCommandToRecipe recipeCommandToRecipe,
                             RecipeToRecipeCommand recipeToRecipeCommand) {

        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe=recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Set<Recipe> getRecipes() {
        Set<Recipe> recipeSet = new HashSet<>();
        recipeRepository.findAll().forEach(item->recipeSet.add(item));
        return recipeSet;
    }

    @Override
    public Recipe findById(Long aLong) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(aLong);
        if (!optionalRecipe.isPresent()){
            throw new RuntimeException("Recipe id not found");
        }

        return optionalRecipe.get();
    }

    @Override
    @Transactional
    public RecipeCommand findCommandById(Long l) {
        // convert outside scope, if hit any lazy loaded ==> error
        RecipeCommand command = recipeToRecipeCommand.convert(this.findById(l));
        return command;
    }

    @Override
    public void deleteById(Long id) {
        recipeRepository.deleteById(id);
    }


    @Override
    @Transactional
    public RecipeCommand saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

        Recipe savedRecipe = recipeRepository.save(detachedRecipe);

        return recipeToRecipeCommand.convert(savedRecipe);
    }


}
