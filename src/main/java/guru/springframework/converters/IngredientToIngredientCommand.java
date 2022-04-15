package guru.springframework.converters;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.domain.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IngredientToIngredientCommand implements Converter<Ingredient, IngredientCommand> {

    private final UnitOfMeasureToUnitOfMeasureCommand uomConverter;

    @Autowired
    public IngredientToIngredientCommand(UnitOfMeasureToUnitOfMeasureCommand uomConverter) {
        this.uomConverter = uomConverter;
    }

    @Override
    public IngredientCommand convert(Ingredient entity) {
        if(entity!=null){
            IngredientCommand command = new IngredientCommand();
            command.setId(entity.getId());
            if(entity.getRecipe()!=null){
                command.setRecipeId(entity.getRecipe().getId());
            }
            command.setAmount(entity.getAmount());
            command.setDescription(entity.getDescription());
            command.setUom(uomConverter.convert(entity.getUom()));

            return command;
        }

        return null;
    }
}
