package com.cw6sem.repository;

import com.cw6sem.domain.Status;
import com.cw6sem.entity.AppraisalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppraisalAgreementRepository extends JpaRepository<AppraisalAgreement, Long> {

    @Query("select a from AppraisalAgreement a where a.customer.login = ?1")
    List<AppraisalAgreement> findAllByUser(String email);

    @Query("select a from AppraisalAgreement a where a.objectToAppraise.id = ?1")
    List<AppraisalAgreement> getByObjectId(Long id);

    @Query("select a from AppraisalAgreement a where a.objectToAppraise.objectType.id = ?1")
    List<AppraisalAgreement> getByTypeId(Long id);

    @Query("select a from AppraisalAgreement a where a.commentFromAppraiser like %?1% or a.commentFromCustomer like %?1% or a.dateOfSigning like %?1%" +
            " or concat(a.expectedPrice,'') like %?1% or concat(a.appraiserPrice,'') like %?1% or concat(a.priceForAppraisal,'') like %?1%" +
            "or concat(a.objectToAppraise.objectType.type,'') like %?1%")
    List<AppraisalAgreement> findAllSuitable(String str);

    @Query("select a.status from AppraisalAgreement a where a.id = ?1")
    Status findStatusById(Long id);

    @Query("select a from AppraisalAgreement a where a.status = ?1")
    List<AppraisalAgreement> filterByStatus(Status status);

    @Query("select AVG(a.appraiserPrice) from AppraisalAgreement a where a.objectToAppraise.description = ?1 and a.objectToAppraise.objectType.type = ?2")
    Double countStat(String description, String type);


    @Query(value = "select SUM(a.price_for_appraisal) FROM appraisalagreement a WHERE a.price_for_appraisal != 0 AND a.date_of_signing is not null AND MONTH(a.date_of_signing)=MONTH(?1) AND YEAR(a.date_of_signing) = YEAR(?1) AND status != 3", nativeQuery = true)
    Double lineChart(LocalDate date);

    @Query("select a.appraiser.id, count(a) as stat from AppraisalAgreement a where a.status = 4 group by a.appraiser.user.id order by stat desc")
    List<String> getBestAppraisers();

    @Query("select a.dateOfSigning, a.expectedPrice, a.appraiserPrice FROM AppraisalAgreement a INNER JOIN ObjectToAppraise o2 on a.objectToAppraise.id = o2.id INNER JOIN ObjectType o on o2.objectType.id = o.id  WHERE a.customer.id = ?1 AND a.dateOfSigning is not null AND a.status != 3")
    List<List<Object>> barChart(Long id);
}

