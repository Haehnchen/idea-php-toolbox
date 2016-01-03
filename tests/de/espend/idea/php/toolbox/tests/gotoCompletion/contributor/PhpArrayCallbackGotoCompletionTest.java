package de.espend.idea.php.toolbox.tests.gotoCompletion.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpArrayCallbackGotoCompletionTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void testArrayCallback() {
        String[] strings = {
            "[$this, '<caret>']",
            "[ $this, '<caret>']",
            "array($this, '<caret>')",
            "[\n\n$this\n,\n\n'<caret>'\n]"
        };

        for (String s : strings) {
            assertCompletionContains(PhpFileType.INSTANCE, String.format("<?php\n" +
                    "class Foo\n" +
                    "{\n" +
                    "   public function foo()\n" +
                    "   {\n" +
                    "        %s" +
                    "   }\n" +
                    "   private function bar() {}\n" +
                    "   protected function car() {}\n" +
                    "}", s),
                "foo", "bar", "car"
            );
        }

        for (String s : new String[] {"[$this, $var , '<caret>']", "[$var, $this, '<caret>']"}) {
            assertCompletionNotContains(PhpFileType.INSTANCE, String.format("<?php\n" +
                    "class Foo\n" +
                    "{\n" +
                    "   public function foo()\n" +
                    "   {\n" +
                    "        %s" +
                    "   }\n" +
                    "}", s),
                "foo"
            );
        }

        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n" +
                "namespace Foo\n" +
                "{\n" +
                "  class Foo\n" +
                "  {\n" +
                "    public function bar() {}\n" +
                "  }\n" +
                "}\n" +
                "namespace {" +
                "   use \\Foo\\Foo" +
                "  /** @var $foo Foo */\n" +
                "  [$foo, '<caret>']" +
                "}",
            "bar"
        );
    }

    public void testArrayCallbackNavigation() {
        for (String s : new String[]{"[$this, 'foo<caret>']", "[\n\n$this\n,\n\n'foo<caret>'\n]"}) {
            assertNavigationMatch(PhpFileType.INSTANCE, String.format("<?php\n" +
                    "class Foo\n" +
                    "{\n" +
                    "   public function foo()\n" +
                    "   {\n" +
                    "        %s" +
                    "   }\n" +
                    "}", s),
                PlatformPatterns.psiElement(Method.class)
            );
        }
    }
}
