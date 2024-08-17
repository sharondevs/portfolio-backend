package com.moonraft.search.domain.command;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode
@NoArgsConstructor
public class Command {
    private String execBy;
    private String execOn;
}
