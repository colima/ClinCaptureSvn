package org.akaza.openclinica.domain.datamap;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.user.UserAccount;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * StudyEvent.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "study_event")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "study_event_study_event_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

public class StudyEvent extends DataMapDomainObject {

    private int studyEventId;
    private UserAccount userAccount;
    private StudyEventDefinition studyEventDefinition;
    private StudySubject studySubject;
    private Integer statusId;
    private String location;
    private Integer sampleOrdinal;
    private Date dateStart;
    private Date dateEnd;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private Integer subjectEventStatusId;
    private Boolean startTimeFlag;
    private Boolean endTimeFlag;
    private List<DnStudyEventMap> dnStudyEventMaps;
    private List<EventCrf> eventCrfs;

    @Id
    @Column(name = "study_event_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getStudyEventId() {
        return this.studyEventId;
    }

    public void setStudyEventId(int studyEventId) {
        this.studyEventId = studyEventId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_event_definition_id")

    public StudyEventDefinition getStudyEventDefinition() {
        return this.studyEventDefinition;
    }

    public void setStudyEventDefinition(
            StudyEventDefinition studyEventDefinition) {
        this.studyEventDefinition = studyEventDefinition;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_subject_id")
    public StudySubject getStudySubject() {
        return this.studySubject;
    }

    public void setStudySubject(StudySubject studySubject) {
        this.studySubject = studySubject;
    }

    @Column(name = "location", length = 2000)
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name = "sample_ordinal")

    public Integer getSampleOrdinal() {
        return this.sampleOrdinal;
    }

    public void setSampleOrdinal(Integer sampleOrdinal) {
        this.sampleOrdinal = sampleOrdinal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_start", length = 8)
    public Date getDateStart() {
        return this.dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_end", length = 8)
    public Date getDateEnd() {
        return this.dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", length = 4)
    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Temporal(TemporalType.TIMESTAMP)
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

    @Column(name = "subject_event_status_id")
    public Integer getSubjectEventStatusId() {
        return this.subjectEventStatusId;
    }

    public void setSubjectEventStatusId(Integer subjectEventStatusId) {
        this.subjectEventStatusId = subjectEventStatusId;
    }

    @Column(name = "start_time_flag")
    public Boolean getStartTimeFlag() {
        return this.startTimeFlag;
    }

    public void setStartTimeFlag(Boolean startTimeFlag) {
        this.startTimeFlag = startTimeFlag;
    }

    @Column(name = "end_time_flag")
    public Boolean getEndTimeFlag() {
        return this.endTimeFlag;
    }

    public void setEndTimeFlag(Boolean endTimeFlag) {
        this.endTimeFlag = endTimeFlag;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studyEvent")
    public List<DnStudyEventMap> getDnStudyEventMaps() {
        return this.dnStudyEventMaps;
    }

    public void setDnStudyEventMaps(List<DnStudyEventMap> dnStudyEventMaps) {
        this.dnStudyEventMaps = dnStudyEventMaps;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studyEvent")
    public List<EventCrf> getEventCrfs() {
        return this.eventCrfs;
    }

    public void setEventCrfs(List<EventCrf> eventCrfs) {
        this.eventCrfs = eventCrfs;
    }

    @Column(name = "status_id")
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

}
