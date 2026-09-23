package com.caesar.add2num.core.api;

import com.caesar.add2num.core.MyBigNumber;
import java.lang.reflect.Method;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class AdditionApiTest {

    private final AdditionService service = new AdditionService(new MyBigNumber());

    @Test
    void serviceAddsLargeNumbersAndTrimsOperands() {
        AdditionResponse response = service.add(" 999999999999999999999 ", "1");

        assertThat(response.result()).isEqualTo("1000000000000000000000");
        assertThat(response.firstNumber()).isEqualTo("999999999999999999999");
        assertThat(response.secondNumber()).isEqualTo("1");
    }

    @Test
    void controllerDelegatesToService() {
        AdditionApiController controller = new AdditionApiController(service);

        AdditionResponse response = controller.add(new AdditionRequest("12", "30"));

        assertThat(response.result()).isEqualTo("42");
    }

    @Test
    void handlerReturnsBadRequestForInvalidFields() throws NoSuchMethodException {
        Method method = AdditionApiController.class.getMethod("add", AdditionRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new AdditionRequest("x", "1"), "additionRequest");
        bindingResult.addError(new FieldError("additionRequest", "firstNumber", "must contain digits"));

        CoreApiError error = new CoreApiExceptionHandler()
                .handleValidation(new MethodArgumentNotValidException(parameter, bindingResult))
                .getBody();

        assertThat(error).isNotNull();
        assertThat(error.timestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(error.status()).isEqualTo(400);
        assertThat(error.fields()).extracting(CoreApiError.FieldMessage::field)
                .containsExactly("firstNumber");
    }
}