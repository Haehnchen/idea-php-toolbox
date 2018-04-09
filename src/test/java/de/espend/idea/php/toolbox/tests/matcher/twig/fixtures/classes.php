<?php

interface StringReturnInterface
{
    public function getName();
}

class ReturnClass implements StringReturnInterface {

    public function getName()
    {
        return 'foo_return';
    }
}