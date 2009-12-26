package com.google.mattmo;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Matthew Gretton
 * @param <E>
 */
public interface Tensor<E> extends Collection<E>
{
  //Tensor methods
  public E get(final int... tensorIndex);
  public int[] indexSizes();
  public int order();
}




