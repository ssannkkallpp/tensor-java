package com.google.mattmo;

/**
 * IndexedGetter
 *
 * @author matthew.gretton
 */
public interface IndexedGetter<E>
{
  E get(int... index);  
}
