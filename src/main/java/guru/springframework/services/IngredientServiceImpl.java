package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;


@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientToIngredientCommand ingredientToIngredientCommand;
    @Autowired
    private UnitOfMeasureRepository unitOfMeasureRepository;
    @Autowired
    private IngredientCommandToIngredient ingredientCommandToIngredient;


    @Transactional
    @Override
    public IngredientCommand findByRecipeIdAndId(Long recipeId, Long ingredientId) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(!recipeOptional.isPresent()){
            log.error("Recipe id not found  " + recipeId);
            return null;
        }

        Recipe recipe = recipeOptional.get();

        Optional<IngredientCommand> ingredientCommandOptional =  recipe.getIngredients().stream().filter(ingredient -> ingredient.getId().equals(ingredientId))
                                        .map(ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();

        if(!ingredientCommandOptional.isPresent()){
            log.error("Ingredient id is not found "+ingredientId);
        }

        return ingredientCommandOptional.get();

    }

    @Override
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

        if(!recipeOptional.isPresent()){

            log.error("Recipe id not found "+ command.getRecipeId());
            return null;
        }else{
            Recipe recipe = recipeOptional.get();

            Optional<Ingredient> ingredientOptional = recipe.getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            // ingredient have existed
            if(ingredientOptional.isPresent()){
                Ingredient ingredientFound = ingredientOptional.get();

                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository
                        .findById(command.getUom().getId())
                        .orElseThrow(()->new RuntimeException("UOM not found"))); // todo address

            }else{
                // add new Ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                ingredient.setRecipe(recipe);
                recipe.getIngredients().add(ingredient);
            }

            //saved recipe, expected also save their new ingredient
            Recipe savedRecipe = recipeRepository.save(recipe);

            // check
            Optional<Ingredient> savedIngredientOptional =  savedRecipe.getIngredients()
                                                                .stream()
                                                                .filter(ingredient -> ingredient.getId().equals(command.getId()))
                                                                .findFirst();
            // check by des..., case add new
            if(!savedIngredientOptional.isPresent()){
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                                            .filter(ingredient -> ingredient.getDescription().equals(command.getDescription()))
                                            .filter(ingredient -> ingredient.getAmount().equals(command.getAmount()))
                                            .filter(ingredient -> ingredient.getUom().getId().equals(command.getUom().getId()))
                                            .findFirst();
            }


            return ingredientToIngredientCommand.convert(savedIngredientOptional.get());
        }
    }

    @Override
    public void deleteByRecipeIdAndId(Long recipeId, Long ingredientId) {
        log.debug("deleteByRecipeIdAndId:"+recipeId+":"+ingredientId);

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

        if(!recipeOptional.isPresent()){
            log.debug("recipe id not found" + recipeId);
        }else{
            Recipe recipe = recipeOptional.get();

            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                                                    .filter(ingredient -> ingredient.getId().equals(ingredientId))
                                                    .findFirst();

            if(ingredientOptional.isPresent()){
                Ingredient ingredient = ingredientOptional.get();

                ingredient.setRecipe(null);
                recipe.getIngredients().remove(ingredient);

                recipeRepository.save(recipe);

            }

        }


    }
}
