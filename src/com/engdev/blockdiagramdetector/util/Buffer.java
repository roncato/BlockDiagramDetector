package com.engdev.blockdiagramdetector.util;

import java.util.*;

public class Buffer<T> {
    private List<T> repository = null;
    private int size = 0;

    public Buffer(int size) {
        repository = new LinkedList<T>();
        this.size = size;
    }

    public boolean add(T object) {
        if (repository.size() == size)
            repository.remove(0);
        return repository.add(object);
    }

    public void flush() {
        repository.clear();
    }

    public boolean contains(T object) {
        return repository.contains(object);
    }

    public boolean containsAll(Collection<T> arg0) {
        return repository.containsAll(arg0);
    }

    public T get(int location) {
        return repository.get(location);
    }

    public int bufferedSize() {
        return repository.size();
    }

    public T peek(int offSet) {
        return repository.get(repository.size() - 1 - offSet);
    }

    public int indexOf(Object object) {
        return repository.indexOf(object);
    }

    public boolean isEmpty() {
        return repository.isEmpty();
    }

    public Iterator<T> iterator() {
        return repository.iterator();
    }

    public int lastIndexOf(T object) {
        return repository.lastIndexOf(object);
    }

    public ListIterator<T> listIterator() {
        return repository.listIterator();
    }

    public ListIterator<T> listIterator(int location) {
        return repository.listIterator(location);
    }

    public T remove(int location) {
        return repository.remove(location);
    }

    public boolean remove(T object) {
        return repository.remove(object);
    }

    public boolean removeAll(Collection<T> arg0) {
        return repository.removeAll(arg0);
    }

    public boolean retainAll(Collection<T> arg0) {
        return repository.retainAll(arg0);
    }

    public Object set(int location, T object) {
        return repository.set(location, object);
    }

    public int size() {
        return size;
    }

    public Object[] toArray() {
        return repository.toArray();
    }

    public Object[] toArray(Object[] array) {
        return repository.toArray(array);
    }

    @Override
    public String toString() {
        String s = "[";
        for (int i = 0; i < bufferedSize(); i++) {
            s += repository.get(i).toString();
            if (i < bufferedSize() - 1)
                s += ", ";
        }
        return s + "]";
    }

}
