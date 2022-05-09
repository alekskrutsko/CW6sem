package com.cw6sem.repository;

import com.cw6sem.domain.Role;
import com.cw6sem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String email);
    User findByLoginAndBlocked(String email, boolean blocked);
    @Query("select a from User a where a.role = ?1")
    List<User> findByRole(Role role);
    @Query("select a from User a where a.blocked = ?1 and a.role = 0")
    List<User> filterCustomersByStatus(boolean blocked);
    @Query("select a from User a where a.role = ?1 or a.role=?2")
    List<User> findCustomersAndAppraisers(Role role, Role role2);
    @Query("select a from User a where a.blocked = ?1 and (a.role = 0 or a.role = 1)")
    List<User> filterCustomersAndAppraisersByStatus(boolean blocked);

}
