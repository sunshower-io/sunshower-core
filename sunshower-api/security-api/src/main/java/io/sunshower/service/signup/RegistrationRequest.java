package io.sunshower.service.signup;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.*;

import io.sunshower.common.crypto.Multihash;
import io.sunshower.common.crypto.Hashes;
import io.sunshower.model.core.auth.User;
import io.sunshower.persistence.core.DistributableEntity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Created by haswell on 11/17/16.
 */
@Entity
@Table(name = "REGISTRATION_REQUEST")
@XmlRootElement(name = "request")
public class RegistrationRequest extends DistributableEntity {

    @XmlElement
    @Column(name = "request_id")
    private String requestId;

    @OneToOne(
            cascade =
                    CascadeType.PERSIST
    )
    @XmlElement
    @JoinColumn(name =  "USER_ID")
    private User user;

    @XmlAttribute
    @Temporal(TemporalType.DATE)
    private Date requested;

    @XmlAttribute
    @Temporal(TemporalType.DATE)
    private Date expires;

    public RegistrationRequest() {

    }




    public RegistrationRequest(User user) {
        this.user = user;
        this.setDefaults();
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getRequested() {
        return requested;
    }

    public void setRequested(Date requested) {
        this.requested = requested;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }


    public String getRequestId() {
        return requestId;
    }

    @Override
    protected void setDefaults() {
        this.requested = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(requested);
        calendar.add(Calendar.DATE, 1);
        this.expires = calendar.getTime();
        this.requestId = Hashes
                .create(Multihash.Type.SHA_2_512)
                .hash(user, requested, expires);
    }
}
