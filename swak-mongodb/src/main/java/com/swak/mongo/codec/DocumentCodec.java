package com.swak.mongo.codec;

import static com.swak.mongo.json.Document.BINARY_FIELD;
import static com.swak.mongo.json.Document.DATE_FIELD;
import static com.swak.mongo.json.Document.ID_FIELD;
import static com.swak.mongo.json.Document.OID_FIELD;
import static com.swak.mongo.json.Document.TIMESTAMP_FIELD;
import static com.swak.mongo.json.Document.TIMESTAMP_INCREMENT_FIELD;
import static com.swak.mongo.json.Document.TIMESTAMP_TIME_FIELD;
import static com.swak.mongo.json.Document.TYPE_FIELD;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONObject;
import com.swak.mongo.json.Document;
import com.swak.mongo.json.Documents;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class DocumentCodec extends AbstractDocumentCodec<Document, Documents> implements CollectibleCodec<Document> {

	private boolean useObjectId = false;

	public DocumentCodec(boolean useObjectId) {
		this.useObjectId = useObjectId;
	}

	@Override
	public Document generateIdIfAbsentFromDocument(Document json) {

		if (!documentHasId(json)) {
			String value = generateHexObjectId();
			if (useObjectId)
				json.put(ID_FIELD, new Document().put(OID_FIELD, value));
			else
				json.put(ID_FIELD, value);
		}
		return json;
	}

	public static String generateHexObjectId() {
		ObjectId id = new ObjectId();
		return id.toHexString();
	}

	@Override
	public boolean documentHasId(Document json) {
		return json.containsKey(ID_FIELD);
	}

	@Override
	public BsonValue getDocumentId(Document json) {
		if (!documentHasId(json)) {
			throw new IllegalStateException("The document does not contain an _id");
		}

		Object id = json.get(ID_FIELD);
		if (id instanceof String) {
			return new BsonString((String) id);
		}

		BsonDocument idHoldingDocument = new BsonDocument();
		BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
		writer.writeStartDocument();
		writer.writeName(ID_FIELD);
		writeValue(writer, null, id, EncoderContext.builder().build());
		writer.writeEndDocument();
		return idHoldingDocument.get(ID_FIELD);
	}

	@Override
	public Class<Document> getEncoderClass() {
		return Document.class;
	}

	@Override
	protected boolean isObjectIdInstance(Object instance) {
		if (instance instanceof Document && ((Document) instance).containsKey(OID_FIELD)) {
			return true;
		}
		return false;
	}

	@Override
	protected void beforeFields(Document object, BiConsumer<String, Object> objectConsumer) {
		if (object.containsKey(ID_FIELD)) {
			objectConsumer.accept(ID_FIELD, object.get(ID_FIELD));
		}
	}

	@Override
	protected Document newObject() {
		return new Document();
	}

	@Override
	protected void add(Document object, String name, Object value) {
		object.put(name, value);
	}

	@Override
	protected boolean isObjectInstance(Object instance) {
		return instance instanceof Document;
	}

	@Override
	protected void forEach(Document object, BiConsumer<String, Object> objectConsumer) {
		object.forEach((k, v) -> {
			objectConsumer.accept(k, v);
		});
	}

	@Override
	protected Documents newArray() {
		return new Documents();
	}

	@Override
	protected void add(Documents array, Object value) {
		if (value != null) {
			array.add(value);
		} else {
			array.add(null);
		}
	}

	@Override
	protected boolean isArrayInstance(Object instance) {
		return instance instanceof Documents;
	}

	@Override
	protected void forEach(Documents array, Consumer<Object> arrayConsumer) {
		array.forEach(arrayConsumer);
	}

	@Override
	protected BsonType getBsonType(Object value) {
		BsonType type = super.getBsonType(value);
		if (type == BsonType.DOCUMENT) {
			Document obj = (Document) value;
			if (obj.containsKey(DATE_FIELD)) {
				return BsonType.DATE_TIME;
			} else if (obj.containsKey(OID_FIELD)) {
				return BsonType.OBJECT_ID;
			} else if (obj.containsKey(BINARY_FIELD)) {
				return BsonType.BINARY;
			} else if (obj.containsKey(TIMESTAMP_FIELD)) {
				return BsonType.TIMESTAMP;
			}
		}
		return type;
	}

	// ---------- Support additional mappings

	@Override
	protected Object readObjectId(BsonReader reader, DecoderContext ctx) {
		return new Document().put(OID_FIELD, reader.readObjectId().toHexString());
	}

	@Override
	protected void writeObjectId(BsonWriter writer, String name, Object value, EncoderContext ctx) {
		Document json = (Document) value;
		ObjectId objectId = new ObjectId(json.getString(OID_FIELD));
		writer.writeObjectId(objectId);
	}

	@Override
	protected Object readDateTime(BsonReader reader, DecoderContext ctx) {
		final Document result = new Document();
		result.put(DATE_FIELD, OffsetDateTime.ofInstant(Instant.ofEpochMilli(reader.readDateTime()), ZoneOffset.UTC)
				.format(ISO_OFFSET_DATE_TIME));
		return result;
	}

	@Override
	protected void writeDateTime(BsonWriter writer, String name, Object value, EncoderContext ctx) {
		writer.writeDateTime(OffsetDateTime.parse(((Document) value).getString(DATE_FIELD)).toInstant().toEpochMilli());
	}

	@Override
	protected Object readBinary(BsonReader reader, DecoderContext ctx) {
		final Document result = new Document();
		BsonBinary bsonBinary = reader.readBinaryData();
		result.fluentPut(BINARY_FIELD, bsonBinary.getData()).fluentPut(TYPE_FIELD, bsonBinary.getType());
		return result;
	}

	@Override
	protected void writeBinary(BsonWriter writer, String name, Object value, EncoderContext ctx) {
		Document binaryDocument = (Document) value;
		byte type = Optional.ofNullable(binaryDocument.getInteger(TYPE_FIELD)).map(Integer::byteValue)
				.orElse(BsonBinarySubType.BINARY.getValue());
		final BsonBinary bson = new BsonBinary(type, binaryDocument.getBytes(BINARY_FIELD));
		writer.writeBinaryData(bson);
	}

	@Override
	protected Object readTimeStamp(BsonReader reader, DecoderContext ctx) {
		final Document result = new Document();
		final Document timeStampComponent = new Document();

		final BsonTimestamp bson = reader.readTimestamp();

		timeStampComponent.put(TIMESTAMP_TIME_FIELD, bson.getTime());
		timeStampComponent.put(TIMESTAMP_INCREMENT_FIELD, bson.getInc());

		result.put(TIMESTAMP_FIELD, timeStampComponent);

		return result;
	}

	@Override
	protected void writeTimeStamp(BsonWriter writer, String name, Object value, EncoderContext ctx) {
		final JSONObject timeStamp = ((Document) value).getJSONObject(TIMESTAMP_FIELD);

		final BsonTimestamp bson = new BsonTimestamp(timeStamp.getInteger(TIMESTAMP_TIME_FIELD),
				timeStamp.getInteger(TIMESTAMP_INCREMENT_FIELD));

		writer.writeTimestamp(bson);
	}

}