package com.swak.mongo.codec;

import static org.bson.assertions.Assertions.notNull;

import org.bson.Document;
import org.bson.Transformer;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.CodeWithScopeCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.CodeWithScope;

public class DocumentCodecxProvider implements CodecProvider {

	private final BsonTypeClassMap bsonTypeClassMap;
	private final Transformer valueTransformer;

	/**
	 * Construct a new instance with a default {@code BsonTypeClassMap}.
	 */
	public DocumentCodecxProvider() {
		this(new BsonTypeClassMap());
	}

	/**
	 * Construct a new instance with a default {@code BsonTypeClassMap} and the
	 * given {@code Transformer}. The transformer is used by the DocumentCodec as a
	 * last step when decoding values.
	 *
	 * @param valueTransformer
	 *            the value transformer for decoded values
	 * @see org.bson.codecs.DocumentCodec#DocumentCodec(org.bson.codecs.configuration.CodecRegistry,
	 *      BsonTypeClassMap, org.bson.Transformer)
	 */
	public DocumentCodecxProvider(final Transformer valueTransformer) {
		this(new BsonTypeClassMap(), valueTransformer);
	}

	/**
	 * Construct a new instance with the given instance of {@code BsonTypeClassMap}.
	 *
	 * @param bsonTypeClassMap
	 *            the non-null {@code BsonTypeClassMap} with which to construct
	 *            instances of {@code DocumentCodec} and {@code
	 *                         ListCodec}
	 */
	public DocumentCodecxProvider(final BsonTypeClassMap bsonTypeClassMap) {
		this(bsonTypeClassMap, null);
	}

	/**
	 * Construct a new instance with the given instance of {@code BsonTypeClassMap}.
	 *
	 * @param bsonTypeClassMap
	 *            the non-null {@code BsonTypeClassMap} with which to construct
	 *            instances of {@code DocumentCodec} and {@code
	 *                         ListCodec}.
	 * @param valueTransformer
	 *            the value transformer for decoded values
	 */
	public DocumentCodecxProvider(final BsonTypeClassMap bsonTypeClassMap, final Transformer valueTransformer) {
		this.bsonTypeClassMap = notNull("bsonTypeClassMap", bsonTypeClassMap);
		this.valueTransformer = valueTransformer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
		if (clazz == CodeWithScope.class) {
			return (Codec<T>) new CodeWithScopeCodec(registry.get(Document.class));
		}

		if (clazz == Document.class) {
			return (Codec<T>) new DocumentCodecx(registry, bsonTypeClassMap, valueTransformer);
		}

		return null;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DocumentCodecxProvider that = (DocumentCodecxProvider) o;

		if (!bsonTypeClassMap.equals(that.bsonTypeClassMap)) {
			return false;
		}
		if (valueTransformer != null ? !valueTransformer.equals(that.valueTransformer)
				: that.valueTransformer != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = bsonTypeClassMap.hashCode();
		result = 31 * result + (valueTransformer != null ? valueTransformer.hashCode() : 0);
		return result;
	}
}
