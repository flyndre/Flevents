package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.UniqueElements;

import java.net.URI;
import java.sql.Blob;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FleventsAccount {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String icon;

    private String secret;

    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<EventRegistration> events;
    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<OrganizationAccount> organisations;

    public FleventsAccount(String uuid){
        this.uuid=uuid;
    }

    public void merge(FleventsAccount account){
        if(account.getFirstname()!=null){
            this.firstname=account.getFirstname();
        }
        if(account.getLastname()!=null){
            this.lastname=account.getLastname();
        }
        if(account.getEmail()!=null){
            this.email=account.getEmail();
        }
        if(account.getIcon()!=null){
            this.icon=account.getIcon();
        }
        if(account.secret!=null){
            this.secret=account.getSecret();
        }
    }



}
