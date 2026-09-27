package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.DeliveryDropLocation;
import com.manacommunity.safety.domain.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    private Long residentId;

    @Column(nullable = false, length = 100)
    private String courierCompany; // Amazon, Flipkart, Swiggy, Zomato, DHL, etc.

    @Column(length = 100)
    private String deliveryAgentName;

    @Column(length = 20)
    private String deliveryAgentPhone;

    private Integer packageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DeliveryDropLocation dropLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryStatus status;

    @Column(length = 50)
    private String lockerSlot;

    @Column(length = 10)
    private String collectionOtp;

    @Column(length = 500)
    private String packagePhotoUrl;

    private LocalDateTime gateArrivedAt;

    private LocalDateTime collectedAt;

    private Long collectedByUserId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
