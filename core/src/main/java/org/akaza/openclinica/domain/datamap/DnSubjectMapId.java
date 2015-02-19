package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * DnSubjectMapId.
 */
@Embeddable
public class DnSubjectMapId extends DataMapDomainObject {

    private Integer subjectId;
    private Integer discrepancyNoteId;
    private String columnName;

    @Column(name = "subject_id")
    public Integer getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
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
        if (!(other instanceof DnSubjectMapId))
            return false;
        DnSubjectMapId castOther = (DnSubjectMapId) other;

        return ((this.getSubjectId() == castOther.getSubjectId())
                || (this.getSubjectId() != null
                && castOther.getSubjectId() != null && this.getSubjectId().equals(castOther.getSubjectId())))
                && ((this.getDiscrepancyNoteId() == castOther.getDiscrepancyNoteId())
                || (this.getDiscrepancyNoteId() != null
                && castOther.getDiscrepancyNoteId() != null
                && this.getDiscrepancyNoteId().equals(castOther.getDiscrepancyNoteId())))
                && ((this.getColumnName() == castOther.getColumnName())
                || (this.getColumnName() != null
                && castOther.getColumnName() != null
                && this.getColumnName().equals(castOther.getColumnName())));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (getSubjectId() == null ? 0 : this.getSubjectId().hashCode());
        result = 37 * result + (getDiscrepancyNoteId() == null ? 0 : this.getDiscrepancyNoteId().hashCode());
        result = 37 * result + (getColumnName() == null ? 0 : this.getColumnName().hashCode());
        return result;
    }
}