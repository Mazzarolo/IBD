/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.log;

import ibd.table.Directory;
import ibd.table.Table;
import java.util.StringTokenizer;

/**
 *
 * @author pccli
 */
public class WriteEntry extends Entry{
    
    public Table table;
    public long pk;
    public String oldValue;
    public String newValue;

    @Override
    public String write() {
        String aux = "";
        aux += "W";
        aux += ";";
        aux += getTransactionId();
        aux += ";";
        aux += table.key;
        aux += ";";
        aux += pk;
        aux += ";";
        aux += oldValue;
        aux += ";";
        aux += newValue;
        return aux;
    }

    @Override
    public void read(String text) throws Exception{
        StringTokenizer tok = new StringTokenizer(text, ";");
        tok.nextToken();
        setTransactionId(Integer.valueOf(tok.nextToken()));
        
        String key = tok.nextToken();
        ///String folder = Table.getTableFolder(key);
        //String file = Table.getTableFile(key);
        table = Directory.getTable(key,Table.DEFULT_PAGE_SIZE, false);
        pk = Long.valueOf(tok.nextToken());
        oldValue = tok.nextToken();
        newValue = tok.nextToken();
    }
}
