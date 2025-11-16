package com.lifelibrarians.lifebookshelf.classification.domain;

/**
 * theme.name ENUM 값 매핑용
 * DB ENUM('family','love', ... ) 과 일치하도록 소문자로 맞춤.
 */
public enum ThemeNameType {
    family,
    love,
    caring,
    local,
    trait,
    friend,
    growing,
    event,
    money,
    hobby,
    pet,
    philosophy,
    parent
}
