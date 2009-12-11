package com.google.mattmo;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.markit.mtk.collections.ArrayUtils;

final class IndexUtils
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

  public static int[] add(int[] array, int[] indices, int[] values)
  {
    if (indices.length == 0)
      return array;

    int[] retVal = new int[array.length+indices.length];

    for (int i = 0,newIndex =0,arrayIndex=0;i<retVal.length;i++)
    {
      if(newIndex < indices.length && i == indices[newIndex])
        retVal[i] = values[newIndex++];
      else
        retVal[i] = array[arrayIndex++];
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

  public static IndexInfo nonNullIndexInfo(Integer... index)
  {
    Iterable<Integer> iterable = Iterables.filter(ArrayUtils.iterable(index),new Predicate<Integer>()
    {
      @Override
      public boolean apply(Integer integer)
      {
        return integer!=null;
      }
    });

    int size = Iterables.size(iterable);
    int[] fixedPos = new int[size];
    int[] fixedPVal = new int[size];

    for(int i = 0,j=0;i<index.length;i++)
    {
      if(index[i]!=null)
      {
        fixedPos[j]=i;
        fixedPVal[j++]=index[i];
      }

    }

    return new IndexInfo(fixedPos,fixedPVal);
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

  
}


