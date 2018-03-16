<?php

namespace Foo {
    Class Car
    {
        /**
         * @param string $foo my Var #Class
         * @param string $bar my car #Trait foo bar
         * @param string $c my car #class_interface foo bar
         */
        public function foo($foo, $bar, $c) {

        }
    }

    class Bike extends Car
    {
        public function foo($foo, $bar)
        {
        }
    }

    Class Compatibility
    {
        /**
         * @param string $foo my Var #ClassInterface aaaas
         */
        public function foo($foo) {

        }
    }

    trait FooTrait {}
}

namespace {
    /**
     * @param string $var #Class
     */
    function foo($var, $foo) {}

    /**
     * @param string $car #Class
     */
    function car($var, $car) {}

    function date() {}
    class DateTime
    {
        public function format($foobar) {}
    }

    class TestClass
    {
        /**
         * @param string $var #Class
         */
        public function __construct($var, $foo)
        {
        }
    }
}