package com.hforge.Model.MetaData;

import com.hforge.Model.AbstractEntity;
import com.hforge.Model.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "userlocation")
@Entity
@Data @EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation extends AbstractEntity {

    private String country;

    private boolean enabled;

    @ManyToOne(targetEntity = Utilisateur.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Utilisateur user;
}
