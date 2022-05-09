package com.cw6sem.entity;

import com.cw6sem.domain.Status;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="appraisalagreement")
public class AppraisalAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", referencedColumnName = "id", nullable = false)
    private ObjectToAppraise objectToAppraise;
    private Double priceForAppraisal;
    private Double expectedPrice;
    private Double appraiserPrice;
    private String dateOfSigning;
    private String commentFromCustomer;
    private String commentFromAppraiser;
    private Status status;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User customer;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "appraiser_id", referencedColumnName = "id")
    private Appraiser appraiser;

    @Transient
    private String strStatus;

    public void setStrStatus(Status status) {
        if (status == Status.WAITFORCUSTOMER) {
            strStatus = "Ожидание ответа заказчика";
        } else if (status == Status.WAITFORAPPRAISER) {
            strStatus = "Ожидание ответа оценщика";
        } else if (status == Status.TERMINATED) {
            strStatus = "Расторгнут";
        } else if (status == Status.SIGNED) {
            strStatus = "Заключен";
        }
    }
}
