package de.espend.idea.php.toolbox.provider;

import org.jetbrains.annotations.NotNull;

public class ClassInterfaceProvider extends ClassProvider {

    protected boolean withInterface = true;

    @NotNull
    @Override
    public String getName() {
        return "ClassInterface";
    }

}
