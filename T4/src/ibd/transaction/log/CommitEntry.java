/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.log;

import java.util.StringTokenizer;

/**
 *
 * @author pccli
 */
public class CommitEntry extends Entry{

    
    @Override
    public String write() {
        String aux = "";
        aux += "C";
        aux += ";";
        aux += getTransactionId();
        return aux;
    }

    @Override
    public void read(String text) {
        StringTokenizer tok = new StringTokenizer(text, ";");
        tok.nextToken();
        setTransactionId(Integer.valueOf(tok.nextToken()));
    }
    
}
