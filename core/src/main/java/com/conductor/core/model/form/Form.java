package com.conductor.core.model.form;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "forms")
@Entity
public class Form extends Resource {

    @Lob
    @Column(name = "form_schema")
    private String formSchema;

    @PrePersist
    public void init()
    {
        super.init(ResourceType.FORM,this );
    }

    public static Form createNew(String formSchema){
        return Form.builder().formSchema(formSchema).build();
    }
}
