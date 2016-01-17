package de.espend.idea.php.toolbox.navigation.locator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectFileTargetLocator implements PhpToolboxTargetLocator {

    @NotNull
    @Override
    public Collection<PsiElement> getTargets(@NotNull TargetLocatorParameter parameter) {
        if(!parameter.getTarget().startsWith("file://")) {
            return Collections.emptyList();
        }

        String projectFile = parameter.getTarget().substring("file://".length());
        projectFile = StringUtil.trimStart(projectFile.replaceAll("\\\\+", "/").replaceAll("/+", "/"), "/");

        VirtualFile relativeFile = VfsUtil.findRelativeFile(parameter.getProject().getBaseDir(), projectFile.split("/"));
        if(relativeFile == null) {
            return Collections.emptyList();
        }

        PsiFile file = PsiManager.getInstance(parameter.getProject()).findFile(relativeFile);
        if(file == null) {
            return Collections.emptyList();
        }

        Collection<PsiElement> targets = new ArrayList<PsiElement>();
        targets.add(file);

        return targets;
    }
}
