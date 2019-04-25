package mx.iteso.desi.cloud.lp1;

import java.io.IOException;
import java.util.HashSet;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.ParseTriples;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;
import mx.iteso.desi.cloud.keyvalue.Triple;

public class IndexImages {
  ParseTriples parser;
  IKeyValueStorage imageStore, titleStore;

  public IndexImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) {
    this.imageStore = imageStore;
    this.titleStore = titleStore;
  }

  public void run(String imageFileName, String titleFileName) throws IOException
  {

    ParseTriples images = new ParseTriples("./dbd/" + imageFileName);
    ParseTriples labels = new ParseTriples("./dbd/" + titleFileName);

    Triple tripleImages = images.getNextTriple();
    Triple tripleLabels = labels.getNextTriple();

    String[] partsImages;
    String[] partsLabels;
    String[] partsLabelsObject;

    String imageSubject;
    String imageObject;
    String labelSubject;
    String labelObject;

    String labelStemmer;

    String lowerPartsImage;

    // Imagenes
    while (tripleImages != null) {
      if (tripleImages.getPredicate().equals("http://xmlns.com/foaf/0.1/depiction")) {
        imageSubject = tripleImages.getSubject().toLowerCase();
        partsImages = imageSubject.split("/");
        lowerPartsImage = partsImages[4].toLowerCase();

        if (lowerPartsImage.matches("^"+Config.filter.toLowerCase()+".*$")) {
          imageObject = tripleImages.getObject().toLowerCase();
          System.out.println("ImgSubj: " + imageSubject);
          System.out.println("ImgObjc: " + imageObject + "\n");
          imageStore.put(imageSubject, imageObject);
        }
      }
      tripleImages = images.getNextTriple();
    }

    // Labels
    while (tripleLabels != null) {

      if (tripleLabels.getPredicate().equals("http://www.w3.org/2000/01/rdf-schema#label")) {

        labelObject = tripleLabels.getObject();

        if (labelObject.matches("^"+Config.filter+".*")) {

          labelSubject = tripleLabels.getSubject().toLowerCase();
          partsLabels = labelObject.split("/");
          partsLabelsObject = partsLabels[0].split(" ");

          for (int i = 0; i < partsLabelsObject.length; i++) {
            labelStemmer = PorterStemmer.stem(partsLabelsObject[i].toLowerCase());
            System.out.println("Key: " + labelStemmer);
            System.out.println("Value: " + labelSubject + "\n");
            titleStore.put(labelStemmer, labelSubject);
          }
        }
      }

      tripleLabels = labels.getNextTriple();
    }

    close();

  }

  public void close() {
    imageStore.close();
    titleStore.close();
  }

  public static void main(String args[])
  {
    System.out.println("*** Alumno: _____________________ (Exp: _________ )");
    try {

      IKeyValueStorage imageStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,
              "images");
      IKeyValueStorage titleStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType,
              "terms");


      IndexImages indexer = new IndexImages(imageStore, titleStore);
      indexer.run(Config.imageFileName, Config.titleFileName);
      System.out.println("Indexing completed");

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed to complete the indexing pass -- exiting");
    }
  }
}

