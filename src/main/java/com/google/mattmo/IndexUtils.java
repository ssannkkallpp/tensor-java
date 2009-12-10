package com.google.mattmo;

import java.util.Arrays;

public final class IndexUtils
{
  private IndexUtils()
  {
  }

  ;

  public static int[] retain(int[] array, int[] indices)
  {
    int arrayLen = array.length;
    int indicesLen = indices.length;
    int[] retVal = new int[indicesLen];

    for (int i = 0, retainedCount = 0; retainedCount < indicesLen && i < arrayLen; i++)
    {
      if (i == indices[retainedCount])
      {
        retVal[retainedCount] = array[i];
        retainedCount++;
      }
    }
    return retVal;
  }

  public static int[] remove(int[] array, int[] indices)
  {
    if (indices.length == 0)
      return array;

    int numFreeIndices = array.length;
    int numFixedIndices = indices.length;

    int[] retVal = new int[numFreeIndices - numFixedIndices];

    for (int i = 0, fixedIndexCount = 0, freeIndexCount = 0; i < numFreeIndices; i++)
    {
      if (fixedIndexCount < numFixedIndices && i == indices[fixedIndexCount])
        fixedIndexCount++;
      else
        retVal[freeIndexCount++] = array[i];
    }

    return retVal;
  }

  public static int calcNumElements(int[] indexSizes)
  {
    int retVal = 1;

    for (int indexSize : indexSizes)
      retVal *= indexSize;

    return retVal;
  }

  public static int[] createBasis(int[] indexSizes)
  {
    int size = indexSizes.length;

    if (size == 0)
      return new int[0];

    int[] basis = new int[size];
    basis[size - 1] = 1;
    int product = basis[size - 1];

    for (int i = size - 2; i >= 0; i--)
    {
      product *= indexSizes[i + 1];
      basis[i] = product;
    }

    return basis;
  }

  public static int flattenTensorIndex(int[] aCoordinate, int[] orderedBasis)
  {
    int index = 0;

    // aCoordiante dot product orderedBasis
    for (int i = 0; i < orderedBasis.length; i++)
    {
      index += orderedBasis[i] * aCoordinate[i];
    }

    return index;
  }

  public static int[] projectIndex(int index, int[] orderedBasis)
  {
    if (orderedBasis.length == 0)
      return new int[0];

    int[] coordinate = new int[orderedBasis.length];

    int q;
    int r = index;
    int basisValue;
    for (int i = 0; i < coordinate.length; i++)
    {
      basisValue = orderedBasis[i];
      //remember this is integer arithmetic ie. floor is always taken with division 5/4 = 1, 4/5 = 0;
      q = r / basisValue;
      r -= basisValue * q;
      coordinate[i] = q;
    }

    return coordinate;
  }

  public static int[] generateFullIndex(int[] index, int indexSizesLen, int[] fixedIndexPositions, int[] fixedIndexValues)
  {
    int[] ret = new int[indexSizesLen];
    Arrays.fill(ret, -1);

    for (int i = 0; i < fixedIndexPositions.length; i++)
      ret[fixedIndexPositions[i]] = fixedIndexValues[i];

    int subCount = 0;
    int count = 0;

    for (int i : ret)
    {
      if (i == -1)
        ret[count] = index[subCount++];
      count++;
    }

    return ret;
  }

  public static FixedIndexInfo mergeFixedIndices(int[] existingFixedIndexPositions, int[] existingFixedIndexValues,
                                                 int[] newFixedIndexPositions, int[] newFixedIndexValues)
  {
    int newLen = newFixedIndexPositions.length;
    int oldLen = existingFixedIndexPositions.length;
    int retLen = newLen + oldLen;

    int[] fixedIndexPositions = new int[retLen];
    int[] fixedIndexValues = new int[retLen];

    //merge ordered lists.
    for (int i = 0, p = 0, q = 0; i < retLen; i++)
    {
      if (q >= oldLen || p < newLen && fixedIndexPositions[q] <= existingFixedIndexPositions[p])
      {
        fixedIndexPositions[i] = newFixedIndexPositions[i];
        fixedIndexValues[i] = newFixedIndexValues[i];
      }
      else
      {
        fixedIndexPositions[i] = existingFixedIndexPositions[i];
        fixedIndexValues[i] = existingFixedIndexValues[i];
      }
    }

    return new FixedIndexInfo(fixedIndexPositions, fixedIndexValues);
  }

  public static FixedIndexInfo generateFixedIndicesInfo(Integer[] array)
  {
    int keyCount = 0;

    for (Integer i : array)
      if (i != null) keyCount++;

    int[] fixedPositions = new int[keyCount];
    int[] fixedValues = new int[keyCount];


    for (int i = 0, j = 0; i < array.length; i++)
    {
      if (array[i] != null)
      {
        fixedPositions[j] = i;
        fixedValues[j] = array[i];
        j++;
      }
    }

    return new FixedIndexInfo(fixedPositions, fixedValues);

  }

  public static <E> E[] newArray(Iterable<? extends E> iterable, int arrayLength)
  {
    int i = 0;

    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Object[arrayLength];

    for (E e : iterable)
    {
      array[i++] = e;
    }

    return array;
  }
}


