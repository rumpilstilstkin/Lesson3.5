package com.example.rumpilstilstkin.lesson5.depend;


public class UsePrinterMain {

    public void main(){
        InkColor color = new InkColor();
        Ink ink = new Ink(color);
        CreatorPaper creator = new CreatorPaper();
        Paper paper = new BlackPaper();


        Printer printer = new Printer(ink, paper);
        printer.printPage();
    }
}
