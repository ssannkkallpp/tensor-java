package com.google.mattmo;

import java.util.Collection;

/**
 * @author Matthew Gretton
 * @param <E>
 */
public interface Tensor<E> extends Collection<E>
{
  Tensor<E> subTensor(Integer... tensorIndex);

  Iterable<Tensor<E>> subTensorIterable(int... fixedIndexPositions);

  E get(int... tensorIndex);

  int[] indexSizes();

  int order();
}




