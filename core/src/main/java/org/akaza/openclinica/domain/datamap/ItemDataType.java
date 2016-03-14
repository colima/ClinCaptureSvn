package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.List;

/**
 * ItemDataType.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "item_data_type")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence_name", value = "item_data_type_item_data_type_id_seq")})
public class ItemDataType extends DataMapDomainObject {

    private int itemDataTypeId;
    private String code;
    private String name;
    private String definition;
    private String reference;
    private List<Item> items;

    @Id
    @Column(name = "item_data_type_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")

    public int getItemDataTypeId() {
        return this.itemDataTypeId;
    }

    public void setItemDataTypeId(int itemDataTypeId) {
        this.itemDataTypeId = itemDataTypeId;
    }

    @Column(name = "code", length = 20)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "definition", length = 1000)
    public String getDefinition() {
        return this.definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Column(name = "reference", length = 1000)
    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "itemDataType")
    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
