package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * DnStudySubjectMapId.
 */
@Embeddable
@SuppressWarnings("serial")
public class DnStudySubjectMapId extends DataMapDomainObject {

    private Integer studySubjectId;
    private Integer discrepancyNoteId;
    private String columnName;

    @Column(name = "study_subject_id")
    public Integer getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setStudySubjectId(Integer studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    @Column(name = "discrepancy_note_id")
    public Integer getDiscrepancyNoteId() {
        return this.discrepancyNoteId;
    }

    public void setDiscrepancyNoteId(Integer discrepancyNoteId) {
        this.discrepancyNoteId = discrepancyNoteId;
    }

    @Column(name = "column_name")
    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof DnStudySubjectMapId))
            return false;
        DnStudySubjectMapId castOther = (DnStudySubjectMapId) other;

        return ((this.getStudySubjectId() == castOther.getStudySubjectId())
                || (this.getStudySubjectId() != null
                && castOther.getStudySubjectId() != null
                && this.getStudySubjectId().equals(castOther.getStudySubjectId())))
                && ((this.getDiscrepancyNoteId() == castOther.getDiscrepancyNoteId())
                || (this.getDiscrepancyNoteId() != null
                && castOther.getDiscrepancyNoteId() != null && this.getDiscrepancyNoteId().equals(castOther.getDiscrepancyNoteId())))
                && ((this.getColumnName() == castOther.getColumnName()) || (this.getColumnName() != null
                && castOther.getColumnName() != null
                && this.getColumnName().equals(castOther.getColumnName())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + (getStudySubjectId() == null ? 0 : this.getStudySubjectId().hashCode());
        result = 37 * result + (getDiscrepancyNoteId() == null ? 0 : this.getDiscrepancyNoteId().hashCode());
        result = 37 * result + (getColumnName() == null ? 0 : this.getColumnName().hashCode());
        return result;
    }
}
