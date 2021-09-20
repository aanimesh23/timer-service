package com.animeshagrawal.timerservice.model;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.*;
import com.animeshagrawal.timerservice.constants.Constants;
import com.newrelic.api.agent.Trace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Offset {

    private long partitionId;
    private long currentOffset; //no of event processed till date
    private long endOffset; // no of total event to be processed
    private long lag;
    private long maxEpoch; //last epoch consumed only (1 thread 1 machine)
    private String lastGroupId; //last message (1 thread 1 machine)

    public void setEndOffset(long offset) {
        this.endOffset = offset;
        this.setLag();
    }
    public void incOffsetEnd() {
        this.endOffset = this.endOffset + 1;
        this.setLag();
    }

    public void incOffsetCurr() {
        this.currentOffset = this.currentOffset + 1;
        this.setLag();
    }

    public void setCurrentOffset(long offset) {
        this.currentOffset = offset;
        this.setLag();
    }

    public void setLag() {
        this.lag = this.endOffset - this.currentOffset;
        if(this.lag < 0) {
            this.endOffset = this.currentOffset;
            this.lag = 0;
        }
    }
    @Trace(dispatcher=true)
    public static Offset getOffset(int partition) {
        Condition partitionKey = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN(String.valueOf(partition)));
        Map<String, Condition> keyConditions = new HashMap<>();
        keyConditions.put("partition_id", partitionKey);

        QueryRequest request = new QueryRequest(Constants.OFFSET_DYNAMO_NAME);
        request.setSelect(Select.ALL_ATTRIBUTES);
        request.setKeyConditions(keyConditions);
        QueryResult result = Constants.DYNAMODB_CLIENT.query(request);
        ArrayList<Map<String, AttributeValue>> rows = new ArrayList<>(result.getItems());
        for (Map<String, AttributeValue> row : rows) {
            Offset ofs = new Offset();
            ofs.setPartitionId(Long.parseLong(row.get("partition_id").getN()));
            ofs.setCurrentOffset(Long.parseLong(row.get("current_offset").getN()));
            ofs.setEndOffset(Long.parseLong(row.get("end_offset").getN()));
            ofs.setLag();
            ofs.setMaxEpoch(Long.parseLong(row.get("max_epoch").getN()));
            ofs.setLastGroupId(row.get("last_group_id").getS());
            return ofs;
        }
        return null;
    }
    @Trace(dispatcher=true)
    public void setOffset() {
        Item item = new Item();
        item.with("partition_id", this.partitionId);
        item.with("current_offset", this.getCurrentOffset());
        item.with("end_offset", this.getEndOffset());
        item.with("lag", this.getLag());
        item.with("max_epoch", this.getMaxEpoch());
        item.with("last_group_id", this.getLastGroupId());
        Constants.DYNAMODB.getTable(Constants.OFFSET_DYNAMO_NAME).putItem(item);
    }

    public static void main(String[] args) {
        getOffset(1);
    }
}
