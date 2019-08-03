package com.chen;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BaseCloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Program {
    private static void testSearch(BaseCloudSolrClient client) throws IOException, SolrServerException {
        Map<String, String> queryParamsMap = new HashMap<String, String>();
        queryParamsMap.put("q", "name: Foo");
        MapSolrParams queryParams = new MapSolrParams(queryParamsMap);
        QueryResponse response = client.query("localdocs" ,queryParams);
        SolrDocumentList documents = response.getResults();

        System.out.println("Found: " + documents.getNumFound() + " documents");
        for(SolrDocument document : documents) {
            for(String fieldName : document.getFieldNames()) {
                System.out.println(fieldName + ": " + document.getFieldValue(fieldName));
            }
            System.out.println("\n-----------------------------\n");
        }
    }

    private static void testIndexing(BaseCloudSolrClient client) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "2584"); // If unique id is match in collection, then it will update instead
        doc.addField("name", "Foo Bar");
        doc.addField("age", 14);
        doc.addField("height", 165);

        client.add("localdocs", doc);
        client.commit("localdocs"); // Indexed documents must be committed
    }

    public static void main(String args[]) throws Exception {

//        Http2SolrClient.Builder builder = new Http2SolrClient.Builder("http://localhost:8983/solr");
//        Http2SolrClient client = builder.build();
        CloudSolrClient.Builder builder = new CloudSolrClient.Builder();
        builder.withZkHost("localhost:9983");
        CloudSolrClient client = builder.build();
//        client.setDefaultCollection("localdocs");

        testIndexing(client);
        testSearch(client);
    }
}
