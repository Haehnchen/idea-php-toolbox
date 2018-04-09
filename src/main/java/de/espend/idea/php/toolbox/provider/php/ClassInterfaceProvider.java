package de.espend.idea.php.toolbox.provider.php;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.matcher.php.docTag.ToolboxDocTagAliasInterface;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ClassInterfaceProvider extends PhpIndexAbstractProviderAbstract implements ToolboxDocTagAliasInterface {

    @NotNull
    @Override
    public String getName() {
        return "class_interface";
    }

    @Override
    protected Collection<PhpClass> getPhpClassesForLookup(final @NotNull PhpIndex phpIndex, final @NotNull String className) {
        return new HashSet<PhpClass>() {{
            addAll(phpIndex.getClassesByName(className));
            addAll(phpIndex.getInterfacesByName(className));
        }};
    }

    @Override
    public String[] getInlineStrings() {
        return new String[] {"ClassInterface"};
    }
}
