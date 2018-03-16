<?php

interface ArrayReturnInterface
{
    public function getNames();
}

interface StringReturnInterface
{
    public function getName();
}

class ReturnClass implements ArrayReturnInterface, StringReturnInterface {

    public function getNames()
    {
        return ['foo_array_1', 'foo_array_2'];
    }

    public function getName()
    {
        return 'foo_return';
    }
}