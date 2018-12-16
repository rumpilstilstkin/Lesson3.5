package com.example.rumpilstilstkin.lesson5.depend;


import android.util.Log;


public class Printer {
    private Ink ink;
    private Paper paper;

    public Printer(Ink ink, Paper paper){
        this.ink = ink;
        this.paper = paper;
    }

    public void printPage(){
        Log.d("Dto", paper.getLine());
    }
}
