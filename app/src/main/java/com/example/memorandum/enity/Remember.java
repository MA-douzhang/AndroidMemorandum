package com.example.memorandum.enity;

import lombok.Data;

@Data
public class Remember {
    private String title;
    private String content;
    private String imgPath;
    private String time;

    public Remember(String myTitle, String myContent, String myImgPath, String myTime) {
        this.title = myTitle;
        this.content = myContent;
        this.imgPath = myImgPath;
        this.time = myTime;
    }
}
