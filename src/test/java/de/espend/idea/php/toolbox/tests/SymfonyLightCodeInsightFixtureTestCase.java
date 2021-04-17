package de.espend.idea.php.toolbox.tests;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public abstract class SymfonyLightCodeInsightFixtureTestCase extends BasePlatformTestCase {

    public void assertCompletionContains(LanguageFileType languageFileType, String configureByText, String... lookupStrings) {

        myFixture.configureByText(languageFileType, configureByText);
        myFixture.completeBasic();

        checkContainsCompletion(lookupStrings);
    }

    public void assertAtTextCompletionContains(String findByText, String... lookupStrings) {

        final PsiElement element = myFixture.findElementByText(findByText, PsiElement.class);
        assert element != null : "No element found by text: " + findByText;
        myFixture.getEditor().getCaretModel().moveToOffset(element.getTextOffset() + 1);
        myFixture.completeBasic();

        checkContainsCompletion(lookupStrings);
    }

    public void assertCompletionNotContains(String filename, String configureByText, String... lookupStrings) {

        myFixture.configureByText(filename, configureByText);
        myFixture.completeBasic();

        assertCompletionNot(lookupStrings);
    }

    public void assertCompletionNotContains(LanguageFileType languageFileType, String configureByText, String... lookupStrings) {

        myFixture.configureByText(languageFileType, configureByText);
        myFixture.completeBasic();

        assertCompletionNot(lookupStrings);
    }

    private void assertCompletionNot(String[] lookupStrings) {
        List<String> lookupElements = myFixture.getLookupElementStrings();

        for (String s : Arrays.asList(lookupStrings)) {
            if(lookupElements.contains(s)) {
                fail(String.format("failed that completion not contains %s in %s", s, lookupElements.toString()));
            }
        }
    }

    public void assertCompletionContains(String filename, String configureByText, String... lookupStrings) {

        myFixture.configureByText(filename, configureByText);
        myFixture.completeBasic();

        completionContainsAssert(lookupStrings);
    }

    private void completionContainsAssert(String[] lookupStrings) {

        List<String> lookupElements = myFixture.getLookupElementStrings();
        if(lookupElements == null || lookupElements.size() == 0) {
            fail(String.format("failed that completion contains '%s' in empty list", StringUtils.join(lookupStrings, ",")));
        }

        for (String s : Arrays.asList(lookupStrings)) {
            if(!lookupElements.contains(s)) {
                fail(String.format("failed that completion contains %s in %s", s, lookupElements.toString()));
            }
        }
    }

    public void assertNavigationContains(LanguageFileType languageFileType, String configureByText, String targetShortcut) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        assertNavigationContains(psiElement, targetShortcut);
    }

    public void assertNavigationContains(PsiElement psiElement, String targetShortcut) {

        if(!targetShortcut.startsWith("\\")) {
            targetShortcut = "\\" + targetShortcut;
        }

        Set<String> classTargets = new HashSet<>();

        for (GotoDeclarationHandler gotoDeclarationHandler : Extensions.getExtensions(GotoDeclarationHandler.EP_NAME)) {
            PsiElement[] gotoDeclarationTargets = gotoDeclarationHandler.getGotoDeclarationTargets(psiElement, 0, myFixture.getEditor());
            if(gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {

                for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                    if(gotoDeclarationTarget instanceof Method) {

                        String meName = ((Method) gotoDeclarationTarget).getName();

                        String clName = ((Method) gotoDeclarationTarget).getContainingClass().getPresentableFQN();
                        if(!clName.startsWith("\\")) {
                            clName = "\\" + clName;
                        }

                        classTargets.add(clName + "::" + meName);
                    } else if(gotoDeclarationTarget instanceof Function) {
                        classTargets.add("\\" + ((Function) gotoDeclarationTarget).getName());
                    }
                }

            }
        }

        if(!classTargets.contains(targetShortcut)) {
            fail(String.format("failed that PsiElement (%s) navigate to %s on %s", psiElement.toString(), targetShortcut, classTargets.toString()));
        }

    }

    public void assertNavigationMatchWithParent(LanguageFileType languageFileType, String configureByText, IElementType iElementType) {
        assertNavigationMatch(languageFileType, configureByText, PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement(iElementType)));
    }

    public void assertNavigationMatch(LanguageFileType languageFileType, String configureByText, ElementPattern<?> pattern) {

        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Set<String> targetStrings = new HashSet<>();

        for (GotoDeclarationHandler gotoDeclarationHandler : Extensions.getExtensions(GotoDeclarationHandler.EP_NAME)) {

            PsiElement[] gotoDeclarationTargets = gotoDeclarationHandler.getGotoDeclarationTargets(psiElement, 0, myFixture.getEditor());
            if(gotoDeclarationTargets == null || gotoDeclarationTargets.length == 0) {
                continue;
            }

            for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                targetStrings.add(gotoDeclarationTarget.toString());
                if(pattern.accepts(gotoDeclarationTarget)) {
                    return;
                }
            }
        }

        fail(String.format("failed that PsiElement (%s) navigate matches one of %s", psiElement.toString(), targetStrings.toString()));
    }

    public void assertNavigationContainsFile(LanguageFileType languageFileType, String configureByText, String targetShortcut) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Set<String> targets = new HashSet<>();

        for (GotoDeclarationHandler gotoDeclarationHandler : Extensions.getExtensions(GotoDeclarationHandler.EP_NAME)) {
            PsiElement[] gotoDeclarationTargets = gotoDeclarationHandler.getGotoDeclarationTargets(psiElement, 0, myFixture.getEditor());
            if (gotoDeclarationTargets != null && gotoDeclarationTargets.length > 0) {
                for (PsiElement gotoDeclarationTarget : gotoDeclarationTargets) {
                    if(gotoDeclarationTarget instanceof PsiFile) {
                        targets.add(((PsiFile) gotoDeclarationTarget).getVirtualFile().getUrl());
                    }
                }
            }
        }

        // its possible to have memory fields,
        // so simple check for ending conditions
        // temp:///src/interchange.en.xlf
        for (String target : targets) {
            if(target.endsWith(targetShortcut)) {
                return;
            }
        }

        fail(String.format("failed that PsiElement (%s) navigate to file %s", psiElement.toString(), targetShortcut));
    }

    public void assertCompletionLookupTailEquals(LanguageFileType languageFileType, String configureByText, String lookupString, String tailText) {

        myFixture.configureByText(languageFileType, configureByText);
        myFixture.completeBasic();

        for (LookupElement lookupElement : myFixture.getLookupElements()) {

            if(!lookupElement.getLookupString().equals(lookupString)) {
                continue;
            }

            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);

            if(presentation.getTailText() == null) {
                fail(String.format("failed to check '%s'", lookupString));
            }

            if(!presentation.getTailText().equals(tailText)) {
                fail(String.format("failed that on '%s' '%s' is equal '%s'", lookupString, tailText, presentation.getTailText()));
            }

            return;

        }

        fail(String.format("failed to check '%s' because it's unknown", lookupString));
    }

    public LookupElementPresentation getCompletionLookupElement(LanguageFileType languageFileType, String configureByText, String lookupString) {

        myFixture.configureByText(languageFileType, configureByText);
        myFixture.completeBasic();

        for (LookupElement lookupElement : myFixture.getLookupElements()) {

            if(!lookupElement.getLookupString().equals(lookupString)) {
                continue;
            }

            LookupElementPresentation presentation = new LookupElementPresentation();
            lookupElement.renderElement(presentation);

            return presentation;
        }

        fail(String.format("failed to to find lookup element '%s'", lookupString));

        return null;
    }

    public void assertPhpReferenceResolveTo(LanguageFileType languageFileType, String configureByText, ElementPattern<?> pattern) {
        myFixture.configureByText(languageFileType, configureByText);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        psiElement = PsiTreeUtil.getParentOfType(psiElement, PhpReference.class);
        if (psiElement == null) {
            fail("Element is not PhpReference.");
        }

        PsiElement resolve = ((PhpReference) psiElement).resolve();
        if(!pattern.accepts(resolve)) {
            fail(String.format("failed pattern matches element of '%s'", resolve == null ? "null" : resolve.toString()));
        }

        assertTrue(pattern.accepts(resolve));
    }

    public void assertCompletionResultEquals(String filename, String complete, String result) {
        myFixture.configureByText(filename, complete);
        myFixture.completeBasic();
        myFixture.checkResult(result);
    }

    public void assertCompletionResultEquals(LanguageFileType languageFileType, String complete, String result) {
        myFixture.configureByText(languageFileType, complete);
        myFixture.completeBasic();
        myFixture.checkResult(result);
    }

    public void assertCheckHighlighting(String filename, String result) {
        myFixture.configureByText(filename, result);
        myFixture.checkHighlighting();
    }

    protected void createDummyFiles(String... files) {
        for (String file : files) {
            String path = myFixture.getProject().getBaseDir().getPath() + "/" + file;
            File f = new File(path);
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                fail(String.format("failed to create file '%s'", file));
            }
        }
    }

    protected void deleteDummyFiles(String... files) {
        for (String file : files) {
            String path = myFixture.getProject().getBaseDir().getPath() + "/" + file;
            File f = new File(path);
            if(!f.exists()) {
                continue;
            }

            try {
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
                fail(String.format("failed to delete file '%s'", file));
            }
        }
    }

    private void checkContainsCompletion(String[] lookupStrings) {
        completionContainsAssert(lookupStrings);
    }

}
