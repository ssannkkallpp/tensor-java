package com.google.mattmo;

/**
 * Created by IntelliJ IDEA.
 * User: matt
 * Date: Dec 11, 2009
 * Time: 2:38:44 PM
 * To change this template use File | Settings | File Templates.
 */
final public class ArrayUtils
{
  private ArrayUtils()
  {
  }

  public static <E> E[] newArray(Iterable<? extends E> iterable, int arrayLength)
  {
    int i = 0;

    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Object[arrayLength];

    for (E e : iterable)
      array[i++] = e;

    return array;
  }
}
