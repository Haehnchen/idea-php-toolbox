package de.espend.idea.php.toolbox.provider.php;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class InterfaceProvider extends PhpIndexAbstractProviderAbstract {

    @NotNull
    protected Collection<String> getClasses(@NotNull PhpToolboxCompletionContributorParameter parameter) {
        return PhpIndex.getInstance(parameter.getProject()).getAllInterfaceNames();
    }

    @Override
    protected Collection<PhpClass> getPhpClassesForLookup(@NotNull PhpIndex phpIndex, @NotNull String name) {
        return phpIndex.getInterfacesByName(name);
    }

    @NotNull
    protected Collection<PhpClass> resolveParameter(@NotNull PhpIndex phpIndex, @NotNull String parameter) {
        return phpIndex.getInterfacesByFQN(parameter);
    }

    @NotNull
    @Override
    public String getName() {
        return "interface";
    }
}

