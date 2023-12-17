package models;



import application.Utilities.LocalDateTimeFormatterHelper;

import java.time.LocalDateTime;

public class Transaction {
    private String id;

    private String description;

//    private Type type;
    private Category category;

    private Float amount;

    private LocalDateTime createdAt;

    private String userId;

    public static enum Category {
        // Income categories
        SALARY, OTHER_INCOME,
        // Expense categories
        HOUSING, UTILITIES, GROCERIES, TRANSPORTATION, INSURANCE, HEALTH_WELLNESS, ENTERTAINMENT

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public float getAmount() {
        // Check if the category is an expense category
        if (getCategory() == Category.HOUSING || getCategory() == Category.UTILITIES || getCategory() == Category.GROCERIES ||
            getCategory() == Category.TRANSPORTATION || getCategory() == Category.INSURANCE || getCategory() == Category.HEALTH_WELLNESS ||
            getCategory() == Category.ENTERTAINMENT)
             {
                return amount * -1;
        }

        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toWriteable() {
        return String.format("%s,%s,%s,%.2f,%s,%s", this.id, this.description, this.category.toString(), this.amount, LocalDateTimeFormatterHelper.format(this.createdAt), this.userId);
    }
}