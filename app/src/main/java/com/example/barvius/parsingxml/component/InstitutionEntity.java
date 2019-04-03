package com.example.barvius.parsingxml.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionEntity {
    private long id;
    private String key;
    private String url;
    private NameTagEntity nameTagEntity = new NameTagEntity();
    private LocationTagEntity locationTagEntity = new LocationTagEntity();
    private IdentifiersTagEntity identifiersTagEntity = new IdentifiersTagEntity();
}
