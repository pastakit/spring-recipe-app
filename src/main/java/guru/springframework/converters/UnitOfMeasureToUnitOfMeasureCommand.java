package guru.springframework.converters;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UnitOfMeasureToUnitOfMeasureCommand implements Converter<UnitOfMeasure, UnitOfMeasureCommand> {

    @Override
    public UnitOfMeasureCommand convert(UnitOfMeasure entity){
        if (entity!=null){
            UnitOfMeasureCommand command = new UnitOfMeasureCommand();
            command.setId(entity.getId());
            command.setDescription(entity.getDescription());
            return command;
        }

        return null;
    }

}
