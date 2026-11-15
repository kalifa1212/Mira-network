package com.hforge.Model.MetaData;

import com.hforge.Model.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "devicemetadata")
@Entity
@EqualsAndHashCode(callSuper=true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMetaData extends AbstractEntity {

    private Long userId;
    private String deviceDetails;
    private String location;
    private Date lastLoggedIn;

}
