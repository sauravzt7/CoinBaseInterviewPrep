package com.machinecoding.IteratorQuestion;
import java.util.*;

interface CustomIterator<T> extends Iterator<T> {
    @Override
    boolean hasNext();
    @Override
    T next();
}

class OddIterator implements CustomIterator<Integer>{
    int current = 1;
    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        int val = current;
        current += 2;
        return val;
    }
}

class EvenIterator implements CustomIterator<Integer>{
    int current = 0;
    @Override
    public boolean hasNext() {
        return true; // infinite streams
    }

    @Override
    public Integer next() {
        int val = current;
        current += 2;
        return val;
    }
}

class RangeIterator implements CustomIterator<Integer>{
    int start;
    int end;
    int increment;

    public RangeIterator(int start, int end, int increment) {
        this.start = start;
        this.end = end;
        this.increment = increment;
    }

    @Override
    public boolean hasNext() {
        if(start >= end)return false;
        return true;
    }

    @Override
    public Integer next() {

        int curr = start;
        start += increment;
        return curr;
    }
}

class InterleavingIterator<T> implements CustomIterator<T>{

    List<Iterator<T>> iterators;
    Queue<Iterator> queue;

    public InterleavingIterator(List<Iterator<T>> iterators) {
        this.iterators = iterators;
        this.queue = new LinkedList<>();

        for(Iterator<T> iterator : iterators) {
            queue.offer(iterator);
        }

    }
    @Override
    public boolean hasNext() {
        assert !queue.isEmpty();
        assert queue.peek() != null;
        return queue.peek().hasNext();
    }

    @Override
    public T next() {
        Iterator iterator = queue.poll();
        if(iterator.hasNext()){
            queue.offer(iterator);
        }

        return (T) iterator.next();
    }
}

public class CustomIteratorTest{
    public static void main(String[] args) {
        Iterator<Integer> oddIterator = new OddIterator();
        Iterator<Integer> evenIterator = new EvenIterator();

        while(oddIterator.hasNext()) {
            System.out.println("Odd: " + oddIterator.next());
        }

        Iterator<Integer> rangeIterator = new RangeIterator(0, 10, 2);

        System.out.println("Range Iterator");
        while(rangeIterator.hasNext()){
            System.out.println( rangeIterator.next());
        }

        List<Integer> list1 = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> list2 = new ArrayList<>(List.of(6, 7, 8, 9, 10));
        List<Integer> list3 = new ArrayList<>(List.of(11, 12, 13, 14, 15));


        List<Iterator<Integer>> iterators = new ArrayList<>(List.of(list1.iterator(), list2.iterator(), list3.iterator()));

        InterleavingIterator ii = new InterleavingIterator(iterators);

        while(ii.hasNext()){
            System.out.print(ii.next() + " ");
        }

    }
}

