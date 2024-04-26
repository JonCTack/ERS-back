package com.revature.models;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name="reimbursements")
@Component
public class Reimbursement {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int reimbId;

    private String description;

    private int amount;

    private String status;

    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    @JoinColumn(name="userId", nullable=false)
    private int userId;

    public Reimbursement() {
    }

    public Reimbursement(int reimbId, String description, int amount, String status, int userId) {
        this.reimbId = reimbId;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.userId = userId;
    }

    public int getReimbId() {
        return reimbId;
    }

    public void setReimbId(int reimbId) {
        this.reimbId = reimbId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "reimbursement{" +
                "reimbId=" + reimbId +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", userId=" + userId +
                '}';
    }
}
