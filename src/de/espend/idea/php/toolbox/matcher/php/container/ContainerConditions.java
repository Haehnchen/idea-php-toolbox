package de.espend.idea.php.toolbox.matcher.php.container;

import com.intellij.openapi.util.Condition;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ContainerConditions {

    final public static Condition<JsonSignature> DEFAULT_TYPE_FILTER = new TypeSignatureCondition(JsonSignature.DEFAULT_TYPE);
    final public static Condition<JsonSignature> RETURN_TYPE_FILTER = new TypeSignatureCondition("return");

    final public static Condition<JsonSignature> ARRAY_KEY_AND_FUNCTION_FILTER = new ArrayKeyAndFunctionCondition();
    final public static Condition<JsonSignature> FUNCTION_FILTER = new FunctionCondition();

    final public static Condition<JsonSignature> CONSTRUCTOR_FILTER = new ConstructorJsonSignatureCondition();

    private static class ArrayKeyAndFunctionCondition implements Condition<JsonSignature> {
        @Override
        public boolean value(JsonSignature signature) {
            return "array_key".equals(signature.getType()) && StringUtils.isNotBlank(signature.getFunction());
        }
    }

    private static class FunctionCondition implements Condition<JsonSignature> {
        @Override
        public boolean value(JsonSignature s) {
            return StringUtils.isNotBlank(s.getFunction());
        }
    }

    private static class TypeSignatureCondition implements Condition<JsonSignature> {
        final private String type;

        public TypeSignatureCondition(@NotNull String type) {
            this.type = type;
        }

        @Override
        public boolean value(JsonSignature signature) {
            return type.equals(signature.getType());
        }
    }

    private static class ConstructorJsonSignatureCondition implements Condition<JsonSignature> {
        @Override
        public boolean value(JsonSignature signature) {
            return "__construct".equalsIgnoreCase(signature.getMethod());
        }
    }
}
