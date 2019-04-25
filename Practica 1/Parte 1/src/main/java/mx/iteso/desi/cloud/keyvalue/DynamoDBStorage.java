package mx.iteso.desi.cloud.keyvalue;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import mx.iteso.desi.cloud.lp1.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class DynamoDBStorage extends BasicKeyValueStore {

    String dbName;
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Config.amazonRegion).build();
    DynamoDB dynamoDB = new DynamoDB(client);

    // Simple autoincrement counter to make sure we have unique entries
    int inx;

    Set<String> attributesToGet = new HashSet<String>();

    public DynamoDBStorage(String dbName) {

        try {
            System.out.println("Looking for table " + dbName);
            ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
            ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<>();

            keySchema.add(new KeySchemaElement().withAttributeName("Keyword").withKeyType(KeyType.HASH));
            keySchema.add(new KeySchemaElement().withAttributeName("inx").withKeyType(KeyType.RANGE));

            attributeDefinitions.add(new AttributeDefinition().withAttributeName("Keyword").withAttributeType("S"));
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("inx").withAttributeType("N"));

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(dbName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(1L)
                            .withWriteCapacityUnits(1L));

            Table table = dynamoDB.createTable(request);
            table.waitForActive();
            this.dbName = dbName;

        }
        catch (ResourceInUseException e){
            this.dbName = dbName;
            System.out.println("Table already exists");
            e.getStackTrace();
        }
        catch (Exception e){
            this.dbName = dbName;
            e.getStackTrace();
        }
    }

    @Override
    public Set<String> get(String search) {
        Set<String> items = new HashSet<>();

        Table table = dynamoDB.getTable(dbName);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("Keyword = :v_id")
                .withValueMap(new ValueMap().withString(":v_id", search));

        ItemCollection<QueryOutcome> queryresult = table.query(spec);
        Iterator<Item> iterator = queryresult.iterator();

        while (iterator.hasNext()){
            items.add(iterator.next().get("Value").toString());
        }

        return items;
    }

    @Override
    public boolean exists(String search) {
        Table table = dynamoDB.getTable(dbName);
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("Keyword = :v_id")
                .withValueMap(new ValueMap().withString(":v_id", search));

        return  table.query(spec).iterator().hasNext();
    }

    @Override
    public Set<String> getPrefix(String search) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addToSet(String keyword, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void put(String keyword, String value) {
        Table table = dynamoDB.getTable(dbName);
        try {
            Item item = new Item();//.withString("key" , keyword).withString("Value" , value).withInt("inx",inx);
            item.withPrimaryKey("inx",inx).withString("Value",value).withString("Keyword",keyword);
            table.putItem(item);
            System.out.println("uploaded "+inx);
            inx++;
        }
        catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());

        }
    }

    @Override
    public void close() {
        dynamoDB.shutdown();
        System.out.println("Connection to " + dbName + " has been closed");
    }

    @Override
    public boolean supportsPrefixes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sync() {
    }

    @Override
    public boolean isCompressible() {
        return false;
    }

    @Override
    public boolean supportsMoreThan256Attributes() {
        return true;
    }

}
