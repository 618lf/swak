package com.swak.mongo.codec;

import static java.util.Arrays.asList;
import static org.bson.assertions.Assertions.notNull;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.BsonBinarySubType;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.BsonTypeCodecMap;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.IdGenerator;
import org.bson.codecs.ObjectIdGenerator;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import com.swak.mongo.json.Document;

/**
 * 参考默认的实现
 * 
 * @author lifeng
 */
public class DocumentCodecx implements CollectibleCodec<Document> {

	private static final CodecRegistry DEFAULT_REGISTRY = fromProviders(
			asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DocumentCodecxProvider()));
	private static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap();

	private final BsonTypeCodecMap bsonTypeCodecMap;
	private final CodecRegistry registry;
	private final IdGenerator idGenerator;
	private final Transformer valueTransformer;

	/**
	 * Construct a new instance with a default {@code CodecRegistry}.
	 */
	public DocumentCodecx() {
		this(DEFAULT_REGISTRY);
	}

	/**
	 * Construct a new instance with the given registry.
	 *
	 * @param registry
	 *            the registry
	 * @since 3.5
	 */
	public DocumentCodecx(final CodecRegistry registry) {
		this(registry, DEFAULT_BSON_TYPE_CLASS_MAP);
	}

	/**
	 * Construct a new instance with the given registry and BSON type class map.
	 *
	 * @param registry
	 *            the registry
	 * @param bsonTypeClassMap
	 *            the BSON type class map
	 */
	public DocumentCodecx(final CodecRegistry registry, final BsonTypeClassMap bsonTypeClassMap) {
		this(registry, bsonTypeClassMap, null);
	}

	/**
	 * Construct a new instance with the given registry and BSON type class map. The
	 * transformer is applied as a last step when decoding values, which allows
	 * users of this codec to control the decoding process. For example, a user of
	 * this class could substitute a value decoded as a Document with an instance of
	 * a special purpose class (e.g., one representing a DBRef in MongoDB).
	 *
	 * @param registry
	 *            the registry
	 * @param bsonTypeClassMap
	 *            the BSON type class map
	 * @param valueTransformer
	 *            the value transformer to use as a final step when decoding the
	 *            value of any field in the document
	 */
	public DocumentCodecx(final CodecRegistry registry, final BsonTypeClassMap bsonTypeClassMap,
			final Transformer valueTransformer) {
		this.registry = notNull("registry", registry);
		this.bsonTypeCodecMap = new BsonTypeCodecMap(notNull("bsonTypeClassMap", bsonTypeClassMap), registry);
		this.idGenerator = new ObjectIdGenerator();
		this.valueTransformer = valueTransformer != null ? valueTransformer : new Transformer() {
			@Override
			public Object transform(final Object value) {
				return value;
			}
		};
	}

	@Override
	public boolean documentHasId(final Document document) {
		return document.containsKey(Document.ID_FIELD);
	}

	@Override
	public BsonValue getDocumentId(final Document document) {
		if (!documentHasId(document)) {
			throw new IllegalStateException("The document does not contain an _id");
		}

		Object id = document.get(Document.ID_FIELD);
		if (id instanceof BsonValue) {
			return (BsonValue) id;
		}

		BsonDocument idHoldingDocument = new BsonDocument();
		BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
		writer.writeStartDocument();
		writer.writeName(Document.ID_FIELD);
		writeValue(writer, EncoderContext.builder().build(), id);
		writer.writeEndDocument();
		return idHoldingDocument.get(Document.ID_FIELD);
	}

	@Override
	public Document generateIdIfAbsentFromDocument(final Document document) {
		if (!documentHasId(document)) {
			document.put(Document.ID_FIELD, ((ObjectId) idGenerator.generate()).toHexString());
		}
		return document;
	}

	@Override
	public void encode(final BsonWriter writer, final Document document, final EncoderContext encoderContext) {
		writeMap(writer, document, encoderContext);
	}

	@Override
	public Document decode(final BsonReader reader, final DecoderContext decoderContext) {
		Document document = new Document();

		reader.readStartDocument();
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			String fieldName = reader.readName();
			document.put(fieldName, readValue(reader, decoderContext));
		}

		reader.readEndDocument();

		return document;
	}

	@Override
	public Class<Document> getEncoderClass() {
		return Document.class;
	}

	private void beforeFields(final BsonWriter bsonWriter, final EncoderContext encoderContext,
			final Map<String, Object> document) {
		if (encoderContext.isEncodingCollectibleDocument() && document.containsKey(Document.ID_FIELD)) {
			bsonWriter.writeName(Document.ID_FIELD);
			writeValue(bsonWriter, encoderContext, document.get(Document.ID_FIELD));
		}
	}

	private boolean skipField(final EncoderContext encoderContext, final String key) {
		return encoderContext.isEncodingCollectibleDocument() && key.equals(Document.ID_FIELD);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeValue(final BsonWriter writer, final EncoderContext encoderContext, final Object value) {
		if (value == null) {
			writer.writeNull();
		} else if (value instanceof Iterable) {
			writeIterable(writer, (Iterable<Object>) value, encoderContext.getChildContext());
		} else if (value instanceof Map) {
			writeMap(writer, (Map<String, Object>) value, encoderContext.getChildContext());
		} else {
			Codec codec = registry.get(value.getClass());
			encoderContext.encodeWithChildContext(codec, writer, value);
		}
	}

	private void writeMap(final BsonWriter writer, final Map<String, Object> map, final EncoderContext encoderContext) {
		writer.writeStartDocument();

		beforeFields(writer, encoderContext, map);

		for (final Map.Entry<String, Object> entry : map.entrySet()) {
			if (skipField(encoderContext, entry.getKey())) {
				continue;
			}
			writer.writeName(entry.getKey());
			writeValue(writer, encoderContext, entry.getValue());
		}
		writer.writeEndDocument();
	}

	private void writeIterable(final BsonWriter writer, final Iterable<Object> list,
			final EncoderContext encoderContext) {
		writer.writeStartArray();
		for (final Object value : list) {
			writeValue(writer, encoderContext, value);
		}
		writer.writeEndArray();
	}

	private Object readValue(final BsonReader reader, final DecoderContext decoderContext) {
		BsonType bsonType = reader.getCurrentBsonType();
		if (bsonType == BsonType.NULL) {
			reader.readNull();
			return null;
		} else if (bsonType == BsonType.ARRAY) {
			return readList(reader, decoderContext);
		} else if (bsonType == BsonType.BINARY && BsonBinarySubType.isUuid(reader.peekBinarySubType())
				&& reader.peekBinarySize() == 16) {
			return registry.get(UUID.class).decode(reader, decoderContext);
		}
		return valueTransformer.transform(bsonTypeCodecMap.get(bsonType).decode(reader, decoderContext));
	}

	private List<Object> readList(final BsonReader reader, final DecoderContext decoderContext) {
		reader.readStartArray();
		List<Object> list = new ArrayList<Object>();
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			list.add(readValue(reader, decoderContext));
		}
		reader.readEndArray();
		return list;
	}
}
