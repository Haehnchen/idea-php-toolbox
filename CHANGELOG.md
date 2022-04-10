Changelog
=========

## 6.0.1
* Prepare dynamic plugin feature (Daniel Espendiller)
* Update vendor link (Daniel Espendiller)

## 6.0.0
* travis to github actions migration (Daniel Espendiller)
* Use ServiceManager to retrieve PhpToolboxApplicationService (Cedric Ziel)
* Migrate PhpToolboxApplicationService to Service (Cedric Ziel)
* Use current classloader for icon (Cedric Ziel)
* Remove unused methods with default implementations (Cedric Ziel)
* Remove unused imports (Cedric Ziel)
* Fix json in docs (Cedric Ziel)
* Remove inapplicable inspection (Cedric Ziel)
* Add NotNull annotation where applicable (Cedric Ziel)
* Use BasePlatformTestCase instead of LightCodeInsightFixtureTestCase (Cedric Ziel)
* Fix code fences (Cedric Ziel)
* Upgrade platform dependencies to 211 (Cedric Ziel)
* Upgrade Gradle wrapper to 7.0.0 (Cedric Ziel)

## 0.5.1
* Fix setting since build on build [#92](https://github.com/Haehnchen/idea-php-toolbox/issues/92) (Daniel Espendiller)

## 0.5.0
* Fix json load exception during index process [#87](https://github.com/Haehnchen/idea-php-toolbox/issues/87) [#90](https://github.com/Haehnchen/idea-php-toolbox/issues/90) (Daniel Espendiller)

## 0.4.6
* Fix PhpDoc hashes don't work for constructor parameters [#74](https://github.com/Haehnchen/idea-php-toolbox/issues/74) [#50](https://github.com/Haehnchen/idea-php-toolbox/issues/50) @podhy

## 0.4.5
* Allow resolving parameter types from new expressions. `foo(new Bar())` [#69](https://github.com/Haehnchen/idea-php-toolbox/issues/69) @CarsonF

## 0.4.4
* Add more validation for type index type resolve [#67](https://github.com/Haehnchen/idea-php-toolbox/issues/67) [#68](https://github.com/Haehnchen/idea-php-toolbox/issues/68)

## 0.4.3
* Updated TypeProvider to support different method/function parameters [#67](https://github.com/Haehnchen/idea-php-toolbox/issues/67) @CarsonF
* Make json model thread safe to fix ArrayIndexOutOfBoundsException [#61](https://github.com/Haehnchen/idea-php-toolbox/issues/61)

## 0.4.2
* Fix ConcurrentModificationException signature filter [#66](https://github.com/Haehnchen/idea-php-toolbox/issues/66) and migrate to PhpTypeProvider3 [#58](https://github.com/Haehnchen/idea-php-toolbox/issues/58)

## 0.4.1
* Add PSR-11 ContainerInterface support / Zend\ServiceManager [#17](https://github.com/Haehnchen/idea-php-toolbox-json-files/pull/17) @bcremer
* Add support of the latest (namespaced) PHPUnit[#16](https://github.com/Haehnchen/idea-php-toolbox-json-files/pull/16)

## 0.4.0
* PhpStorm 2017.1 build
* Add more static json configurations [#15](https://github.com/Haehnchen/idea-php-toolbox-json-files/issues/15), [#14](https://github.com/Haehnchen/idea-php-toolbox-json-files/issues/14), [#11](https://github.com/Haehnchen/idea-php-toolbox-json-files/issues/11), [#10](https://github.com/Haehnchen/idea-php-toolbox-json-files/issues/10), [#9](https://github.com/Haehnchen/idea-php-toolbox-json-files/issues/9), [#8](https://github.com/Haehnchen/idea-php-toolbox-json-files/issues/8) @stof @Koc @King2500

### 0.3.0
* Add extension point for json resource
* Fix worker exited due to exception java.lang.NullPointerException #51
* Java8 and PhpStorm 2016.x migration
* Icons from com.intellij.icons.AllIcons sub-classes not working #43
* Add function provider #38
* Update static json thx to: @King2500, @havvg

### 0.2.2
* Add some more extension point and api improvements for foreign plugins

### 0.2.1
* Add parameters to registrar json object and pipe to parameter #37
    
### 0.2
* Add trait and interface provider #15
* Providers can provide presentable information
* Add php variadic like provider
* Add Json REST-Server
* Migrate Symfony plugin inline code signature provider detection
* Skip invalid json files #23
* Fixing invalid completion for array values in key #18
* Allow double slashes for class and interfaces #25
* Provide extension point for target name resolving #34
* Add some more core json completion @Koc, @King2500 #2
      
### 0.1
* First public release