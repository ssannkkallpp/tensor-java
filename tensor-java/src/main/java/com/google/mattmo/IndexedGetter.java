package com.google.mattmo;

import com.google.common.base.Function;

/**
 * IndexedGetter
 *
 * @author matthew.gretton
 */
public interface IndexedGetter<E>
{
  E get(Integer... index);
}
