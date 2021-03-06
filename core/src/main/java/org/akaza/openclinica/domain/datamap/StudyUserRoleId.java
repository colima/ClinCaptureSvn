package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

/**
 * StudyUserRoleId.
 */
@Embeddable
public class StudyUserRoleId extends AbstractMutableDomainObject {

    private String roleName;
    private Integer studyId;
    private Integer statusId;
    private Integer ownerId;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private String userName;

    public StudyUserRoleId() {
    }

    @Column(name = "role_name", length = 40)
    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Column(name = "study_id")
    public Integer getStudyId() {
        return this.studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @Column(name = "status_id")
    public Integer getStatusId() {
        return this.statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    @Column(name = "owner_id")
    public Integer getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name = "date_created", length = 4)
    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "date_updated", length = 4)
    public Date getDateUpdated() {
        return this.dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Column(name = "update_id")
    public Integer getUpdateId() {
        return this.updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    @Column(name = "user_name", length = 40)
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof StudyUserRoleId))
            return false;
        StudyUserRoleId castOther = (StudyUserRoleId) other;

        return ((this.getRoleName() == castOther.getRoleName())
                || (this.getRoleName() != null
                && castOther.getRoleName() != null
                && this.getRoleName().equals(castOther.getRoleName())))
                && ((this.getStudyId() == castOther.getStudyId())
                || (this.getStudyId() != null
                && castOther.getStudyId() != null && this.getStudyId().equals(castOther.getStudyId())))
                && ((this.getStatusId() == castOther.getStatusId())
                || (this.getStatusId() != null
                && castOther.getStatusId() != null
                && this.getStatusId().equals(castOther.getStatusId())))
                && ((this.getOwnerId() == castOther.getOwnerId())
                || (this.getOwnerId() != null && castOther.getOwnerId() != null
                && this.getOwnerId().equals(castOther.getOwnerId())))
                && ((this.getDateCreated() == castOther.getDateCreated())
                || (this.getDateCreated() != null
                && castOther.getDateCreated() != null
                && this.getDateCreated().equals(castOther.getDateCreated())))
                && ((this.getDateUpdated() == castOther.getDateUpdated())
                || (this.getDateUpdated() != null
                && castOther.getDateUpdated() != null
                && this.getDateUpdated().equals(castOther.getDateUpdated())))
                && ((this.getUpdateId() == castOther.getUpdateId())
                || (this.getUpdateId() != null
                && castOther.getUpdateId() != null
                && this.getUpdateId().equals(castOther.getUpdateId())))
                && ((this.getUserName() == castOther.getUserName())
                || (this.getUserName() != null
                && castOther.getUserName() != null
                && this.getUserName().equals(castOther.getUserName())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + (getRoleName() == null ? 0 : this.getRoleName().hashCode());
        result = 37 * result + (getStudyId() == null ? 0 : this.getStudyId().hashCode());
        result = 37 * result + (getStatusId() == null ? 0 : this.getStatusId().hashCode());
        result = 37 * result + (getOwnerId() == null ? 0 : this.getOwnerId().hashCode());
        result = 37 * result + (getDateCreated() == null ? 0 : this.getDateCreated().hashCode());
        result = 37 * result + (getDateUpdated() == null ? 0 : this.getDateUpdated().hashCode());
        result = 37 * result + (getUpdateId() == null ? 0 : this.getUpdateId().hashCode());
        result = 37 * result + (getUserName() == null ? 0 : this.getUserName().hashCode());
        return result;
    }

}
