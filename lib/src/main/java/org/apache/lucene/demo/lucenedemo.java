package org.apache.lucene.demo;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import junit.framework.TestCase;



import sun.nio.ch.IOStatus;

public class lucenedemo extends TestCase{
//	public static void main(String args[]){
//		String indexDir=null;
//		String docDir=null;
//		
//	}
	protected String ids[]={"1","2"};
	protected String unindexed[]={"Netherlands","Italy"};
	protected String unsorted[]={"Amsterdam has lots of bridges","Venice has lots of cancels"};
	protected String text[]={"Amsterdam","Venice"};
	private Directory directory=new RAMDirectory();
	protected void setUp() throws Exception{
		System.out.println("setup");
		IndexWriter indexWriter=getWriter();
		for(int i=0;i<ids.length;i++){
			Document doc=new Document();
			doc.add(new Field("id",ids[i],Field.Store.YES,Field.Index.NOT_ANALYZED));
			doc.add(new Field("country",unindexed[i],Field.Store.YES,Field.Index.NO));
			doc.add(new Field("contents",unsorted[i],Field.Store.NO,Field.Index.ANALYZED));
			doc.add(new Field("city",text[i],Field.Store.YES,Field.Index.ANALYZED));
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	public void testA(){
		System.out.println("ok");
	}

	private IndexWriter getWriter() throws IOException {
		Analyzer analyzer=new WhitespaceAnalyzer();
		IndexWriterConfig iwf=new IndexWriterConfig(analyzer);
		IndexWriter indexwriter=new IndexWriter(directory,iwf);
		return indexwriter;
	}
	protected int getHitCount(String fieldName,String searchString) throws IOException{
		IndexReader indexReader=DirectoryReader.open(directory);
		IndexSearcher searcher=new IndexSearcher(indexReader);
		Term t=new Term(fieldName,searchString);
		//TestUtil.hitCount();
		Query query=new TermQuery(t);
		int hits=searcher.search(query,10).totalHits;
		return hits;
	}
	public void testIndexWriter() throws IOException{
		IndexWriter indexWriter=getWriter();
		assertEquals(ids.length,indexWriter.numDocs());
		indexWriter.close();
		System.out.println("writer ok");
	}
	public void testIndexReader() throws IOException{
		IndexReader reader=DirectoryReader.open(directory);
		assertEquals(ids.length,reader.numDocs());
		assertEquals(ids.length, reader.maxDoc());
		reader.close();
	}
	public void testHits() throws  IOException{
		System.out.println(getHitCount("contents", "bridges"));
	}
	public void testDeleteBeforeOptimiza() throws IOException{
		Analyzer analyzer=new WhitespaceAnalyzer();
		IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
		IndexWriter writer=new IndexWriter(directory,iwc);
		System.out.println(writer.numDocs());
		assertEquals(2,writer.numDocs());
		writer.deleteDocuments(new Term("id","1"));
		writer.commit();
	}
}
