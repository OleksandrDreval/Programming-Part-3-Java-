package ua.nure.ice.bookcatalog.laboratorna4.saga.dto;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

import java.util.List;


class SagaDtoPojoTest {

    private static final String DTO_PACKAGE = "ua.nure.ice.bookcatalog.laboratorna4.saga.dto";

    @Test
    void validateDtoGettersAndSetters() {
        List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses(DTO_PACKAGE);

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .build();

        validator.validate(pojoClasses);
    }
}
