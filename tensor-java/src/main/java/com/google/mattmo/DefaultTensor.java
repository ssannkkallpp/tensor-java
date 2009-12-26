package com.google.mattmo;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.markit.mtk.collections.func.Actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static com.google.mattmo.ArrayUtils.newArray;

/**
 *
 * Abstract implementation of a Tensor, the only methods that need to be implemented are the get method and the indexSizes
 * Method.
 */
public final class DefaultTensor<E> implements Tensor<E>
{
  private final IndexedGetter<E> _indexedGetter;
  private final int[] _indexSizes;
  private final int _order;
  private final int _size;

  private DefaultTensor(IndexedGetter<E> aIndexedGetter,
                       int[] aIndexSizes,
                       int aOrder,
                       int aSize)
  {
    _indexedGetter = aIndexedGetter;
    _indexSizes = aIndexSizes;
    _order = aOrder;
    _size = aSize;
  }

  @Override
  public E get(int... tensorIndex)
  {
    return _indexedGetter.get(tensorIndex);
  }

  @Override
  public int[] indexSizes()
  {
    return _indexSizes;
  }

  @Override
  public int order()
  {
    return _order;
  }

  @Override
  public int size()
  {
    return _size;
  }

  @Override
  public Iterator<E> iterator()
  {
    final int[] basis = IndexUtils.createBasis(indexSizes());
    final int size = size();

    return new AbstractIterator<E>()
    {
      private int count = 0;

      @Override
      protected E computeNext()
      {
        if (count < size)
          return get(IndexUtils.projectIndex(count++, basis));
        return endOfData();
      }
    };
  }
  
  @Override
  public String toString()
  {

    E[] internalRep = newArray(this, this.size());

    return "DefaultImmutableTensor{" +
        "flattenedTensor =" + (Arrays.asList(internalRep)) +
        ", index sizes =" + Arrays.toString(indexSizes()) +
        '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof DefaultTensor)) return false;

    DefaultTensor<?> that = (DefaultTensor<?>) o;

    E[] thisArray = newArray(this, this.size());

    Object[] thatArray = newArray(that, that.size());

    if (!Arrays.equals(this.indexSizes(), that.indexSizes())) return false;
    if (!Arrays.equals(thisArray, thatArray)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    E[] elements = newArray(this, this.size());
    int result = Arrays.hashCode(elements);
    result = 31 * result + Arrays.hashCode(this.indexSizes());
    return result;
  }

  @Override
  public boolean isEmpty()
  {
    return Iterables.isEmpty(this);
  }

  @Override
  public boolean contains(Object o)
  {
    return Iterables.contains(this,o);
  }

  @Override
  public boolean containsAll(final Collection<?> c)
  {
    final Collection<Object> bob = new ArrayList<Object>();

    Actions.<Collection<Object>,Object>addToCollectionWhere(this,bob,new Predicate<Object>()
    {
      @Override
      public boolean apply(Object o)
      {
        return !bob.contains(o) && c.contains(o);
      }
    });

    return bob.size()==c.size();
  }

  @Override
  public E[] toArray()
  {
    return newArray(this,size());
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    E[] elementData = newArray(this,_size);

    if (a.length < _size)
      // Make a new array of a's runtime type, but my contents:
      return (T[]) Arrays.copyOf(elementData, _size, a.getClass());

	  System.arraycopy(elementData, 0, a, 0, _size);
    if (a.length > _size)
        a[_size] = null;
    return a;
  }

  @Override
  public boolean add(E e)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException("Tensors are Immutable");
  }

  public static <E> DefaultTensor<E> create(IndexedGetter<E> aIndexedGetter,int[] aIndexSizes)

  {return new DefaultTensor<E>(aIndexedGetter, aIndexSizes, aIndexSizes.length, IndexUtils.calcNumElements(aIndexSizes));}

  public final static <E> DefaultTensor<E> create(Tensor<E> aTensor)
  {
    E[] tensorRep = ArrayUtils.newArray(aTensor,aTensor.size());
    int[] indexSizes = aTensor.indexSizes();
    return DefaultTensor.create(new DefaultIndexGetter<E>(tensorRep,IndexUtils.createBasis(indexSizes)),indexSizes);
  }

  public static final class DefaultIndexGetter<E> implements IndexedGetter<E>
  {
    private final E[] _tensorRepresentation;
    private final int[] _basis;

    DefaultIndexGetter(E[] aTensorRepresentation,
                       int[] aBasis
    )
    {
      _basis = aBasis;
      _tensorRepresentation = aTensorRepresentation;
    }

    @Override
    public E get(int... index)
    {
      return _tensorRepresentation[IndexUtils.flattenTensorIndex(index,_basis)];
    }

    public static <E> Builder getBuilder(final int[] indexSizes)
    {
      @SuppressWarnings("unchecked")//casting an array that only contains nulls is safe
      E[] emptyArrayRepresentation = (E[]) new Object[IndexUtils.calcNumElements(indexSizes)];
      return new Builder<E>(IndexUtils.createBasis(indexSizes),emptyArrayRepresentation);
    }

    public static class Builder<E>
    {
      private final E[] _tensorRepresentation;
      private final int[] _basis;

      private Builder(int[] aBasis,E[] aTensorRepresentation)
      {
        _basis = aBasis;
        _tensorRepresentation = aTensorRepresentation;
      }

      public Builder set(E aElement, int... index)
      {
        _tensorRepresentation[IndexUtils.flattenTensorIndex(index,_basis)] = aElement;
        return this;
      }

      public Builder addAll(Iterable<E> elements)
      {
        int i = 0;
        for (E element : elements)
          _tensorRepresentation[i++] = element;
        return this;
      }

      public DefaultIndexGetter<E> build()
      {
        return new DefaultIndexGetter<E>(_tensorRepresentation, _basis);
      }
    }
  }
}
