package tests;

import auxiliary.Alphabet;
import auxiliary.Dictionary;

public class dictionaryTest {

    public static void main(String[] args) {
        String text = "\t\t\tencapsulation german poland kombat waffele whom? leg character terms else camel rabbit fire text if they" +
                " element sky item robot! past dune dolphine then it am quality eye moon system folder light letter number# notch allies dog word hash why? gun pink" +
                " what? yellow sea see noon file top does ear can't table foot hand a strike black i electricity pistol kill boat democracy off battle" +
                " single fly component blue watch reflector future machine progress atom under inferno voice rotor tonight mouce door won't data use" +
                " doom sign screen do bomb red later whale white hello? than which england morning! dirt tree thrown water trash ranch she sand squad" +
                " desk present where? magenta game code enigma plural death vegtable fruit for their privacy back house fox not hair napkin and street cat now of iteration class trike fight mortal live how? on patrol" +
                " keyboard midnight or green umbrella wheel saw orange airplane window front he\n";

        String exclude = "!'#?";

        Alphabet abc = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ !?'");
        Dictionary dict = new Dictionary(text,exclude, abc);



        System.out.println(dict.getWordSuggestions("ma"));




    }
}
