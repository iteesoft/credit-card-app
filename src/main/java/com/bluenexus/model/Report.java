package com.bluenexus.model;

import java.math.BigDecimal;
import java.util.List;

public record Report(List<Transaction> transactions, BigDecimal total) {
}
