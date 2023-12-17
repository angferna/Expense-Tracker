package Services;



import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;

import java.util.LinkedHashMap;
import java.util.Map;

import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import models.Transaction;
import models.Transaction.Category;
import repositories.TransactionRepository;

public class TransactionService {

    private TransactionRepository transactionRepository = new TransactionRepository();

    public Transaction create(String description, Float amount, Transaction.Category category, String userId, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setDescription(description);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setCreatedAt(date.atStartOfDay());
        transaction.setUserId(userId);

        try {
            transactionRepository.create(transaction);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transaction;
    }
    
    public void update(String id, String description, Float amount, Transaction.Category category) {
        try {
            Transaction transaction = transactionRepository.getById(id);

            if (transaction != null) {
                transaction.setDescription(description);
                transaction.setAmount(amount);
                transaction.setCategory(category);
                transactionRepository.update(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        try {
            Transaction transaction = transactionRepository.getById(id);

            if (transaction != null) {
                transactionRepository.delete(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Data> getIncomePieChartData(String uuid) throws IOException {
        List<PieChart.Data> incomePieChartData = new ArrayList<>();
        List<Transaction> transactions = getByUserId(uuid);

        // Income categories
        Map<Category, Float> incomeData = new LinkedHashMap<>();
        incomeData.put(Category.SALARY, 0f);
        //incomeData.put(Category.INVESTMENT_INCOME, 0f);
        incomeData.put(Category.OTHER_INCOME, 0f);

        transactions.stream()
                .filter(t -> t.getAmount() >= 0)
                .forEach(t -> incomeData.put(t.getCategory(), incomeData.get(t.getCategory()) + t.getAmount()));

        incomeData.forEach((category, sum) -> incomePieChartData.add(new Data(category.toString(), Math.floor(sum))));

        return incomePieChartData;
    }

    public List<Data> getExpensePieChartData(String uuid) throws IOException {
        List<PieChart.Data> expensePieChartData = new ArrayList<>();
        List<Transaction> transactions = getByUserId(uuid);

        // Expense categories
        Map<Category, Float> expenseData = new LinkedHashMap<>();
        expenseData.put(Category.HOUSING, 0f);
        expenseData.put(Category.UTILITIES, 0f);
        expenseData.put(Category.GROCERIES, 0f);
        expenseData.put(Category.TRANSPORTATION, 0f);
        expenseData.put(Category.INSURANCE, 0f);
        expenseData.put(Category.HEALTH_WELLNESS, 0f);
        expenseData.put(Category.ENTERTAINMENT, 0f);

        transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .forEach(t -> expenseData.put(t.getCategory(), expenseData.get(t.getCategory()) - t.getAmount()));

        expenseData.forEach((category, sum) -> expensePieChartData.add(new Data(category.toString(), Math.floor(sum))));

        return expensePieChartData;
    }




    public List<Transaction> search(String description) throws IOException {
        Predicate<Transaction> descriptionPredicate = d -> d.getDescription().contains(description);

        return transactionRepository.all().stream()
                .filter(descriptionPredicate)
                .collect(Collectors.toList());
    }

    public List<Transaction> getByUserId(String uuid) throws IOException {
        Predicate<Transaction> userUuidPredicate = d -> d.getUserId().equalsIgnoreCase(uuid);

        return transactionRepository.all().stream()
                .filter(userUuidPredicate)
                .collect(Collectors.toList());
    }
}
