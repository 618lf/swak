package com.swak.mongo.json;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class DocumentBsonAdapter implements Bson {

	private Document obj;

	public DocumentBsonAdapter(Document obj) {
		this.obj = obj;
	}

	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<>(obj, codecRegistry.get(Document.class));
	}

	public static DocumentBsonAdapter wrap(Document obj) {
		return new DocumentBsonAdapter(obj);
	}
}
