package ibd.table.management;

import ibd.table.Table;
import ibd.table.block.Block;
import ibd.table.record.Record;

public class HeapTableJoaoMazzarolo extends Table {

    public HeapTableJoaoMazzarolo(String folder, String name, int pageSize, boolean override) throws Exception {
        super(folder, name, pageSize, override);
    }

    @Override
    protected Record addRecord(Record record) throws Exception {
        Block b = findHeapBlock(record);
        if (b == null) {
            b = addBlock();
        }
        
        Record rec = addRecord(b, record);
        
        updateHeapListOnInsertion(b, rec);
        
        return rec;
    }

    @Override
    protected Record removeRecord(Block block, Record record) throws Exception {
        record = super.removeRecord(block, record);
        if (record == null) {
            return null;
        }
        
        if(block.next_heap_block_id == -1 && getFirstHeapBlock() != block.getPageID()) {
            updateHeapListOnRemoval(block, record);
        }

        return record;
    }

    private Block findHeapBlock(Record record) throws Exception {
        int blockId = getFirstHeapBlock();
        if (blockId != -1) {
            return getBlock(blockId);
        }
        return null;
    }

    private void updateHeapListOnInsertion(Block b, Record record) throws Exception {
        if(getFirstHeapBlock() == -1) {
            setFirstHeapBlock(b.getPageID());
            setLastHeapBlock(b.getPageID());
        } else if(!b.fits(410)) {
            setFirstHeapBlock(b.next_heap_block_id);
            if(b.next_heap_block_id != -1) {
                Block nextBlock = getBlock(b.next_heap_block_id);
                nextBlock.prev_heap_block_id = -1;
                b.next_heap_block_id = -1;
                dataFile.writePage(nextBlock);
            } else {
                setLastHeapBlock(-1);
            }
        } else {
            return;
        }
        dataFile.writePage(b);
    }

    private void updateHeapListOnRemoval(Block b, Record record) throws Exception {
        if(getLastHeapBlock() == -1) {
            setFirstHeapBlock(b.getPageID());
            setLastHeapBlock(b.getPageID());
        } else {
            Block lastBlock = getBlock(getLastHeapBlock());
            lastBlock.next_heap_block_id = b.getPageID();
            b.prev_heap_block_id = lastBlock.getPageID();
            setLastHeapBlock(b.getPageID());
            dataFile.writePage(lastBlock);
        }
        dataFile.writePage(b);
    }
}
