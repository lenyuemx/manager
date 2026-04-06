package com.example.common.enums;

public enum LevelEnum {
    //社长
    HEADER("社长"),
    //学生
    STUDENT("学生"),
    //教师
    TEACHER("教师"),
    ;

    public String level;

    LevelEnum(String level) {
        this.level = level;
    }
}
