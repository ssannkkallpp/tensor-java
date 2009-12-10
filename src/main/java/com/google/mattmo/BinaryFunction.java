package com.google.mattmo;

public interface BinaryFunction<A, B, C>
{
  C apply(A leftItem, B rightItem);
}
