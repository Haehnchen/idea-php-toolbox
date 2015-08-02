package de.espend.idea.php.toolbox.provider;

import org.jetbrains.annotations.NotNull;

public class ClassInterfaceProvider extends ClassProvider {

    @NotNull
    @Override
    public String getName() {
        return "ClassInterface";
    }

    protected boolean withInterfaces() {
        return true;
    }

}
