/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction;

import ibd.table.record.AbstractRecord;

/**
 *
 * @author pccli
 */
public class FakeRecord extends AbstractRecord{

    Integer id;
    Integer blockId;
    Long pk;
    String content;
    
    public void setRecordId(Integer id) {
        this.id = id;
    }
    
    @Override
    public Integer getRecordId() {
        return id;
    }

    @Override
    public Integer getBlockId() {
        //return blockId;
        return -1;
    }

    @Override
    public Long getPrimaryKey() {
        return pk;
    }
    
    public void setPrimaryKey(Long pk) {
        this.pk = pk;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    
    
}
