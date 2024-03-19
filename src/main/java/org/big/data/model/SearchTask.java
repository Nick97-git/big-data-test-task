package org.big.data.model;

public record SearchTask(String rootPath, int depth, String mask, String clientId) {
}
