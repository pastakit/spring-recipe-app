package guru.springframework.converters;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Category;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RecipeToRecipeCommand implements Converter<Recipe, RecipeCommand> {

    private final CategoryToCategoryCommand categorytConverter;
    private final NotesToNotesCommand notesToNotesCommand;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;


    @Autowired
    public RecipeToRecipeCommand(CategoryToCategoryCommand categorytConverter,
                                 NotesToNotesCommand notesToNotesCommand,
                                 IngredientToIngredientCommand ingredientToIngredientCommand) {
        this.categorytConverter = categorytConverter;
        this.notesToNotesCommand=notesToNotesCommand;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
    }

    @Override
    public RecipeCommand convert(Recipe source) {
        if(source==null){
            return null;
        }

        RecipeCommand command = new RecipeCommand();
        command.setId(source.getId());
        command.setDescription(source.getDescription());
        command.setPrepTime(source.getPrepTime());
        command.setCookTime(source.getCookTime());
        command.setServings(source.getServing());
        command.setSource(source.getSource());
        command.setUrl(source.getUrl());
        command.setDirections(source.getDirection());
        command.setDifficulty(source.getDifficulty());
        command.setNotes(notesToNotesCommand.convert(source.getNotes()));


        if(source.getCategories()!=null){
            for(Category c:source.getCategories()){
                command.getCategories().add(categorytConverter.convert(c));
            }
        }

        if(source.getIngredients()!=null){
            for(Ingredient i: source.getIngredients()){
                command.getIngredients().add(ingredientToIngredientCommand.convert(i));
            }
        }

        return command;

    }
}
