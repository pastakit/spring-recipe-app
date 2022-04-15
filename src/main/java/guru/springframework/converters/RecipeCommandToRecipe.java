package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Category;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RecipeCommandToRecipe implements Converter<RecipeCommand, Recipe> {

    private final IngredientCommandToIngredient ingredientConverter;
    private final NotesCommandToNotes notesConverter;
    private final CategoryCommandToCategory categoryConverter;

    @Autowired
    public RecipeCommandToRecipe(IngredientCommandToIngredient ingredientConverter,
                                 NotesCommandToNotes noteConverter,
                                 CategoryCommandToCategory categoryConverter) {
        this.ingredientConverter = ingredientConverter;
        this.notesConverter = noteConverter;
        this.categoryConverter=categoryConverter;

    }

    @Override
    public Recipe convert(RecipeCommand source) {
        if (source==null){
            return null;
        }

        Recipe recipe = new Recipe();

        recipe.setId(source.getId());
        recipe.setDescription(source.getDescription());
        recipe.setPrepTime(source.getPrepTime());
        recipe.setCookTime(source.getCookTime());
        recipe.setServing(source.getServings());
        recipe.setSource(source.getSource());
        recipe.setUrl(source.getUrl());
        recipe.setDirection(source.getDirections());

        recipe.setNotes(notesConverter.convert(source.getNotes()));

        if(source.getCategories()!=null){
            for(CategoryCommand cc: source.getCategories()){
                recipe.getCategories().add(categoryConverter.convert(cc));
            }
        }

        // ingredientConverter ??

        return recipe;
    }
}
