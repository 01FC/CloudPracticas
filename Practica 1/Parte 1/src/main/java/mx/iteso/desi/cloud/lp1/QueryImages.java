package mx.iteso.desi.cloud.lp1;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;

public class QueryImages {
  IKeyValueStorage imageStore;
  IKeyValueStorage titleStore;
	
  public QueryImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) 
  {
	  this.imageStore = imageStore;
	  this.titleStore = titleStore;
  }
	
  public Set<String> query(String word)
  {
    HashSet<String> imgQuerySet = new HashSet<String>();

    titleStore.get(PorterStemmer.stem(word.toLowerCase())).forEach((imgLabel) ->
            imageStore.get(imgLabel).forEach((path) ->
                    imgQuerySet.add(path)));

    return imgQuerySet;
  }
        
  public void close()
  {
    imageStore.close();
    titleStore.close();
  }
	
  public static void main(String args[]) 
  {

    System.out.println("*** Alumno: _____________________ (Exp: _________ )");
    

    IKeyValueStorage imageStore = null;
    IKeyValueStorage titleStore = null;
    try {
      imageStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,"images");
      titleStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,"terms");
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    QueryImages myQuery = new QueryImages(imageStore, titleStore);

    for (int i=0; i<args.length; i++) {
      System.out.println(args[i]+":");
      Set<String> result = myQuery.query(args[i]);
      Iterator<String> iter = result.iterator();
      while (iter.hasNext()) 
        System.out.println("  - "+iter.next());
    }
    
    myQuery.close();
  }
}

