Intellij / PhpStorm Plugin for PHP Improvements
========================

[![Build Status](https://travis-ci.org/Haehnchen/idea-php-toolbox.svg?branch=master)](https://travis-ci.org/Haehnchen/idea-php-toolbox)
[![Version](http://phpstorm.espend.de/badge/8133/version)](https://plugins.jetbrains.com/plugin/8133)
[![Downloads](http://phpstorm.espend.de/badge/8133/downloads)](https://plugins.jetbrains.com/plugin/8133)
[![Downloads last month](http://phpstorm.espend.de/badge/8133/last-month)](https://plugins.jetbrains.com/plugin/8133)

Key         | Value
----------- | -----------
Plugin Url  | http://plugins.jetbrains.com/plugin?pr=&pluginId=8133
ID          | de.espend.idea.php.toolbox
Doc x       | http://phpstorm.espend.de/php-toolbox

## Screenshots
![Php Toolbox PHPUnit](https://plugins.jetbrains.com/files/8133/screenshot_15579.png)

## Core improvements

### Type hint variable
```php
// Strips several non common variable names of type hint completion; like "interface"
function foo(FooInterface $<caret>)
function foo(FooAbstract $<caret>)
function foo(FooExtension $<caret>)
```

### Callable arrays
```php
class Bar
{
    public function foo()
    {
        [$this, 'foo<caret>']
        [$foo, 'foo<caret>']
    }
}
$foo = new Bar();
```

### String class, method and function navigation
```php
foo('\\DateTime');
foo('\\DateTime:format');
foo('\\DateTime::format');
'DateTime::format';
'date';
```

## Json Configuration

All files in project named `.ide-toolbox.metadata.json` or application folder with pattern `/php-toolbox/*-toolbox.metadata.json`. Find your application "Settings > Languages & Framework > PHP Toolbox"

```javascript
{
  "registrar":[
    {
      "signature":[
        "Behat\\Behat\\Context\\Environment\\ContextEnvironment:hasContextClass",
        "Behat\\Behat\\Context\\Environment\\InitializedContextEnvironment:getContext"
      ],
      "provider":"behat_context_classes",
      "language":"php"
    }
  ],
  "providers": [
    {
      "name": "behat_context_classes",
      "source": {
        "contributor": "sub_classes",
        "parameter": "Behat\\Behat\\Context\\Context"
      }
    }
  ]
}
```


### Registrar

```javascript
{
  "provider":"date_format",
  "language":"php",
  "signatures":[
    {
      "class": "DateTime",
      "method": "format"
    },
    {
      "class": "PHPUnit_Framework_TestCase",
      "method": "getMock",
      "type": "type"
    },
    {
      "class": "Symfony\\Component\\HttpFoundation\\Response",
      "method": "__construct",
      "type": "array_key",
      "index": 2
    },
    {
      "class": "Symfony\\Component\\HttpFoundation\\Response",
      "method": "__construct",
      "index": 2,
      "array": "Content-Type"
    }    
  ]
}
```

#### Function

```php
foo('<caret>')
```

```javascript
{
  "function": "foo"
}
```

```php
foo('', '<caret>')
```

```javascript
{
  "function": "foo",
  "index": 1
}
```

#### Class method

```php
/** @var $f \\FooClass */
$f->foo('<caret>')
```

```javascript
{
  "class": "FooClass",
  "method": "foo"
}
```

```php
/** @var $f \\FooClass */
$f->foo('', '<caret>')
```

```javascript
{
  "class": "FooClass",
  "method": "foo",
  "index": 1
}
```

#### Types

```php
/** @var $f \\FooClass */
$f->foo('date_time')->format<caret>
```

```javascript
{
  "registrar":[
    {
      "provider":"date",
      "language":"php",
      "signatures":[
        {
          "class": "FooClass",
          "method": "foo",
          "type": "type"
        }
      ]
    }
  ],
  "providers": [
    {
      "name": "date",
      "items":[
        {
          "lookup_string": "date_time",
          "type": "DateTime"
        }
      ]
    }
  ]
}
```

#### Array

```php
foo(['<caret>'])
```

```javascript
{
  "function": "foo",
  "type": "array_key"
}
```

```php
foo(['foo' => '<caret>'])
```

```javascript
{
  "function": "foo",
  "array": "foo"
}
```

#### Signature shortcut

```javascript
{
  "provider":"class_interface",
  "language":"php",
  "signature":[
    "ReflectionProperty:__construct",
    "class_exists",
    "is_subclass_of:1" // ":1" means 2nd argument (arguments indexing starts from 0)
  ]  
}
```

#### Twig

```twig
{{ foo('<caret>') }}
{% if foo('<caret>') %}
{% set bar = foo('<caret>') %}
{{ 'bar'|foo('<caret>') }}
```

```javascript
{
  "provider":"date_format",
  "language":"twig",
  "signatures":[
    {
      "function": "foo"
    }
  ]
}
```    

### Providers

```javascript
{
  "name": "date_format",
  "items":[
    {
      "lookup_string": "d",
      "type_text": "Day of month (01..31)",
      "icon": "com.jetbrains.php.PhpIcons.METHOD"
      "presentable_text": "foo",
      "tail_text": "foo",
      "type": "DateTime",
      "target": "DateTime",
      "target": "Ns\\Time::format",
      "target": "DateTime:format",
    }
  ]
}
```

#### Lookup shortcut

```javascript
{
  "name": "date_format",
  "lookup_strings": ["car", "apple"]
}
```

#### Lookup defaults

```javascript
{
  "name": "date_format",
  "defaults": {
    "icon":"com.jetbrains.php.PhpIcons.METHOD",
  },
  "items": [
    {
      "lookup_string":"d",
    }
  ]  
}
```

#### Sources
 
##### return

```php
class SecurityExtension implements Twig_ExtensionInterface
{
    public function getName()
    {
        return 'security'
    }
}
class WebProfilerExtension extends Twig_ExtensionInterface
{
    public function getName()
    {
        return 'profiler'
    }
}
```

```javascript
{
  "name": "twig_extensions",
  "source": {
    "contributor": "return",
    "parameter": "Twig_ExtensionInterface::getName"
  }
}
```

##### return_array

```php
class UserAdmin implements AdminInterface
{
    public function getExportFormats()
    {
        return ['json', 'xml']
    }
}
class TopicAdmin extends AdminInterface
{
    public function getExportFormats()
    {
        return ['csv', 'xls']
    }
}
```

```javascript
{
  "name": "sonata_admin_export_formats",
  "source": {
    "contributor": "return_array",
    "parameter": "Sonata\\AdminBundle\\Admin\\AdminInterface:getExportFormats"
  }
}
```

##### sub_classes

```php
class Foo implements BehatContext {}
class Foo extends BehatContext {}
```

```javascript
{
  "name": "behat_context_classes",
  "source": {
    "contributor": "sub_classes",
    "parameter": "BehatContext"
  }
}
```


