<?php

namespace Foo
{
    class Parameter
    {
        public function getFoo() {}
    }

    class Variadic
    {
        public function getFoo() {}
    }

    trait FooTrait {}
    interface FooInterface {}
}

namespace {
    function t() {};
    function i() {};
    function c() {};
    function ci() {};
    function _variadic() {};
}