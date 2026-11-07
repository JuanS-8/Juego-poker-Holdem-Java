package com.poker.dto;

import java.io.Serializable;

public record CartaDTO(String valor, String palo) implements Serializable {
}
