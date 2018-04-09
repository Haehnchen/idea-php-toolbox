package de.espend.idea.php.toolbox.provider.php;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ClassProvider extends PhpIndexAbstractProviderAbstract {

    @Override
    protected Collection<PhpClass> getPhpClassesForLookup(@NotNull PhpIndex phpIndex, @NotNull String className) {
        return phpIndex.getClassesByName(className);
    }

    @NotNull
    @Override
    public String getName() {
        return "class";
    }
}

