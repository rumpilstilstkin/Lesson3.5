package com.example.rumpilstilstkin.lesson5.depend;


public class WhitePaper implements Paper{

    private CreatorPaper creator;

    public WhitePaper(CreatorPaper creator){
        this.creator = creator;
    }

    @Override
    public String getPage() {
        return "page";
    }

    @Override
    public String getLine() {
        return line;
    }
}
