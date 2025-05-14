package com.machinecoding.TransactionQuestoin;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Transaction{
    String transactionId;
    String userId;
    Double amount;
    LocalDateTime timestamp;

    public Transaction(String transactionId, String userId, Double amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String toString(){
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}

enum Operator{
    EQUAL,
    GREATER_THAN,
    LESS_THAN
}
interface Criteria{
    List<Transaction> satisfy(List<Transaction> transactions);
}


class UserFilterCriteria implements Criteria{

    String userId;
    public UserFilterCriteria(String userId) {
        this.userId = userId;
    }

    @Override
    public List<Transaction> satisfy(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.userId.equals(this.userId))
                .collect(Collectors.toList());
    }
}



class AmountFilterCriteria implements Criteria{
    Operator operator;
    double amount;

    public AmountFilterCriteria(Operator operator, double amount) {
        this.operator = operator;
        this.amount = amount;
    }


    @Override
    public List<Transaction> satisfy(List<Transaction> transactions) {

        return switch (operator){
            case EQUAL -> transactions.stream().filter(transaction -> transaction.amount == this.amount).collect(Collectors.toList());
            case GREATER_THAN -> transactions.stream().filter(transaction -> transaction.amount > this.amount).collect(Collectors.toList());
            case LESS_THAN -> transactions.stream().filter(transaction -> transaction.amount < this.amount).collect(Collectors.toList());
            default -> throw new IllegalStateException("Unexpected value: " + operator);
        };
    }
}

class TimeStampFilterCriteria implements Criteria {
    Operator operator;
    LocalDateTime timestamp;

    public TimeStampFilterCriteria(Operator operator, LocalDateTime timestamp) {
        this.operator = operator;
        this.timestamp = timestamp;
    }

    @Override
    public List<Transaction> satisfy(List<Transaction> transactions) {
        return switch (operator) {
            case EQUAL -> transactions.stream()
                    .filter(transaction -> transaction.timestamp.equals(this.timestamp))
                    .collect(Collectors.toList());
            case GREATER_THAN -> transactions.stream()
                    .filter(transaction -> transaction.timestamp.isAfter(this.timestamp))
                    .collect(Collectors.toList());
            case LESS_THAN -> transactions.stream()
                    .filter(transaction -> transaction.timestamp.isBefore(this.timestamp))
                    .collect(Collectors.toList());
            default -> transactions;
        };
    }
}

class ANDFilter implements Criteria{

    List<Criteria> criteriaList;
    public ANDFilter(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    @Override
    public List<Transaction> satisfy(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> criteriaList.stream().allMatch(
                        criteria -> !criteria.satisfy(List.of(transaction)).isEmpty()
                )).collect(Collectors.toList());
    }
}

class ORFilter implements Criteria{

    List<Criteria> criteriaList;
    public ORFilter(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    @Override
    public List<Transaction> satisfy(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> criteriaList.stream().anyMatch(
                        criteria -> !criteria.satisfy(List.of(transaction)).isEmpty()
                )).collect(Collectors.toList());
    }
}

interface Pagination{
    List<Transaction> paginate(String transactionId, int pageSize);
}

class TransactionRepository{

    private List<Transaction> transactions = List.of(
            new Transaction("T1", "user1", 100.0, LocalDateTime.now()),
            new Transaction("T2", "user2", 200.0, LocalDateTime.now()),
            new Transaction("T3", "user3", 300.0, LocalDateTime.now()),
            new Transaction("T4", "user4", 400.0, LocalDateTime.now()),
            new Transaction("T5", "user5", 500.0, LocalDateTime.now())
    );

    //fetch the first page of transactions
    public List<Transaction> findFirstTransactions(int pageSize){
        return transactions.subList(0, Math.min(pageSize, transactions.size()));
    }

    public List<Transaction> findTransactionAfterId(String transactionId, int pageSize){

        int index = -1;
        List<Transaction> sortedTransactions = transactions.stream().sorted(Comparator.comparing(t -> t.transactionId)).toList();
        // we can use binary search to find the index of the transactionId
        index = Collections.binarySearch(sortedTransactions, new Transaction(transactionId, "", 0.0, LocalDateTime.now()), (t1, t2) -> t1.transactionId.compareTo(t2.transactionId));
        System.out.println("Index: " + index);
        if(index == -1){
            return Collections.emptyList();
        }
        return transactions.subList(index + 1, Math.min(index + 1 + pageSize, transactions.size()));
    }

}

class TransactionPagination {

    TransactionRepository repository;
    public TransactionPagination() {
        this.repository = new TransactionRepository();
    }

    public List<Transaction> getTransactionsWithCursorPagination(String lastTransactionId, int pageSize) {

        if(lastTransactionId == null || lastTransactionId.isEmpty()){
            return repository.findFirstTransactions(pageSize);
        }

        return repository.findTransactionAfterId(lastTransactionId, pageSize);

    }
}


public class TransactionFilterAndPagination {

        public static void main(String[] args) {
//            Transaction transaction1 = new Transaction("1", "user1", 100.0, LocalDateTime.now());
//            Transaction transaction2 = new Transaction("2", "user2", 200.0, LocalDateTime.now());
//            Transaction transaction3 = new Transaction("3", "user3", 300.0, LocalDateTime.now());
//            Transaction transaction4 = new Transaction("4", "user4", 400.0, LocalDateTime.now());
//
//
//            Criteria c1 = new UserFilterCriteria("user1");
//            Criteria c2 = new UserFilterCriteria("user2");
//            Criteria c3 = new TimeStampFilterCriteria(Operator.GREATER_THAN, LocalDateTime.now().minusDays(5));
//
//            List<Transaction> filteredTransactions = c3.satisfy(List.of(transaction1, transaction2, transaction3, transaction4));
//
//            Criteria c4 = new ANDFilter(List.of(c1, c2, c3));
//
//            List<Transaction> filteredTransactionByAND = c4.satisfy(List.of(transaction1, transaction2));
//
//
//
//
//            System.out.println("Filtered Transactions: " + filteredTransactions);
//            System.out.println("Filtered Transactions by AND: " + filteredTransactionByAND);

            //

            TransactionPagination transactionPagination = new TransactionPagination();
            List<Transaction> transactions = transactionPagination.getTransactionsWithCursorPagination(null, 2);
            System.out.println("First Page Transactions: " + transactions);

            transactions = transactionPagination.getTransactionsWithCursorPagination("T2", 2);
            System.out.println("Second Page Transactions: " + transactions);


        }

}


//public class TransactionPaginationTest {
//
//    public static void main(String[] args) {
//
//
//
//    }
//
//
//}
