/*
🧩 Problem Statement (from the image)
You are given a list of transactions, each transaction has:
fee
size
You are given a blockSize.

Goal:
Choose a subset of transactions such that:

The total size of selected transactions ≤ blockSize

The total fee is maximized.

But there's more:

Follow-up:
Transactions can now have parent-child relationships.

Meaning: If a child transaction is selected, its parent must also be selected first.
 */
package com.machinecoding.KnapSackProblem;

import java.util.*;

class Transaction{
    int id, fee, size;
    List<Transaction> children;

    Transaction(int id, int fee, int size, List<Transaction> children) {
        this.id = id;
        this.fee = fee;
        this.size = size;
        this.children = children;

    }

    public double getFeePerSize(){
        return (double) (this.fee / this.size);
    }
    public void addChild(Transaction child){
        children.add(child);
    }

    public String toString(){
        return "[" + id + ", " + fee + ", " + size + ", " + children + "]";
    }
}

interface IFeeCalculator{
    List<Transaction> calculateMaxFee(List<Transaction> transactions, int blockSize);
}


class DPFeeCalculator implements IFeeCalculator{
    @Override
    public List<Transaction> calculateMaxFee(List<Transaction> transactions, int blockSize) {
        int[][] dp = new int[transactions.size() + 1][blockSize + 1];
        // state : dp[i][sz] represents the max fee generated using [0:i] transactions and sz size block


        for(int i = 1; i <= transactions.size(); i++){
            for(int sz = 0; sz <= blockSize; sz++){
                if(sz >= transactions.get(i - 1).size)
                dp[i][sz] = transactions.get(i - 1).fee + dp[i - 1][sz - transactions.get(i - 1).size];
                dp[i][sz] = Math.max(dp[i][sz], dp[i - 1][sz]);
            }
        }

        System.out.println(dp[transactions.size()][blockSize]);

        return null;
    }
}

class GreedyFeeCalculator implements IFeeCalculator{


    @Override
    public List<Transaction> calculateMaxFee(List<Transaction> transactions, int blockSize) {

        transactions.sort((t1, t2) -> Double.compare(t2.getFeePerSize(), t1.getFeePerSize()));
        List<Transaction> result = new ArrayList<Transaction>();

        int currentSize = 0;
        int totalFee = 0;

        for(int i = 0; i < transactions.size(); i++){
            if(currentSize + transactions.get(i).size <= blockSize){
                totalFee += transactions.get(i).fee;
                currentSize += transactions.get(i).size;
                result.add(transactions.get(i));
            }
            else break;
        }

        return result;
    }
}

class TopoLogicalGreedyFeeCalculator implements IFeeCalculator{
    @Override
    public List<Transaction> calculateMaxFee(List<Transaction> transactions, int blockSize) {

        Map<Transaction, Integer> indegree = new HashMap<>();
        PriorityQueue<Transaction> pq = new PriorityQueue<>((t1, t2) -> Double.compare(t2.getFeePerSize(), t1.getFeePerSize()));


        for(Transaction t : transactions){
            indegree.putIfAbsent(t, 0);
            for(Transaction c: t.children) {
                indegree.put(c, indegree.getOrDefault(c, 0) + 1);
            }
        }

        for(Map.Entry<Transaction, Integer> e: indegree.entrySet()){
            if(e.getValue() == 0){
                pq.add(e.getKey());
            }
        }
        int remainingSize = blockSize;
        List<Transaction> result = new ArrayList<Transaction>();
        while(!pq.isEmpty() && remainingSize > 0){
            Transaction t = pq.poll();


            if(t.size <= remainingSize){
                remainingSize -= t.size;
                result.add(t);
            }

            for(Transaction c: t.children){
                indegree.put(c, indegree.getOrDefault(c, 0) - 1);
                if(indegree.get(c) == 0){
                    pq.add(c);
                }
            }

        }

        return result;
    }

}


public class KnapSackProblemDemo {

    public static void main(String[] args) {
        Transaction t1 = new Transaction(1, 10, 1, new ArrayList<>());
        Transaction t2 = new Transaction(2, 5, 2, new ArrayList<>());
        Transaction t3 = new Transaction(3, 3, 3, new ArrayList<>());

        int blockSize = 5;


        IFeeCalculator feeCalculator = new DPFeeCalculator();
        IFeeCalculator greedyFeeCalculator = new GreedyFeeCalculator();

        List<Transaction> bestDP = feeCalculator.calculateMaxFee(List.of(t1, t2, t3), blockSize);
        System.out.println(" Best Result with DP: " + bestDP);

        List<Transaction> bestGreedy = greedyFeeCalculator.calculateMaxFee(Arrays.asList(t1, t2, t3), blockSize);
        bestGreedy.forEach(System.out::println);


    }

}
