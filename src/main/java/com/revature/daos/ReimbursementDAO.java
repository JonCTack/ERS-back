package com.revature.daos;

import com.revature.models.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReimbursementDAO extends JpaRepository<Reimbursement, Integer> {
    public Optional<Reimbursement> findById(int reimbId);
    public List<Reimbursement> findByUserUserId(int userId);
    public List<Reimbursement> findByStatus(String status);
    public List<Reimbursement> findByStatusAndUserUserId(String status, int userId);
}
